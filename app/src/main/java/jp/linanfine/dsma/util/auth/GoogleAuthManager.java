package jp.linanfine.dsma.util.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jp.linanfine.dsma.BuildConfig;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleAuthManager {
    public static final int RC_SIGN_IN = 9001;

    private static final String TAG = "GoogleAuthManager";
    private static final String BASE_URL = "https://fnapi.fia-s.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static GoogleAuthManager instance;
    private final OkHttpClient client;
    private GoogleSignInClient googleSignInClient;
    private GoogleAuthCallback callback;

    // シングルトンパターン
    public static synchronized GoogleAuthManager getInstance() {
        if (instance == null) {
            instance = new GoogleAuthManager();
        }
        return instance;
    }

    private GoogleAuthManager() {
        client = new OkHttpClient();
    }

    // Googleサインインクライアントの初期化
    public void initialize(Context context) {
        Log.d(TAG, "Using Google Client ID: " + BuildConfig.GOOGLE_CLIENT_ID);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // Googleサインインを開始
    public void signIn(Activity activity, GoogleAuthCallback callback) {
        this.callback = callback;
        Log.d(TAG, "Starting Google Sign In");
        Intent signInIntent = googleSignInClient.getSignInIntent();
        Log.d(TAG, "Created sign in intent: " + signInIntent);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleSignInResult(Intent data) {
        try {
            Log.d(TAG, "Handling sign in result...");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            // 追加デバッグログ（TAGを使用）
            if (task.isSuccessful()) {
                Log.d(TAG, "Task is successful");
            } else {
                Exception e = task.getException();
                Log.e(TAG, "Task failed with exception: " + (e != null ? e.getMessage() : "null"));
            }

            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null && account.getIdToken() != null) {
                Log.d(TAG, "Sign in success, id token: " + account.getIdToken().substring(0, 10) + "...");
                callback.onSuccess(account.getIdToken(), account);
            } else {
                Log.w(TAG, "Sign in failed: account or token is null");
                callback.onFailure("アカウント情報の取得に失敗しました");
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed: " + e.getStatusCode() + ", " + e.getMessage(), e);
            callback.onFailure("Google認証に失敗しました: " + e.getStatusCode());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in sign in handling", e);
            callback.onFailure("認証処理中に予期せぬエラーが発生しました: " + e.getMessage());
        }
    }

    // Googleアカウントからユーザーを検索
    public void findPlayerByGoogleToken(String idToken, final FindPlayerCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("googleToken", idToken);
        } catch (JSONException e) {
            callback.onFailure("JSONの作成に失敗しました: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/find-player-by-google")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("ネットワークエラー: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("サーバーエラー: " + response.code());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.optBoolean("success", false);
                    boolean found = jsonResponse.optBoolean("found", false);
                    String message = "";

                    if (success && found) {
                        JSONObject playerObj = jsonResponse.getJSONObject("player");
                        String id = playerObj.getString("id");
                        String name = playerObj.getString("name");
                        Player player = new Player(id, name);
                        message = "Googleアカウントで認証され、ユーザー「" + name + "」としてログインしました";
                        callback.onSuccess(true, found, player, message);
                    } else if (success) {
                        message = "Googleアカウントに紐づけられたユーザーが見つかりませんでした。新規ユーザー登録を行ってください。";
                        callback.onSuccess(true, false, null, message);
                    } else {
                        String error = jsonResponse.optString("error", "不明なエラー");
                        callback.onFailure(error);
                    }
                } catch (JSONException e) {
                    callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                }
            }
        });
    }

    // ユーザーとGoogleアカウントを紐づける
    public void connectUserWithGoogleToken(String playerId, String idToken, final ConnectGoogleCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("playerId", playerId);
            jsonBody.put("googleToken", idToken);
        } catch (JSONException e) {
            callback.onFailure("JSONの作成に失敗しました: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/connect-google")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("ネットワークエラー: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("サーバーエラー: " + response.code());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.optBoolean("success", false);

                    if (success) {
                        Player player = null;
                        if (jsonResponse.has("player")) {
                            JSONObject playerObj = jsonResponse.getJSONObject("player");
                            String id = playerObj.getString("id");
                            String name = playerObj.getString("name");
                            player = new Player(id, name);
                        }
                        String message = "Googleアカウントとの連携が完了しました";
                        callback.onSuccess(player, message);
                    } else {
                        String error = jsonResponse.optString("error", "不明なエラー");
                        callback.onFailure(error);
                    }
                } catch (JSONException e) {
                    callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                }
            }
        });
    }

    // Googleアカウントとの連携を解除
    public void unlinkGoogleFromPlayer(String playerId, final UnlinkGoogleCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("playerId", playerId);
        } catch (JSONException e) {
            callback.onFailure("JSONの作成に失敗しました: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/auth/unlink-google")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("ネットワークエラー: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("サーバーエラー: " + response.code());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean success = jsonResponse.optBoolean("success", false);

                    if (success) {
                        String message = jsonResponse.optString("message", "Googleアカウントとの連携を解除しました");
                        callback.onSuccess(message);
                    } else {
                        String error = jsonResponse.optString("error", "不明なエラー");
                        callback.onFailure(error);
                    }
                } catch (JSONException e) {
                    callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                }
            }
        });
    }

    // ユーザーの存在確認とGoogle連携状態を検証
    public void validateUser(String userId, final ValidateUserCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", userId);
        } catch (JSONException e) {
            callback.onFailure("JSONの作成に失敗しました: " + e.getMessage());
            return;
        }

        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url(BASE_URL + "/validate-user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("ネットワークエラー: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onFailure("サーバーエラー: " + response.code());
                    return;
                }

                String responseBody = response.body() != null ? response.body().string() : "{}";
                try {
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    boolean exists = jsonResponse.optBoolean("exists", false);
                    boolean isGoogleLinked = jsonResponse.optBoolean("isGoogleLinked", false);

                    callback.onSuccess(exists, isGoogleLinked, null);
                } catch (JSONException e) {
                    callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                }
            }
        });
    }

    // Google連携情報の保存
    public void saveGoogleLinkStatus(Context context, String playerId, boolean isLinked) {
        SharedPreferences prefs = context.getSharedPreferences("FlareNotePrefs", Context.MODE_PRIVATE);
        prefs.edit().putBoolean("isGoogleLinked_" + playerId, isLinked).apply();
    }

    // Google連携情報の取得
    public boolean getGoogleLinkStatus(Context context, String playerId) {
        SharedPreferences prefs = context.getSharedPreferences("FlareNotePrefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("isGoogleLinked_" + playerId, false);
    }

    // コールバックインターフェース
    public interface GoogleAuthCallback {
        void onSuccess(String idToken, GoogleSignInAccount account);
        void onFailure(String error);
    }

    public interface FindPlayerCallback {
        void onSuccess(boolean success, boolean found, Player player, String message);
        void onFailure(String error);
    }

    public interface ConnectGoogleCallback {
        void onSuccess(Player player, String message);
        void onFailure(String error);
    }

    public interface UnlinkGoogleCallback {
        void onSuccess(String message);
        void onFailure(String error);
    }

    public interface ValidateUserCallback {
        void onSuccess(boolean exists, boolean isGoogleLinked, String message);
        void onFailure(String error);
    }

    // Playerモデルクラス
    public static class Player {
        private final String id;
        private final String name;

        public Player(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
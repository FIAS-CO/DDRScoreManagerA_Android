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
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jp.linanfine.dsma.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoogleAuthManager {
    private static final String TAG = "GoogleAuthManager";
    public static final int RC_SIGN_IN = 9001;
    private static final String BASE_URL = "https://fnapi.fia-s.com/api";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static GoogleAuthManager instance;
    private GoogleSignInClient mGoogleSignInClient;
    private OkHttpClient httpClient;
    private SharedPreferences prefs;

    private GoogleAuthManager() {
        httpClient = new OkHttpClient();
    }

    public static GoogleAuthManager getInstance() {
        if (instance == null) {
            instance = new GoogleAuthManager();
        }
        return instance;
    }

    public void init(Context context, String clientId) {
        // Google Sign-Inの設定
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        prefs = context.getSharedPreferences("FlareNotePrefs", Context.MODE_PRIVATE);
    }

    /**
     * Google認証を開始する
     */
    public void startGoogleSignIn(Activity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * ActivityResultの処理
     */
    public GoogleSignInAccount processSignInResult(Intent data) throws ApiException {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            return task.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed: status code=" + e.getStatusCode() +
                    ", message=" + e.getMessage() +
                    ", status=" + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
            throw e;
        }
    }
    /**
     * Googleアカウントからユーザーを検索
     */
    public void findUserByGoogleToken(String idToken, final ApiCallback<FindUserResult> callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("googleToken", idToken);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/find-player-by-google")
                    .post(RequestBody.create(JSON, requestBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("サーバーエラー: " + response.code());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        FindUserResult result = new FindUserResult();
                        result.success = json.optBoolean("success", false);
                        result.found = json.optBoolean("found", false);

                        if (result.success && result.found) {
                            JSONObject playerJson = json.getJSONObject("player");
                            result.player = new User(
                                    playerJson.getString("id"),
                                    playerJson.getString("name")
                            );
                        }

                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    /**
     * ユーザーとGoogleアカウントを紐づける
     */
    public void connectUserWithGoogle(String userId, String idToken, final ApiCallback<User> callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("playerId", userId);
            requestBody.put("googleToken", idToken);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/connect-google")
                    .post(RequestBody.create(JSON, requestBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("サーバーエラー: " + response.code());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        boolean success = json.optBoolean("success", false);
                        if (success && json.has("player")) {
                            JSONObject playerJson = json.getJSONObject("player");
                            User user = new User(
                                    playerJson.getString("id"),
                                    playerJson.getString("name")
                            );
                            callback.onSuccess(user);
                        } else {
                            String error = json.optString("error", "不明なエラー");
                            callback.onFailure(error);
                        }
                    } catch (JSONException e) {
                        callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    /**
     * Googleアカウントとの紐づけを解除
     */
    public void disconnectGoogle(String userId, final ApiCallback<String> callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("playerId", userId);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/auth/unlink-google")
                    .post(RequestBody.create(JSON, requestBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("サーバーエラー: " + response.code());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        boolean success = json.optBoolean("success", false);
                        if (success) {
                            String message = json.optString("message", "Google連携を解除しました");
                            callback.onSuccess(message);
                        } else {
                            String error = json.optString("error", "不明なエラー");
                            callback.onFailure(error);
                        }
                    } catch (JSONException e) {
                        callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    /**
     * サーバー上のユーザー状態を検証
     */
    public void validateUser(String userId, final ApiCallback<UserValidationResult> callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("id", userId);

            Request request = new Request.Builder()
                    .url(BASE_URL + "/validate-user")
                    .post(RequestBody.create(JSON, requestBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    callback.onFailure(e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        callback.onFailure("サーバーエラー: " + response.code());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);

                        UserValidationResult result = new UserValidationResult();
                        result.exists = json.optBoolean("exists", false);
                        result.isGoogleLinked = json.optBoolean("isGoogleLinked", false);

                        callback.onSuccess(result);
                    } catch (JSONException e) {
                        callback.onFailure("レスポンスの解析に失敗しました: " + e.getMessage());
                    }
                }
            });
        } catch (JSONException e) {
            callback.onFailure("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    /**
     * Google連携状態をローカルに保存
     */
    public void saveGoogleLinkStatus(String userId, boolean isLinked) {
        prefs.edit().putBoolean("google_linked_" + userId, isLinked).apply();
    }

    /**
     * Google連携状態をローカルから取得
     */
    public boolean getGoogleLinkStatus(String userId) {
        return prefs.getBoolean("google_linked_" + userId, false);
    }

    /**
     * サインアウト
     */
    public void signOut(Context context, OnCompleteListener<Void> listener) {
        mGoogleSignInClient.signOut().addOnCompleteListener(listener);
    }

    // 内部クラス定義

    public static class User {
        private String id;
        private String name;

        public User(String id, String name) {
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

    public static class FindUserResult {
        public boolean success;
        public boolean found;
        public User player;
    }

    public static class UserValidationResult {
        public boolean exists;
        public boolean isGoogleLinked;
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(String errorMessage);
    }
}
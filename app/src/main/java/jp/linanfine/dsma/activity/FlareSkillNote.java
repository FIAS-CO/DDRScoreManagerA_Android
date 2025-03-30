package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;

import jp.linanfine.dsma.BuildConfig;
import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.auth.GoogleAuthManager;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.util.flare.FlareSkillUpdater;
import jp.linanfine.dsma.util.skillnote.TopFlareSkillProcessor;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FlareSkillNote extends Activity {
    private static final String TAG = "FlareSkillNote";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String API_BASE_URL = "https://fnapi.fia-s.com/api";

    // ViewFlippers
    private ViewFlipper viewFlipper;
    private ViewFlipper googleButtonsFlipper;

    // 未登録ユーザーUI
    private EditText playerNameEditText;
    private Button registerButton;
    private Button googleLoginButton;
    private TextView messageTextUnregistered;

    // 登録済みユーザーUI
    private TextView userNameDisplay;
    private Button sendDataButton;
    private Button googleConnectButton;
    private Button googleDisconnectButton;
    private Button deleteUserButton;
    private TextView messageTextRegistered;

    // その他
    private OkHttpClient httpClient;
    private ProgressDialog progressDialog;
    private GoogleAuthManager googleAuthManager;
    private boolean isGoogleLinked = false;
    private String currentUserId = "";
    private String currentUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_flare_skill_note);

        // 初期化
        initializeComponents();
        setupListeners();
        loadUserData();
    }

    private void initializeComponents() {
        // HTTP Client
        httpClient = new OkHttpClient();

        // Google認証マネージャー
        googleAuthManager = GoogleAuthManager.getInstance();
        googleAuthManager.init(this, BuildConfig.GOOGLE_CLIENT_ID);

        // ViewFlippers
        viewFlipper = findViewById(R.id.viewFlipper);
        googleButtonsFlipper = findViewById(R.id.googleButtonsFlipper);

        // 未登録ユーザーUI要素
        playerNameEditText = findViewById(R.id.playerName);
        registerButton = findViewById(R.id.registerButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        messageTextUnregistered = findViewById(R.id.messageTextUnregistered);

        // 登録済みユーザーUI要素
        userNameDisplay = findViewById(R.id.userNameDisplay);
        sendDataButton = findViewById(R.id.sendDataButton);
        googleConnectButton = findViewById(R.id.googleConnectButton);
        googleDisconnectButton = findViewById(R.id.googleDisconnectButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        messageTextRegistered = findViewById(R.id.messageTextRegistered);

        // リンクボタン
        setupUrlButtons();

        // How to Use
        View howToUseTitle = findViewById(R.id.howToUseTitle);
        View howToUseContent = findViewById(R.id.howToUseContent);
        howToUseTitle.setOnClickListener(v -> {
            if (howToUseContent.getVisibility() == View.VISIBLE) {
                howToUseContent.setVisibility(View.GONE);
            } else {
                howToUseContent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupListeners() {
        // 登録ボタン
        registerButton.setOnClickListener(v -> {
            String username = playerNameEditText.getText().toString().trim();
            if (username.isEmpty()) {
                showMessage(getString(R.string.enter_username_and_press_button));
                return;
            }
            registerUser(username);
        });

        // Google関連ボタン
        googleLoginButton.setOnClickListener(v -> loginWithGoogle());
        googleConnectButton.setOnClickListener(v -> connectGoogleAccount());
        googleDisconnectButton.setOnClickListener(v -> disconnectGoogleAccount());

        // データ送信ボタン
        sendDataButton.setOnClickListener(v -> sendData());

        // 削除ボタン
        deleteUserButton.setOnClickListener(v -> confirmDeleteUser());
    }

    private void loadUserData() {
        // 保存されているユーザー情報を読み込む
        currentUserId = FileReader.readFlareSkillNotePlayerId(this);
        currentUserName = FileReader.readFlareSkillNotePlayerName(this);

        if (!currentUserId.isEmpty()) {
            // ユーザー情報が存在する場合
            userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + currentUserName);

            // Google連携状態を確認
            isGoogleLinked = googleAuthManager.getGoogleLinkStatus(currentUserId);
            updateGoogleButtons();

            // 登録済み画面を表示
            viewFlipper.setDisplayedChild(1);

            // サーバーでの状態を検証
            validateUserOnServer();
        } else {
            // 未登録時は登録画面を表示
            viewFlipper.setDisplayedChild(0);
        }
    }

    private void setupUrlButtons() {
        Button flareNoteTopUnregistered = findViewById(R.id.goToFlareNoteTopUnregistered);
        Button flareNoteTopRegistered = findViewById(R.id.goToFlareNoteTopRegistered);
        Button flareNoteUserSite = findViewById(R.id.goToFlareNoteUserSite);

        flareNoteTopUnregistered.setOnClickListener(v -> openWebPage("https://flarenote.fia-s.com"));
        flareNoteTopRegistered.setOnClickListener(v -> openWebPage("https://flarenote.fia-s.com"));
        flareNoteUserSite.setOnClickListener(v -> {
            if (!currentUserName.isEmpty()) {
                openWebPage("https://flarenote.fia-s.com/personal-skill/" + currentUserName);
            }
        });
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FileReader.requestAd(findViewById(R.id.adContainer), this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuthManager.RC_SIGN_IN) {
            handleGoogleSignInResult(data);
        }
    }

    private void handleGoogleSignInResult(Intent data) {
        try {
            GoogleSignInAccount account = googleAuthManager.processSignInResult(data);
            String idToken = account.getIdToken();

            if (idToken == null) {
                hideProgressDialog();
                showMessage("Google認証に失敗しました: IDトークンがありません");
                return;
            }

            // 現在の画面状態をチェック
            int currentScreen = viewFlipper.getDisplayedChild();

            if (currentScreen == 0) {
                // 未登録画面からの呼び出し - 既存ユーザーを検索
                findExistingUser(idToken);
            } else {
                // 登録済み画面からの呼び出し - 現在のユーザーとGoogleを連携
                connectCurrentUserWithGoogle(idToken);
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed: " + e.getStatusCode(), e);
            hideProgressDialog();
            String errorDetail = e.getStatusCode() + ": " + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode());
            showMessage("Google認証エラー: " + errorDetail);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error in Google sign in", e);
            hideProgressDialog();
            showMessage("Google認証で予期せぬエラーが発生しました: " + e.getMessage());
        }
    }

    // 既存ユーザーの検索（未登録画面からの呼び出し時）
    private void findExistingUser(String idToken) {
        googleAuthManager.findUserByGoogleToken(idToken, new GoogleAuthManager.ApiCallback<GoogleAuthManager.FindUserResult>() {
            @Override
            public void onSuccess(GoogleAuthManager.FindUserResult result) {
                runOnUiThread(() -> {
                    hideProgressDialog();

                    if (result.success && result.found && result.player != null) {
                        // 既存ユーザーが見つかった
                        currentUserId = result.player.getId();
                        currentUserName = result.player.getName();

                        // ユーザー情報を保存
                        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, currentUserId, currentUserName);
                        googleAuthManager.saveGoogleLinkStatus(currentUserId, true);
                        isGoogleLinked = true;

                        // UI更新
                        userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + currentUserName);
                        updateGoogleButtons();
                        viewFlipper.setDisplayedChild(1);

                        showMessage(getString(R.string.google_auth_user_login, currentUserName));
                    } else if (result.success) {
                        // ユーザーが見つからなかった
                        showMessage(getString(R.string.google_auth_no_user));
                    } else {
                        // API呼び出しは成功したが、処理に失敗した場合
                        showMessage("Google認証でエラーが発生しました");
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage("Google認証でエラーが発生しました: " + errorMessage);
                });
            }
        });
    }

    // 現在のユーザーとGoogleを連携（登録済み画面からの呼び出し時）
    private void connectCurrentUserWithGoogle(String idToken) {
        googleAuthManager.connectUserWithGoogle(currentUserId, idToken, new GoogleAuthManager.ApiCallback<GoogleAuthManager.User>() {
            @Override
            public void onSuccess(GoogleAuthManager.User user) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    googleAuthManager.saveGoogleLinkStatus(currentUserId, true);
                    isGoogleLinked = true;
                    updateGoogleButtons();
                    showMessage(getString(R.string.google_auth_connection_completed));
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage(getString(R.string.google_auth_connection_failed, errorMessage));
                });
            }
        });
    }

    //---------- ユーザー登録関連 ----------

    private void registerUser(String username) {
        showProgressDialog();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", username);

            Request request = new Request.Builder()
                    .url(API_BASE_URL + "/create-user")
                    .post(RequestBody.create(JSON, jsonBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (response.isSuccessful()) {
                                String id = jsonResponse.getString("id");
                                String name = jsonResponse.getString("name");

                                // ユーザー情報を保存
                                currentUserId = id;
                                currentUserName = name;
                                FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, id, name);

                                // UI更新
                                userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + name);
                                showMessage(getString(R.string.flarenote_success_create_user, name));

                                // 登録済み画面に切り替え
                                viewFlipper.setDisplayedChild(1);
                            } else {
                                // エラー処理
                                String error = jsonResponse.optString("error", "不明なエラー");
                                String detail = jsonResponse.optString("detail", "詳細なし");
                                showMessage(getString(R.string.flarenote_error_detail, error, detail));
                            }
                        } catch (JSONException e) {
                            showMessage("レスポンスの解析に失敗しました: " + e.getMessage());
                        }
                    });
                }
            });
        } catch (JSONException e) {
            hideProgressDialog();
            showMessage("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    private void confirmDeleteUser() {
        if (currentUserName.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.flarenote_confirm_deletion)
                .setMessage(getString(R.string.flarenote_confirm_deletion_message, currentUserName))
                .setPositiveButton(R.string.flarenote_delete, (dialog, which) -> deleteUser())
                .setNegativeButton(R.string.flarenote_cancel, null)
                .show();
    }

    private void deleteUser() {
        if (currentUserId.isEmpty()) {
            return;
        }

        showProgressDialog();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", currentUserId);

            // ローカルデータを先に削除
            String nameToShow = currentUserName;
            FileReader.saveFlareSkillNotePlayerId(this, "", "");
            googleAuthManager.saveGoogleLinkStatus(currentUserId, false);
            currentUserId = "";
            currentUserName = "";

            // 画面を未登録状態に戻す
            viewFlipper.setDisplayedChild(0);
            playerNameEditText.setText("");

            Request request = new Request.Builder()
                    .url(API_BASE_URL + "/delete-user")
                    .post(RequestBody.create(JSON, jsonBody.toString()))
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseBody = response.body().string();
                    runOnUiThread(() -> {
                        hideProgressDialog();
                        try {
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            if (response.isSuccessful()) {
                                showMessage(getString(R.string.flarenote_user_deleted, nameToShow));
                            } else {
                                String error = jsonResponse.optString("error", "不明なエラー");
                                String details = jsonResponse.optString("details", "詳細なし");
                                showMessage(getString(R.string.flarenote_error_detail, error, details));
                            }
                        } catch (JSONException e) {
                            showMessage("レスポンスの解析に失敗しました: " + e.getMessage());
                        }
                    });
                }
            });
        } catch (JSONException e) {
            hideProgressDialog();
            showMessage("リクエストの作成に失敗しました: " + e.getMessage());
        }
    }

    //---------- データ送信 ----------

    private void sendData() {
        if (currentUserId.isEmpty()) {
            showMessage(getString(R.string.flarenote_push_button_after_registration));
            return;
        }

        showProgressDialog();

        // スコアデータの処理
        new Thread(() -> {
            try {
                TreeMap<Integer, MusicData> musicDataMap = FileReader.readMusicList(this);
                TreeMap<Integer, MusicScore> musicScoreMap = FileReader.readScoreList(this, null);
                FlareSkillUpdater.updateAllFlareSkills(musicDataMap, musicScoreMap);

                String jsonData = new TopFlareSkillProcessor().processTopFlareSkills(
                        currentUserId, musicScoreMap, musicDataMap);

                Request request = new Request.Builder()
                        .url(API_BASE_URL + "/player-scores")
                        .post(RequestBody.create(JSON, jsonData))
                        .build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseBody = response.body().string();
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            try {
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                if (response.isSuccessful()) {
                                    showMessage(getString(R.string.flarenote_send_data_successfully));
                                } else {
                                    String error = jsonResponse.optString("error", "不明なエラー");
                                    String detail = jsonResponse.optString("details", "詳細なし");
                                    showMessage(getString(R.string.flarenote_error_detail, error, detail));
                                }
                            } catch (JSONException e) {
                                showMessage("レスポンスの解析に失敗しました: " + e.getMessage());
                            }
                        });
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage("データ処理中にエラーが発生しました: " + e.getMessage());
                });
            }
        }).start();
    }

//---------- Google認証関連 ----------

    private void loginWithGoogle() {
        showProgressDialog();
        googleAuthManager.startGoogleSignIn(this);
    }

    private void connectGoogleAccount() {
        if (currentUserId.isEmpty()) {
            showMessage(getString(R.string.flarenote_user_not_registered));
            return;
        }

        showProgressDialog();
        googleAuthManager.startGoogleSignIn(this);
    }

    private void disconnectGoogleAccount() {
        if (currentUserId.isEmpty()) {
            return;
        }

        showProgressDialog();
        googleAuthManager.disconnectGoogle(currentUserId, new GoogleAuthManager.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    googleAuthManager.saveGoogleLinkStatus(currentUserId, false);
                    isGoogleLinked = false;
                    updateGoogleButtons();
                    showMessage(getString(R.string.google_auth_connection_removed));

                    // Googleからもサインアウト
                    googleAuthManager.signOut(FlareSkillNote.this, task -> {
                        // サインアウト完了（特に何もしない）
                    });
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage(getString(R.string.google_auth_disconnect_failed) + ": " + errorMessage);
                });
            }
        });
    }

    private void validateUserOnServer() {
        if (currentUserId.isEmpty()) {
            return;
        }

        // プログレスダイアログなしで検証
        googleAuthManager.validateUser(currentUserId, new GoogleAuthManager.ApiCallback<GoogleAuthManager.UserValidationResult>() {
            @Override
            public void onSuccess(GoogleAuthManager.UserValidationResult result) {
                runOnUiThread(() -> {
                    if (!result.exists) {
                        // ユーザーが削除されている場合
                        showMessage("別端末からユーザーが削除されました");

                        // ローカルデータをクリア
                        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");
                        googleAuthManager.saveGoogleLinkStatus(currentUserId, false);
                        currentUserId = "";
                        currentUserName = "";

                        // 未登録画面に切り替え
                        playerNameEditText.setText("");
                        viewFlipper.setDisplayedChild(0);
                    } else if (isGoogleLinked != result.isGoogleLinked) {
                        // Google連携状態が変更されている場合
                        isGoogleLinked = result.isGoogleLinked;
                        googleAuthManager.saveGoogleLinkStatus(currentUserId, result.isGoogleLinked);
                        updateGoogleButtons();

                        if (!isGoogleLinked) {
                            showMessage("別端末からGoogleアカウントとの連携が解除されました");
                        }
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // エラーがあっても特に何もしない（静かに失敗）
                Log.e(TAG, "Failed to validate user: " + errorMessage);
            }
        });
    }

    private void updateGoogleButtons() {
        // Google連携状態に応じてボタン表示を切替
        googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
    }

    //---------- ユーティリティ ----------

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.flarenote_processing));
            progressDialog.setCancelable(false);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessage(String message) {
        runOnUiThread(() -> {
            // 両方のメッセージ欄に同じ内容を表示
            messageTextUnregistered.setText(message);
            messageTextRegistered.setText(message);

            // 長いメッセージの場合はトーストも表示
            if (message.length() > 30) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
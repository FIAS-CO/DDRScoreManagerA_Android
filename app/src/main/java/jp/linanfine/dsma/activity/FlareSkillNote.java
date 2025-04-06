package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;

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
    private EditText usernameEditText;
    private Button registerButton;
    private Button goToUserSiteButton;
    private OkHttpClient client;
    private ViewFlipper viewFlipper;

    // 未登録ユーザー向けUI要素
    private Button googleLoginButton;
    private TextView messageTextUnregistered;

    // 登録済みユーザー向けUI要素
    private TextView userNameDisplay;
    private Button sendDataButton;
    private Button deleteUserButton;
    private ViewFlipper googleButtonsFlipper;
    private Button googleConnectButton;
    private Button googleDisconnectButton;
    private TextView messageTextRegistered;
    private Button goToFlareNoteTopRegistered;

    private boolean isUserRegistered = false;
    private boolean isGoogleLinked = false;
    private String userId = "";
    private String userName = "";
    private boolean isLoading = false;

    private GoogleAuthManager googleAuthManager;

    public void initialize() {
        // ViewFlipper設定
        viewFlipper = findViewById(R.id.viewFlipper);

        // 共通UI要素
        client = new OkHttpClient();

        // 未登録ユーザー向けUI要素
        usernameEditText = findViewById(R.id.playerName);
        registerButton = findViewById(R.id.registerButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        messageTextUnregistered = findViewById(R.id.messageTextUnregistered);

        // 登録済みユーザー向けUI要素
        userNameDisplay = findViewById(R.id.userNameDisplay);
        sendDataButton = findViewById(R.id.sendDataButton);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        googleButtonsFlipper = findViewById(R.id.googleButtonsFlipper);
        googleConnectButton = findViewById(R.id.googleConnectButton);
        googleDisconnectButton = findViewById(R.id.googleDisconnectButton);
        messageTextRegistered = findViewById(R.id.messageTextRegistered);
        goToFlareNoteTopRegistered = findViewById(R.id.goToFlareNoteTopRegistered);
        goToUserSiteButton = findViewById(R.id.goToFlareNoteUserSite);

        // GoogleAuthManagerのインスタンス化とinitialize
        googleAuthManager = GoogleAuthManager.Companion.getInstance();
        googleAuthManager.init(this);

        // ボタンイベントの設定
        registerButton.setOnClickListener(v -> {
            createUser(usernameEditText.getText().toString());
        });

        findViewById(R.id.deleteUserButton).setOnClickListener(v -> showDeleteConfirmation());

        sendDataButton.setOnClickListener(v -> sendData());

        // GoogleLoginボタン（未登録ユーザー用）
        googleLoginButton.setOnClickListener(v -> findUserWithGoogle());

        // Google連携・解除ボタン（登録済みユーザー用）
        googleConnectButton.setOnClickListener(v -> linkGoogleAccount());
        googleDisconnectButton.setOnClickListener(v -> unlinkGoogleAccount());

        // 「FlareNote TOP」ボタン
        findViewById(R.id.goToFlareNoteTopUnregistered).setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com"));
        goToFlareNoteTopRegistered.setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com"));

        // 「ユーザーページ」ボタン
        goToUserSiteButton.setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com/personal-skill/" + userName));

        // HowTo使用のトグル
        final TextView howToUseTitle = findViewById(R.id.howToUseTitle);
        final TextView howToUseContent = findViewById(R.id.howToUseContent);

        // XML側から表示したかった
        howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);

        howToUseTitle.setOnClickListener(v -> {
            if (howToUseContent.getVisibility() == View.VISIBLE) {
                howToUseContent.setVisibility(View.GONE);
                howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
            } else {
                howToUseContent.setVisibility(View.VISIBLE);
                howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        // ユーザーデータのロード
        loadUserData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView = this.getLayoutInflater().inflate(R.layout.activity_flare_skill_note, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);

        initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);

        // サーバーでユーザーとGoogle連携状態を検証
        validateUserOnServer();
    }

    private void loadUserData() {
        userId = FileReader.readFlareSkillNotePlayerId(this);
        userName = FileReader.readFlareSkillNotePlayerName(this);

        isUserRegistered = !userId.isEmpty();

        // Google連携状態の取得
        isGoogleLinked = getSharedPreferences("FlareNotePrefs", MODE_PRIVATE)
                .getBoolean("isGoogleLinked_" + userId, false);

        updateUI();
    }

    private void updateUI() {
        if (isUserRegistered) {
            // 登録済みユーザー用UI表示
            viewFlipper.setDisplayedChild(1);
            userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + userName);

            // Google連携状態に応じたボタン表示
            googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
            goToUserSiteButton.setEnabled(true);
        } else {
            // 未登録ユーザー用UI表示
            viewFlipper.setDisplayedChild(0);
            goToUserSiteButton.setEnabled(false);
        }
    }

    private void createUser(String username) {
        if (username.isEmpty()) {
            setMessage(getString(R.string.enter_username_and_press_button));
            return;
        }

        setLoading(true);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url("https://fnapi.fia-s.com/api/create-user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    setMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                    setLoading(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("name");
                            setMessage(getString(R.string.flarenote_success_create_user, name));
                            String id = jsonResponse.getString("id");
                            FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, id, name);

                            userId = id;
                            userName = name;
                            isUserRegistered = true;

                            updateUI();
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("detail", "詳細なし");
                            setMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        setMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                    setLoading(false);
                });
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.flarenote_confirm_deletion)
                .setMessage(getString(R.string.flarenote_confirm_deletion_message, userName))
                .setPositiveButton(R.string.flarenote_delete, (dialog, which) -> deleteUser())
                .setNegativeButton(R.string.flarenote_cancel, null)
                .show();
    }

    private void deleteUser() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);

        if (id.isEmpty()) {
            return;
        }

        setLoading(true);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toString());
        Request request = new Request.Builder()
                .url("https://fnapi.fia-s.com/api/delete-user")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    setMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                    setLoading(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("user");
                            setMessage(getString(R.string.flarenote_user_deleted, name));

                            // ユーザー情報をクリア
                            FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");
                            isUserRegistered = false;
                            isGoogleLinked = false;
                            userId = "";
                            userName = "";

                            // Google連携情報も削除
                            getSharedPreferences("FlareNotePrefs", MODE_PRIVATE)
                                    .edit()
                                    .remove("isGoogleLinked_" + id)
                                    .apply();

                            updateUI();
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("detail", "詳細なし");
                            setMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        setMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                    setLoading(false);
                });
            }
        });
    }

    private void sendData() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);

        if (id.isEmpty()) {
            setMessage(getString(R.string.flarenote_push_button_after_registration));
            return;
        }

        setLoading(true);

        TreeMap<Integer, MusicData> musicDataMap = FileReader.readMusicList(FlareSkillNote.this);
        TreeMap<Integer, MusicScore> musicScoreMap = FileReader.readScoreList(FlareSkillNote.this, null);
        FlareSkillUpdater.updateAllFlareSkills(musicDataMap, musicScoreMap);

        String json = new TopFlareSkillProcessor().processTopFlareSkills(id, musicScoreMap, musicDataMap);

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("https://fnapi.fia-s.com/api/player-scores")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    setMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                    setLoading(false);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            setMessage(getString(R.string.flarenote_send_data_successfully));
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("details", "no detail.");
                            setMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        setMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                    setLoading(false);
                });
            }
        });
    }

    // Googleアカウントを使ってユーザーを検索
    private void findUserWithGoogle() {
        setLoading(true);

        // バックグラウンドスレッドでGoogle認証を実行
        new Thread(() -> {
            try {
                // Google認証を実行
                GoogleAuthManager.FindUserResult result = googleAuthManager.findUserWithGoogleSync(FlareSkillNote.this);

                runOnUiThread(() -> {
                    if (result.getSuccess() && result.getFound() && result.getPlayer() != null) {
                        userId = result.getPlayer().getId();
                        userName = result.getPlayer().getName();
                        isUserRegistered = true;
                        isGoogleLinked = true;

                        // 設定を保存
                        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, userId, userName);
                        googleAuthManager.saveGoogleLinkStatus(userId, true);

                        updateUI();
                        setMessage(getString(R.string.google_auth_user_login, userName));
                    } else {
                        setMessage(getString(R.string.google_auth_no_user));
                    }
                    setLoading(false);
                });
            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";

                runOnUiThread(() -> {
                    setLoading(false);
                    if (errorMessage.contains("canceled") || errorMessage.contains("cancelled")) {
                        setMessage(getString(R.string.google_auth_cancelled));
                    } else {
                        setMessage(getString(R.string.google_auth_failed, errorMessage));
                    }
                });
            }
        }).start();
    }

    // Googleアカウントとユーザーを紐づける
    private void linkGoogleAccount() {
        if (!isUserRegistered || userId.isEmpty()) {
            setMessage(getString(R.string.flarenote_user_not_registered));
            return;
        }

        setLoading(true);

        // バックグラウンドスレッドでGoogle認証を実行
        new Thread(() -> {
            try {

                // メソッド名を確認して正しいメソッドを呼び出す
                runOnUiThread(() -> {
                    try {
                        // 非同期メソッドを呼び出す代わりに、実装を簡略化
                        // 本来はgoogleAuthManagerの正しいメソッドを呼び出す
                        setMessage("Google連携を試行中です...");

                        // 仮実装（実際にはこれがJava側から呼べるメソッドで置き換える）
                        new Thread(() -> {
                            try {
                                GoogleAuthManager.User user = googleAuthManager.connectWithGoogleSync(FlareSkillNote.this, userId);

                                runOnUiThread(() -> {
                                    isGoogleLinked = true;
                                    googleAuthManager.saveGoogleLinkStatus(userId, true);

                                    googleButtonsFlipper.setDisplayedChild(1); // 解除ボタンを表示
                                    setMessage(getString(R.string.google_auth_connection_completed));
                                    setLoading(false);
                                });
                            } catch (Exception e) {
                                final String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";

                                runOnUiThread(() -> {
                                    setLoading(false);
                                    if (errorMessage.contains("canceled") || errorMessage.contains("cancelled")) {
                                        setMessage(getString(R.string.google_auth_cancelled));
                                    } else {
                                        setMessage(getString(R.string.google_auth_connection_failed, errorMessage));
                                    }
                                });
                            }
                        }).start();
                    } catch (Exception e) {
                        setLoading(false);
                        setMessage("Error: " + e.getLocalizedMessage());
                    }
                });
            } catch (Exception e) {
                final String errorMessage = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";

                runOnUiThread(() -> {
                    setLoading(false);
                    if (errorMessage.contains("canceled") || errorMessage.contains("cancelled")) {
                        setMessage(getString(R.string.google_auth_cancelled));
                    } else {
                        setMessage(getString(R.string.google_auth_connection_failed, errorMessage));
                    }
                });
            }
        }).start();
    }

    // Googleアカウントとの連携を解除
    private void unlinkGoogleAccount() {
        setLoading(true);

        // バックグラウンドスレッドでGoogle連携解除を実行
        new Thread(() -> {
            try {
                // Google連携解除を実行
                String message = googleAuthManager.disconnectGoogleSync(userId);

                runOnUiThread(() -> {
                    isGoogleLinked = false;
                    googleAuthManager.saveGoogleLinkStatus(userId, false);

                    googleButtonsFlipper.setDisplayedChild(0); // 連携ボタンを表示
                    setMessage(getString(R.string.google_auth_connection_removed));
                    setLoading(false);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                    setMessage(getString(R.string.google_auth_disconnect_failed));
                });
            }
        }).start();
    }

    // サーバーでユーザーとGoogle連携状態を検証
    private void validateUserOnServer() {
        if (userId.isEmpty()) return;

        setLoading(true);

        // バックグラウンドスレッドでユーザー検証を実行
        new Thread(() -> {
            try {
                // ユーザー検証を実行
                GoogleAuthManager.UserValidationResult result = googleAuthManager.validateUserSync(userId);

                runOnUiThread(() -> {
                    setLoading(false);

                    if (!result.getExists()) {
                        // ユーザーが削除されている場合
                        setMessage(getString(R.string.flarenote_user_not_registered));

                        // ユーザー情報をクリア
                        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");
                        isUserRegistered = false;
                        isGoogleLinked = false;
                        userId = "";
                        userName = "";

                        // Google連携情報も削除
                        googleAuthManager.saveGoogleLinkStatus(userId, false);

                        updateUI();
                    } else if (isGoogleLinked && !result.isGoogleLinked()) {
                        // Google連携が解除されている場合
                        setMessage(getString(R.string.google_auth_disconnect_failed));
                        isGoogleLinked = false;
                        googleAuthManager.saveGoogleLinkStatus(userId, false);

                        googleButtonsFlipper.setDisplayedChild(0); // 連携ボタンを表示
                    } else {
                        // サーバーと連携状態を同期
                        isGoogleLinked = result.isGoogleLinked();
                        googleAuthManager.saveGoogleLinkStatus(userId, result.isGoogleLinked());

                        googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    setLoading(false);
                });
            }
        }).start();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void setLoading(boolean loading) {
        isLoading = loading;

        // ローディング表示の切り替え
        // 実際のローディングUI要素がある場合はここで制御

        // UI要素の有効/無効切り替え
        findViewById(R.id.playerName).setEnabled(!loading);
        findViewById(R.id.registerButton).setEnabled(!loading);
        findViewById(R.id.googleLoginButton).setEnabled(!loading);
        findViewById(R.id.sendDataButton).setEnabled(!loading);
        findViewById(R.id.deleteUserButton).setEnabled(!loading);
        findViewById(R.id.googleConnectButton).setEnabled(!loading);
        findViewById(R.id.googleDisconnectButton).setEnabled(!loading);
    }

    private void setMessage(String message) {
        // 両方のメッセージ表示用TextViewに同じメッセージをセット
        messageTextUnregistered.setText(message);
        messageTextRegistered.setText(message);
    }
}
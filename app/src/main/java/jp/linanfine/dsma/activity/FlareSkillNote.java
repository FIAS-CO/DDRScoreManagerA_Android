package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.databinding.ActivityFlareSkillNoteBinding;
import jp.linanfine.dsma.databinding.ViewFlareUploaderRegisteredUserBinding;
import jp.linanfine.dsma.databinding.ViewFlareUploaderUnregisteredUserBinding;
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
    private ActivityFlareSkillNoteBinding binding;

    private ViewFlareUploaderUnregisteredUserBinding unregisteredBinding;
    private ViewFlareUploaderRegisteredUserBinding registeredBinding;

    private OkHttpClient client;
    private GoogleAuthManager googleAuthManager;

    private boolean isUserRegistered = false;
    private boolean isGoogleLinked = false;
    private String userId = "";
    private String userName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);

        // ViewBindingの初期化
        binding = ActivityFlareSkillNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        unregisteredBinding = binding.unregisteredUserView;
        registeredBinding = binding.registeredUserView;

        initialize();
    }

    public void initialize() {
        client = new OkHttpClient();

        // GoogleAuthManagerのインスタンス化とinitialize
        googleAuthManager = GoogleAuthManager.Companion.getInstance();
        googleAuthManager.init(this);

        // ボタンイベントの設定
        unregisteredBinding.registerButton.setOnClickListener(v ->
                createUser(unregisteredBinding.playerName.getText().toString()));

        // GoogleLoginボタン（未登録ユーザー用）
        unregisteredBinding.googleLoginButton.setOnClickListener(v -> findUserWithGoogle());

        // 「FlareNote TOP」ボタン
        unregisteredBinding.goToFlareNoteTopUnregistered.setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com"));

        registeredBinding.deleteUserButton.setOnClickListener(v -> showDeleteConfirmation());

        registeredBinding.sendDataButton.setOnClickListener(v -> sendData());

        // Google連携・解除ボタン（登録済みユーザー用）
        registeredBinding.googleConnectButton.setOnClickListener(v -> linkGoogleAccount());
        registeredBinding.googleDisconnectButton.setOnClickListener(v -> unlinkGoogleAccount());

        registeredBinding.goToFlareNoteTopRegistered.setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com"));

        // 「ユーザーページ」ボタン
        registeredBinding.goToFlareNoteUserSite.setOnClickListener(v ->
                openUrl("https://flarenote.fia-s.com/personal-skill/" + userName));

        // XML側から表示したかった
        binding.howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);

        binding.howToUseTitle.setOnClickListener(v -> {
            if (binding.howToUseContent.getVisibility() == View.VISIBLE) {
                binding.howToUseContent.setVisibility(View.GONE);
                binding.howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
            } else {
                binding.howToUseContent.setVisibility(View.VISIBLE);
                binding.howToUseTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        binding.googleAuthTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);

        binding.googleAuthTitle.setOnClickListener(v -> {
            if (binding.googleAuthContent.getVisibility() == View.VISIBLE) {
                binding.googleAuthContent.setVisibility(View.GONE);
                binding.googleAuthTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_right, 0);
            } else {
                binding.googleAuthContent.setVisibility(View.VISIBLE);
                binding.googleAuthTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            }
        });

        // ユーザーデータのロード
        loadUserData();
    }

    @Override
    public void onResume() {
        super.onResume();
        FileReader.requestAd(binding.adContainer, this);

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
            binding.viewFlipper.setDisplayedChild(1);
            registeredBinding.userNameDisplay.setText(getString(R.string.flarenote_username, userName));

            // Google連携状態に応じたボタン表示
            registeredBinding.googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
            registeredBinding.goToFlareNoteUserSite.setEnabled(true);
        } else {
            // 未登録ユーザー用UI表示
            binding.viewFlipper.setDisplayedChild(0);
            registeredBinding.goToFlareNoteUserSite.setEnabled(false);
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
                assert response.body() != null;
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
                            String detail = jsonResponse.optString("detail", "no detail");
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
                assert response.body() != null;
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
                assert response.body() != null;
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
                runOnUiThread(() -> {
                    try {
                        setMessage(getString(R.string.google_auth_trying_to_connect));

                        new Thread(() -> {
                            try {
                                googleAuthManager.connectWithGoogleSync(FlareSkillNote.this, userId);

                                runOnUiThread(() -> {
                                    isGoogleLinked = true;
                                    googleAuthManager.saveGoogleLinkStatus(userId, true);

                                    registeredBinding.googleButtonsFlipper.setDisplayedChild(1); // 解除ボタンを表示
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
                googleAuthManager.disconnectGoogleSync(userId);

                runOnUiThread(() -> {
                    isGoogleLinked = false;
                    googleAuthManager.saveGoogleLinkStatus(userId, false);

                    registeredBinding.googleButtonsFlipper.setDisplayedChild(0); // 連携ボタンを表示
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

                        registeredBinding.googleButtonsFlipper.setDisplayedChild(0); // 連携ボタンを表示
                    } else {
                        // サーバーと連携状態を同期
                        isGoogleLinked = result.isGoogleLinked();
                        googleAuthManager.saveGoogleLinkStatus(userId, result.isGoogleLinked());

                        registeredBinding.googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
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
        // UI要素の有効/無効切り替え
        unregisteredBinding.playerName.setEnabled(!loading);
        unregisteredBinding.registerButton.setEnabled(!loading);
        unregisteredBinding.googleLoginButton.setEnabled(!loading);
        registeredBinding.sendDataButton.setEnabled(!loading);
        registeredBinding.deleteUserButton.setEnabled(!loading);
        registeredBinding.googleConnectButton.setEnabled(!loading);
        registeredBinding.googleDisconnectButton.setEnabled(!loading);
    }

    private void setMessage(String message) {
        // 両方のメッセージ表示用TextViewに同じメッセージをセット
        unregisteredBinding.messageTextUnregistered.setText(message);
        registeredBinding.messageTextRegistered.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // バインディングをクリア
        binding = null;
    }
}
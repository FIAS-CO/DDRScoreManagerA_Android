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
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.ConnectionResult;

public class FlareSkillNote extends Activity {
    private String TAG = "FlareSkillNote";
    // メインViewFlipper（未登録/登録済み画面切替）
    private ViewFlipper viewFlipper;
    // Google認証ボタン切替用ViewFlipper
    private ViewFlipper googleButtonsFlipper;

    // 未登録ユーザー向けUI要素
    private EditText playerNameEditText;
    private Button registerButton;
    private Button googleLoginButton;
    private TextView messageTextUnregistered;

    // 登録済みユーザー向けUI要素
    private TextView userNameDisplay;
    private Button sendDataButton;
    private Button googleConnectButton;
    private Button googleDisconnectButton;
    private Button goToUserSiteButton;
    private Button deleteUserButton;
    private TextView messageTextRegistered;

    // 共通のHow to Useセクション
    private TextView howToUseContent;

    private OkHttpClient client;
    private ProgressDialog progressDialog;
    private boolean isGoogleLinked = false;
    private GoogleAuthManager googleAuthManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView = this.getLayoutInflater().inflate(R.layout.activity_flare_skill_note, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);

        initialize();
    }

    public void initialize() {
        // ViewFlipperの初期化
        viewFlipper = findViewById(R.id.viewFlipper);
        googleButtonsFlipper = findViewById(R.id.googleButtonsFlipper);

        // Google Play Services の状態確認コードを追加
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services not available: " + resultCode);
            // ユーザーに通知
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            }
        }

        // 共通
        client = new OkHttpClient();

        // 未登録ユーザー向けUI要素の初期化
        playerNameEditText = findViewById(R.id.playerName);
        registerButton = findViewById(R.id.registerButton);
        googleLoginButton = findViewById(R.id.googleLoginButton);
        messageTextUnregistered = findViewById(R.id.messageTextUnregistered);

        // 登録済みユーザー向けUI要素の初期化
        userNameDisplay = findViewById(R.id.userNameDisplay);
        sendDataButton = findViewById(R.id.sendDataButton);
        googleConnectButton = findViewById(R.id.googleConnectButton);
        googleDisconnectButton = findViewById(R.id.googleDisconnectButton);
        goToUserSiteButton = findViewById(R.id.goToFlareNoteUserSite);
        deleteUserButton = findViewById(R.id.deleteUserButton);
        messageTextRegistered = findViewById(R.id.messageTextRegistered);

        // 共通部分のHow to Use初期化
        howToUseContent = findViewById(R.id.howToUseContent);
        findViewById(R.id.howToUseTitle).setOnClickListener(v -> {
            if (howToUseContent.getVisibility() == View.VISIBLE) {
                howToUseContent.setVisibility(View.GONE);
            } else {
                howToUseContent.setVisibility(View.VISIBLE);
            }
        });

        // GoogleAuthManagerの初期化
        googleAuthManager = GoogleAuthManager.getInstance();
        googleAuthManager.initialize(this);

        // ボタンのクリックリスナーを設定
        registerButton.setOnClickListener(v -> {
            String username = playerNameEditText.getText().toString();
            createUser(username);
        });

        googleLoginButton.setOnClickListener(v -> loginWithGoogle());
        googleConnectButton.setOnClickListener(v -> connectWithGoogle());
        googleDisconnectButton.setOnClickListener(v -> disconnectGoogle());
        sendDataButton.setOnClickListener(v -> sendData());
        deleteUserButton.setOnClickListener(v -> confirmDeleteUser());

        // URLボタンの設定
        String userName = FileReader.readFlareSkillNotePlayerName(this);
        setupUrlButton(R.id.goToFlareNoteTopUnregistered, "https://flarenote.fia-s.com");
        setupUrlButton(R.id.goToFlareNoteTopRegistered, "https://flarenote.fia-s.com");
        setupUrlButton(R.id.goToFlareNoteUserSite, "https://flarenote.fia-s.com/personal-skill/" + userName);

        // 初期状態の設定
        String playerId = FileReader.readFlareSkillNotePlayerId(this);
        String playerName = FileReader.readFlareSkillNotePlayerName(this);

        if (!playerId.isEmpty()) {
            // 登録済みモード
            userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + playerName);

            // Google連携状態を取得
            isGoogleLinked = googleAuthManager.getGoogleLinkStatus(this, playerId);
            updateGoogleButtons();

            // 登録済み画面を表示
            viewFlipper.setDisplayedChild(1);
        } else {
            // 未登録モード
            viewFlipper.setDisplayedChild(0);
        }

        // サーバーでのユーザー状態を検証
        validateUserOnServer();
    }

    @Override
    public void onResume() {
        super.onResume();
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ここにデバッグログを追加（TAGを使用）
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (data != null) {
            Log.d(TAG, "Intent data: " + data.toString());
        } else {
            Log.d(TAG, "Intent data is null");
        }

        // Google認証の結果を処理
        if (requestCode == GoogleAuthManager.RC_SIGN_IN) {
            hideProgressDialog();
            if (resultCode == RESULT_OK && data != null) {
                Log.d(TAG, "Sign-in result data: " + data);
                googleAuthManager.handleSignInResult(data);
            } else {
                showMessage(getString(R.string.flarenote_google_auth_cancelled));
            }
        }
    }

    private void createUser(String username) {
        if (username.isEmpty()) {
            showMessage(this.getResources().getString(R.string.enter_username_and_press_button));
            return;
        }

        showProgressDialog();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", username);
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressDialog();
            return;
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
                    hideProgressDialog();
                    showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("name");
                            String id = jsonResponse.getString("id");
                            FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, id, name);

                            // 登録成功メッセージを表示
                            showMessage(getString(R.string.flarenote_success_create_user, name));

                            // ユーザー名を表示に設定
                            userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + name);

                            // 画面を切り替え
                            viewFlipper.setDisplayedChild(1);
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("detail", "詳細なし");
                            showMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        showMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                });
            }
        });
    }

    private void confirmDeleteUser() {
        String playerName = FileReader.readFlareSkillNotePlayerName(FlareSkillNote.this);
        if (playerName.isEmpty()) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.flarenote_confirm_deletion)
                .setMessage(getString(R.string.flarenote_confirm_deletion_message, playerName))
                .setPositiveButton(R.string.flarenote_delete, (dialog, which) -> deleteUser())
                .setNegativeButton(R.string.flarenote_cancel, null)
                .show();
    }

    private void deleteUser() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);
        String playerName = FileReader.readFlareSkillNotePlayerName(FlareSkillNote.this);

        if (id.isEmpty()) {
            return;
        }

        showProgressDialog();

        // ローカルデータを先に削除
        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");
        googleAuthManager.saveGoogleLinkStatus(this, id, false);

        // 未登録画面に切り替え
        playerNameEditText.setText("");
        viewFlipper.setDisplayedChild(0);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
            hideProgressDialog();
            return;
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
                    hideProgressDialog();
                    showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("user");
                            showMessage(getString(R.string.flarenote_user_deleted, name));
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("details", "詳細なし");
                            showMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        showMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                });
            }
        });
    }

    private void sendData() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);

        if (id.isEmpty()) {
            showMessage(getString(R.string.flarenote_push_button_after_registration));
            return;
        }

        showProgressDialog();

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
                    hideProgressDialog();
                    showMessage(getString(R.string.flarenote_network_error, e.getMessage()));
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    hideProgressDialog();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            showMessage(getString(R.string.flarenote_send_data_successfully));
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("details", "no detail.");
                            showMessage(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        showMessage(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                });
            }
        });
    }

    // Google認証関連の処理

    // Googleアカウントでログイン
    private void loginWithGoogle() {
        showProgressDialog();
        googleAuthManager.signIn(this, new GoogleAuthManager.GoogleAuthCallback() {
            @Override
            public void onSuccess(String idToken, GoogleSignInAccount account) {
                googleAuthManager.findPlayerByGoogleToken(idToken, new GoogleAuthManager.FindPlayerCallback() {
                    @Override
                    public void onSuccess(boolean success, boolean found, GoogleAuthManager.Player player, String message) {
                        hideProgressDialog();
                        showMessage(message);

                        if (success && found && player != null) {
                            // ユーザーが見つかった場合、情報を保存
                            FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, player.getId(), player.getName());
                            googleAuthManager.saveGoogleLinkStatus(FlareSkillNote.this, player.getId(), true);
                            isGoogleLinked = true;

                            runOnUiThread(() -> {
                                // ユーザー名を表示
                                userNameDisplay.setText(getString(R.string.flarenote_username) + ": " + player.getName());
                                // Google連携ボタンを更新
                                updateGoogleButtons();
                                // 登録済み画面に切り替え
                                viewFlipper.setDisplayedChild(1);
                            });
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        hideProgressDialog();
                        showMessage(error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                hideProgressDialog();
                showMessage(error);
            }
        });
    }

    // 既存ユーザーとGoogleアカウントを連携
    private void connectWithGoogle() {
        String playerId = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);
        if (playerId.isEmpty()) {
            showMessage(getString(R.string.flarenote_user_not_registered));
            return;
        }

        showProgressDialog();
        googleAuthManager.signIn(this, new GoogleAuthManager.GoogleAuthCallback() {
            @Override
            public void onSuccess(String idToken, GoogleSignInAccount account) {
                googleAuthManager.connectUserWithGoogleToken(playerId, idToken, new GoogleAuthManager.ConnectGoogleCallback() {
                    @Override
                    public void onSuccess(GoogleAuthManager.Player player, String message) {
                        googleAuthManager.saveGoogleLinkStatus(FlareSkillNote.this, playerId, true);
                        isGoogleLinked = true;

                        runOnUiThread(() -> {
                            hideProgressDialog();
                            showMessage(message);
                            updateGoogleButtons();
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        runOnUiThread(() -> {
                            hideProgressDialog();
                            showMessage(error);
                        });
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                hideProgressDialog();
                showMessage(error);
            }
        });
    }

    // Googleアカウント連携を解除
    private void disconnectGoogle() {
        String playerId = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);
        if (playerId.isEmpty()) {
            return;
        }

        showProgressDialog();
        googleAuthManager.unlinkGoogleFromPlayer(playerId, new GoogleAuthManager.UnlinkGoogleCallback() {
            @Override
            public void onSuccess(String message) {
                googleAuthManager.saveGoogleLinkStatus(FlareSkillNote.this, playerId, false);
                isGoogleLinked = false;

                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage(message);
                    updateGoogleButtons();
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showMessage(error);
                });
            }
        });
    }

    // サーバーでのユーザー状態を検証
    private void validateUserOnServer() {
        String playerId = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);
        if (playerId.isEmpty()) {
            return;
        }

        googleAuthManager.validateUser(playerId, new GoogleAuthManager.ValidateUserCallback() {
            @Override
            public void onSuccess(boolean exists, boolean isGoogleLinked, String message) {
                runOnUiThread(() -> {
                    if (!exists) {
                        // ユーザーが削除されている場合
                        String errorMessage = "別端末からユーザーが削除されました。";
                        showMessage(errorMessage);

                        // ローカルデータをクリア
                        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");
                        googleAuthManager.saveGoogleLinkStatus(FlareSkillNote.this, playerId, false);

                        // 未登録画面に切り替え
                        playerNameEditText.setText("");
                        viewFlipper.setDisplayedChild(0);
                    } else if (FlareSkillNote.this.isGoogleLinked != isGoogleLinked) {
                        // Google連携状態が変更されている場合
                        FlareSkillNote.this.isGoogleLinked = isGoogleLinked;
                        googleAuthManager.saveGoogleLinkStatus(FlareSkillNote.this, playerId, isGoogleLinked);

                        // UIを更新
                        updateGoogleButtons();

                        if (!isGoogleLinked) {
                            showMessage("別端末からGoogleアカウントとの連携が解除されました。");
                        }
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                // エラーの場合は無視してUIの状態を維持
            }
        });
    }

    private void setupUrlButton(int buttonId, final String url) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> openUrl(url));
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void updateGoogleButtons() {
        // Google連携状態に応じてViewFlipperの表示を切り替え
        googleButtonsFlipper.setDisplayedChild(isGoogleLinked ? 1 : 0);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.flarenote_processing));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessage(String message) {
        runOnUiThread(() -> {
            // 両方のメッセージ表示欄に同じ内容を表示する
            messageTextUnregistered.setText(message);
            messageTextRegistered.setText(message);
        });
    }
}
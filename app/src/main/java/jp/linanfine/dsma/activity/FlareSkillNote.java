package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
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
    private TextView messageTextView;
    private OkHttpClient client;

    public void initialize() {
        usernameEditText = findViewById(R.id.playerName);
        registerButton = findViewById(R.id.registerButton);
        messageTextView = findViewById(R.id.messageText);
        goToUserSiteButton = findViewById(R.id.goToFlareNoteUserSite);
        client = new OkHttpClient();

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            createUser(username);
        });

        this.<Button>findViewById(R.id.deleteUserButton).setOnClickListener(v -> deleteUser());

        this.<Button>findViewById(R.id.sendDataButton).setOnClickListener(v -> sendData());

        String name = FileReader.readFlareSkillNotePlayerName(FlareSkillNote.this);
        setupUrlButton(R.id.goToFlareNoteTop, "https://flarenote.fia-s.com");
        setupUrlButton(R.id.goToFlareNoteUserSite, "https://flarenote.fia-s.com/personal-skill/" + name);

        String playerName = FileReader.readFlareSkillNotePlayerName(FlareSkillNote.this);
        if (!playerName.isEmpty()) {
            usernameEditText.setText(playerName);
            inUserRegisteredMode();
        } else {
            inNoUserMode();
        }

        TextView howToUseTitle = findViewById(R.id.howToUseTitle);
        final TextView howToUseContent = findViewById(R.id.howToUseContent);

        howToUseTitle.setOnClickListener(v -> {
            if (howToUseContent.getVisibility() == View.VISIBLE) {
                howToUseContent.setVisibility(View.GONE);
            } else {
                howToUseContent.setVisibility(View.VISIBLE);
            }
        });
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

    private void createUser(String username) {

        if (username.isEmpty()) {
            messageTextView.setText(this.getResources().getString(R.string.enter_username_and_press_button));
            return;
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", username);  // "username" から "name" に変更
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
                runOnUiThread(() -> messageTextView.setText(getString(R.string.flarenote_network_error, e.getMessage())));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("name");
                            messageTextView.setText(getString(R.string.flarenote_success_create_user, name));
                            String id = jsonResponse.getString("id");
                            FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, id, name);

                            inUserRegisteredMode();
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("detail", "詳細なし");
                            messageTextView.setText(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        messageTextView.setText(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                });
            }
        });
    }

    private void deleteUser() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);

        if (id.isEmpty()) {
            return;
        }

        FileReader.saveFlareSkillNotePlayerId(FlareSkillNote.this, "", "");

        inNoUserMode();

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id", id);  // "username" から "name" に変更
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
                runOnUiThread(() -> messageTextView.setText(getString(R.string.flarenote_network_error, e.getMessage())));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            String name = jsonResponse.getString("user");
                            messageTextView.setText(getString(R.string.flarenote_user_deleted, name));

                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("detail", "詳細なし");
                            messageTextView.setText(getString(R.string.flarenote_error_detail, error, detail));
                        }
                    } catch (JSONException e) {
                        messageTextView.setText(getString(R.string.flarenote_fail_response_analyze, responseData));
                    }
                });
            }
        });
    }

    private void sendData() {
        String id = FileReader.readFlareSkillNotePlayerId(FlareSkillNote.this);

        if (id.isEmpty()) {
            messageTextView.setText("ユーザーを登録してからボタンを押してください");
            return;
        }

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
                runOnUiThread(() -> messageTextView.setText("ネットワークエラー: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseData = response.body().string();
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        if (response.isSuccessful()) {
                            messageTextView.setText("データが正常に送信されました。");
                        } else {
                            String error = jsonResponse.getString("error");
                            String detail = jsonResponse.optString("details", "詳細なし");
                            messageTextView.setText("エラー: " + error + "\n詳細: " + detail);
                        }
                    } catch (JSONException e) {
                        messageTextView.setText("レスポンスの解析に失敗しました: " + responseData);
                    }
                });
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

    private void inUserRegisteredMode() {
        registerButtonEnable(registerButton, false);
        textEditEnabled(false);
        registerButtonEnable(goToUserSiteButton, true);
    }

    private void inNoUserMode() {
        registerButtonEnable(registerButton, true);
        textEditEnabled(true);
        registerButtonEnable(goToUserSiteButton, false);
    }

    private void textEditEnabled(boolean enabled) {
        usernameEditText.setEnabled(enabled);
        usernameEditText.invalidate();
    }

    private void registerButtonEnable(Button registerButton, boolean enabled) {
        registerButton.setEnabled(enabled);
        registerButton.invalidate();
    }
}
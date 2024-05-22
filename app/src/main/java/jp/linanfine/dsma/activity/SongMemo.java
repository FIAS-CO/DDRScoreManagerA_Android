package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.database.DatabaseClient;
import jp.linanfine.dsma.database.Memo;
import jp.linanfine.dsma.database.MemoDao;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;

public class SongMemo extends Activity {

    private void initialize() {
        Intent intent = getIntent();

        int songId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
        String songName = intent.getStringExtra("jp.linanfine.dsma.musicname");
        ((TextView) this.findViewById(R.id.musicName)).setText(songName);

        DatabaseClient dbClient = DatabaseClient.getInstance(this);
        EditText memoView = this.findViewById(R.id.editTextTextMultiLine);
        MemoDao memoDao = dbClient.getAppDatabase().memoDao();
        new Thread(() -> {
            Memo memo = memoDao.findMemoById(songId);
            if (memo != null) {
                runOnUiThread(() -> memoView.setText(memo.text));
            }
        }).start();
        Button cancel = this.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> SongMemo.this.finish());

        Button ok = this.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            new Thread(() -> memoDao.upsert(new Memo(songId, memoView.getText().toString()))).start();
            SongMemo.this.finish();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_song_memo);

        initialize();
    }

}

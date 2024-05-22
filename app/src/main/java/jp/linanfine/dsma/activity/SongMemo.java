package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.database.DatabaseClient;
import jp.linanfine.dsma.database.Memo;
import jp.linanfine.dsma.database.MemoDao;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicData;

public class SongMemo extends Activity {

    private void initialize() {
        Intent intent = getIntent();

        int mItemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);

        TreeMap<Integer, MusicData> mMusicList = FileReader.readMusicList(this);

        MusicData item = mMusicList.get(mItemId);
        assert item != null;
        ((TextView) this.findViewById(R.id.musicName)).setText(item.Name);

        DatabaseClient dbClient = DatabaseClient.getInstance(this);
        EditText memoView = this.findViewById(R.id.editTextTextMultiLine);
        MemoDao memoDao = dbClient.getAppDatabase().memoDao();
        new Thread(() -> {
            Memo memo = memoDao.findMemoById(mItemId);
            if (memo != null) {
                runOnUiThread(() -> memoView.setText(memo.text));
            }
        }).start();
        Button cancel = this.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> SongMemo.this.finish());

        Button ok = this.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            new Thread(() -> memoDao.upsert(new Memo(mItemId, memoView.getText().toString()))).start();
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

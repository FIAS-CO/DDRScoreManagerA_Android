package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicData;

public class SongMemo extends Activity {

    private void initialize() {
        Intent intent = getIntent();

        int mItemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);

        TreeMap<Integer, MusicData> mMusicList = FileReader.readMusicList(this);
        View memoView = this.findViewById(R.id.editTextTextMultiLine);

        MusicData item = mMusicList.get(mItemId);
        assert item != null;
        ((TextView) this.findViewById(R.id.musicName)).setText(item.Name);

        Button cancel = this.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> SongMemo.this.finish());

        Button ok = this.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
//            mScoreList.put(mItemId, scoredata);
//            FileReader.saveScoreData(SongMemo.this, mRivalId, mScoreList);
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

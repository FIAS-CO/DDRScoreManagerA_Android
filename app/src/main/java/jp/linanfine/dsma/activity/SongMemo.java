package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;

public class SongMemo extends Activity {

    private int mItemId;
    private PatternType mPattern;
    private TreeMap<Integer, MusicData> mMusicList;
    private TreeMap<Integer, MusicScore> mScoreList;

    MusicData item;
    MusicScore scoredata;

    TextView scoreView;

    private int mScore;

    Handler mHandler = new Handler();
    View mHandledView;

    private void setView() {

        scoreView.setText(TextUtil.getScoreText(mScore));



    }

    private void initialize() {

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mItemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
        mPattern = (PatternType) intent.getSerializableExtra("jp.linanfine.dsma.pattern");
//        mRivalId = intent.getStringExtra("jp.linanfine.dsma.rivalid");
//        mRivalName = intent.getStringExtra("jp.linanfine.dsma.rivalname");

        mMusicList = FileReader.readMusicList(this);
//        mScoreList = FileReader.readScoreList(this, mRivalId);

//        if (mRivalId != null) {
//            ((TextView) this.findViewById(R.id.title)).setText(mRivalName);
//        }

        View memoView = this.findViewById(R.id.editTextTextMultiLine);


        TextView textView;

        textView = (TextView) this.findViewById(R.id.musicName);
        textView.setText(mMusicList.get(mItemId).Name);

        textView = (TextView) this.findViewById(R.id.patternType);
        //textView.setText(mPattern.toString());

        View s = this.findViewById(R.id.patternTypeS);
        View d = this.findViewById(R.id.patternTypeD);
        View sd = this.findViewById(R.id.patternTypeSD);

        item = mMusicList.get(mItemId);
        if (mScoreList.containsKey(mItemId)) {
            scoredata = mScoreList.get(mItemId);
            mScoreList.remove(mItemId);
        } else {
            scoredata = new MusicScore();
        }

        scoreView = (TextView) this.findViewById(R.id.score);

        MusicRank rankData = MusicRank.Noplay;
        FullComboType fc = FullComboType.None;
        int combo = 0;
        int score = 0;
        int play = 0;
        int clear = 0;
        switch (mPattern) {
            case bSP:
                rankData = scoredata.bSP.Rank;
                score = scoredata.bSP.Score;
                combo = scoredata.bSP.MaxCombo;
                play = scoredata.bSP.PlayCount;
                clear = scoredata.bSP.ClearCount;
                fc = scoredata.bSP.FullComboType;
                sd.setBackgroundColor(0xff66ffff);
                s.setBackgroundColor(0xff66ffff);
                d.setBackgroundColor(0xff000000);
                textView.setText("SINGLE BEGINNER");
                break;
            case BSP:
                rankData = scoredata.BSP.Rank;
                score = scoredata.BSP.Score;
                combo = scoredata.BSP.MaxCombo;
                play = scoredata.BSP.PlayCount;
                clear = scoredata.BSP.ClearCount;
                fc = scoredata.BSP.FullComboType;
                sd.setBackgroundColor(0xffff9900);
                s.setBackgroundColor(0xffff9900);
                d.setBackgroundColor(0xff000000);
                textView.setText("SINGLE BASIC");
                break;
            case DSP:
                rankData = scoredata.DSP.Rank;
                score = scoredata.DSP.Score;
                combo = scoredata.DSP.MaxCombo;
                play = scoredata.DSP.PlayCount;
                clear = scoredata.DSP.ClearCount;
                fc = scoredata.DSP.FullComboType;
                sd.setBackgroundColor(0xffff0000);
                s.setBackgroundColor(0xffff0000);
                d.setBackgroundColor(0xff000000);
                textView.setText("SINGLE DIFFICULT");
                break;
            case ESP:
                rankData = scoredata.ESP.Rank;
                score = scoredata.ESP.Score;
                combo = scoredata.ESP.MaxCombo;
                play = scoredata.ESP.PlayCount;
                clear = scoredata.ESP.ClearCount;
                fc = scoredata.ESP.FullComboType;
                sd.setBackgroundColor(0xff00ff00);
                s.setBackgroundColor(0xff00ff00);
                d.setBackgroundColor(0xff000000);
                textView.setText("SINGLE EXPERT");
                break;
            case CSP:
                rankData = scoredata.CSP.Rank;
                score = scoredata.CSP.Score;
                combo = scoredata.CSP.MaxCombo;
                play = scoredata.CSP.PlayCount;
                clear = scoredata.CSP.ClearCount;
                fc = scoredata.CSP.FullComboType;
                sd.setBackgroundColor(0xffff00ff);
                s.setBackgroundColor(0xffff00ff);
                d.setBackgroundColor(0xff000000);
                textView.setText("SINGLE CHALLENGE");
                break;
            case BDP:
                rankData = scoredata.BDP.Rank;
                score = scoredata.BDP.Score;
                combo = scoredata.BDP.MaxCombo;
                play = scoredata.BDP.PlayCount;
                clear = scoredata.BDP.ClearCount;
                fc = scoredata.BDP.FullComboType;
                sd.setBackgroundColor(0xffff9900);
                d.setBackgroundColor(0xffff9900);
                s.setBackgroundColor(0xff000000);
                textView.setText("DOUBLE BASIC");
                break;
            case DDP:
                rankData = scoredata.DDP.Rank;
                score = scoredata.DDP.Score;
                combo = scoredata.DDP.MaxCombo;
                play = scoredata.DDP.PlayCount;
                clear = scoredata.DDP.ClearCount;
                fc = scoredata.DDP.FullComboType;
                sd.setBackgroundColor(0xffff0000);
                d.setBackgroundColor(0xffff0000);
                s.setBackgroundColor(0xff000000);
                textView.setText("DOUBLE DIFFICULT");
                break;
            case EDP:
                rankData = scoredata.EDP.Rank;
                score = scoredata.EDP.Score;
                combo = scoredata.EDP.MaxCombo;
                play = scoredata.EDP.PlayCount;
                clear = scoredata.EDP.ClearCount;
                fc = scoredata.EDP.FullComboType;
                sd.setBackgroundColor(0xff00ff00);
                d.setBackgroundColor(0xff00ff00);
                s.setBackgroundColor(0xff000000);
                textView.setText("DOUBLE EXPERT");
                break;
            case CDP:
                rankData = scoredata.CDP.Rank;
                score = scoredata.CDP.Score;
                combo = scoredata.CDP.MaxCombo;
                play = scoredata.CDP.PlayCount;
                clear = scoredata.CDP.ClearCount;
                fc = scoredata.CDP.FullComboType;
                sd.setBackgroundColor(0xffff00ff);
                d.setBackgroundColor(0xffff00ff);
                s.setBackgroundColor(0xff000000);
                textView.setText("DOUBLE CHALLENGE");
                break;
            default:
                break;
        }

        mScore = score;

        Button cancel = (Button) this.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> SongMemo.this.finish());

        Button ok = (Button) this.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            mScoreList.put(mItemId, scoredata);
//            FileReader.saveScoreData(SongMemo.this, mRivalId, mScoreList);
            SongMemo.this.finish();
        });

        setView();

    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
        FileReader.requestAd((LinearLayout) this.findViewById(R.id.adContainer), this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_score_edit);

        initialize();
    }

}

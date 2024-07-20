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

public class ScoreEdit extends Activity {

    private int mItemId;
    private PatternType mPattern;
    private TreeMap<Integer, MusicData> mMusicList;
    private TreeMap<Integer, MusicScore> mScoreList;
    private String mRivalId;
    private String mRivalName;

    MusicData item;
    MusicScore scoredata;

    Spinner stateView;
    TextView scoreView;
    TextView comboView;
    TextView playView;
    TextView clearView;

    private int mScore;
    private int mCombo;
    private int mPlay;
    private int mClear;
    private FullComboType mFc;
    private MusicRank mRank;

    Handler mHandler = new Handler();
    View mHandledView;

    private void setView() {

        scoreView.setText(TextUtil.getScoreText(mScore));
        comboView.setText(String.valueOf(mCombo));
        playView.setText(String.valueOf(mPlay));
        clearView.setText(String.valueOf(mClear));

        if (mFc == FullComboType.MerverousFullCombo) {
            stateView.setSelection(0, true);
        } else if (mFc == FullComboType.PerfectFullCombo) {
            stateView.setSelection(1, true);
        } else if (mFc == FullComboType.FullCombo) {
            stateView.setSelection(2, true);
        } else if (mFc == FullComboType.GoodFullCombo) {
            stateView.setSelection(3, true);
        } else if (mFc == FullComboType.Life4) {
            stateView.setSelection(4, true);
        } else if (mFc == FullComboType.None) {
            if (mRank == MusicRank.Noplay) {
                stateView.setSelection(7, true);
            } else if (mRank == MusicRank.E) {
                stateView.setSelection(6, true);
            } else {
                stateView.setSelection(5, true);
            }
        }
    }

    private void initialize() {

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }

        mItemId = intent.getIntExtra("jp.linanfine.dsma.musicid", -1);
        mPattern = (PatternType) intent.getSerializableExtra("jp.linanfine.dsma.pattern");
        mRivalId = intent.getStringExtra("jp.linanfine.dsma.rivalid");
        mRivalName = intent.getStringExtra("jp.linanfine.dsma.rivalname");

        mMusicList = FileReader.readMusicList(this);
        mScoreList = FileReader.readScoreList(this, mRivalId);

        if (mRivalId != null) {
            ((TextView) this.findViewById(R.id.title)).setText(mRivalName);
        }

        View editScore = this.findViewById(R.id.editScore);
        editScore.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) ScoreEdit.this.getLayoutInflater().inflate(R.layout.view_digit_edit_text, null).findViewById(R.id.editText);
            editView.setText(String.valueOf(mScore));
            editView.setOnFocusChangeListener((view1, focused) -> {
                if (focused) {
                    mHandledView = view1;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(ScoreEdit.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(ScoreEdit.this.getResources().getString(R.string.dialog_input_score))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton(ScoreEdit.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                        Editable text = editView.getText();
                        try {
                            int value = Integer.valueOf(text.toString());
                            if (0 <= value && value <= 1000000) {
                                mScore = value;
                                ScoreEdit.this.setView();
                            }
                        } catch (Exception e) {
                        }
                    })
                    .setNegativeButton(ScoreEdit.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                    })
                    .show();
        });

        View editMaxCombo = this.findViewById(R.id.editMaxCombo);
        editMaxCombo.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) ScoreEdit.this.getLayoutInflater().inflate(R.layout.view_digit_edit_text, null).findViewById(R.id.editText);
            editView.setText(String.valueOf(mCombo));
            editView.setOnFocusChangeListener((view12, focused) -> {
                if (focused) {
                    mHandledView = view12;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(ScoreEdit.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(ScoreEdit.this.getResources().getString(R.string.dialog_input_max_combo))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton(ScoreEdit.this.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                            Editable text = editView.getText();
                            try {
                                int value = Integer.valueOf(text.toString());
                                if (0 <= value && value <= 1000000) {
                                    mCombo = value;
                                    ScoreEdit.this.setView();
                                }
                            } catch (Exception e) {
                            }
                        }
                    })
                    .setNegativeButton(ScoreEdit.this.getResources().getString(R.string.strings_global____cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                        }
                    })
                    .show();
        });

        View editPlayCount = this.findViewById(R.id.editPlayCount);
        editPlayCount.setOnClickListener(view -> {
            //テキスト入力を受け付けるビューを作成します。
            final EditText editView = (EditText) ScoreEdit.this.getLayoutInflater().inflate(R.layout.view_digit_edit_text, null).findViewById(R.id.editText);
            editView.setText(String.valueOf(mPlay));
            editView.setOnFocusChangeListener((view13, focused) -> {
                if (focused) {
                    mHandledView = view13;
                    mHandler.post(() -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                    });
                }
            });
            new AlertDialog.Builder(ScoreEdit.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle(ScoreEdit.this.getResources().getString(R.string.dialog_input_play_count))
                    //setViewにてビューを設定します。
                    .setView(editView)
                    .setPositiveButton(ScoreEdit.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                        Editable text = editView.getText();
                        try {
                            int value = Integer.valueOf(text.toString());
                            if (0 <= value) {
                                mPlay = value;
                                ScoreEdit.this.setView();
                            }
                        } catch (Exception e) {
                        }
                    })
                    .setNegativeButton(ScoreEdit.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                    })
                    .show();
        });

        View editClearCount = this.findViewById(R.id.editClearCount);
        editClearCount.setOnClickListener(view -> {
                    //テキスト入力を受け付けるビューを作成します。
                    final EditText editView = (EditText) ScoreEdit.this.getLayoutInflater().inflate(R.layout.view_digit_edit_text, null).findViewById(R.id.editText);
                    editView.setText(String.valueOf(mClear));
                    editView.setOnFocusChangeListener((view14, focused) -> {
                        if (focused) {
                            mHandledView = view14;
                            mHandler.post(new Runnable() {
                                public void run() {
                                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.showSoftInput(mHandledView, InputMethodManager.SHOW_FORCED);
                                }
                            });
                        }
                    });
                    new AlertDialog.Builder(ScoreEdit.this)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle(ScoreEdit.this.getResources().getString(R.string.dialog_input_clear_count))
                            //setViewにてビューを設定します。
                            .setView(editView)
                            .setPositiveButton(ScoreEdit.this.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                                Editable text = editView.getText();
                                try {
                                    int value = Integer.valueOf(text.toString());
                                    if (0 <= value) {
                                        mClear = value;
                                        ScoreEdit.this.setView();
                                    }
                                } catch (Exception e) {
                                }
                            })
                            .setNegativeButton(ScoreEdit.this.getResources().getString(R.string.strings_global____cancel), (dialog, whichButton) -> {
                                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(mHandledView.getWindowToken(), 0);
                            })
                            .show();
                }
        );

        TextView textView;

        textView = (TextView) this.findViewById(R.id.musicName);
        textView.setText(mMusicList.get(mItemId).Name);

        textView = (TextView) this.findViewById(R.id.patternType);

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

        stateView = (Spinner) this.findViewById(R.id.state);
        scoreView = (TextView) this.findViewById(R.id.score);
        comboView = (TextView) this.findViewById(R.id.maxCombo);
        playView = (TextView) this.findViewById(R.id.playCount);
        clearView = (TextView) this.findViewById(R.id.clearCount);

        ((Button) this.findViewById(R.id.clear)).setOnClickListener(view -> {
            mScore = 0;
            mCombo = 0;
            mPlay = 0;
            mClear = 0;
            mFc = FullComboType.None;
            mRank = MusicRank.Noplay;
            setView();
        });

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
        mCombo = combo;
        mPlay = play;
        mClear = clear;
        mFc = fc;
        mRank = rankData;

        Button cancel = (Button) this.findViewById(R.id.cancel);
        cancel.setOnClickListener(view -> ScoreEdit.this.finish());

        Button ok = (Button) this.findViewById(R.id.ok);
        ok.setOnClickListener(view -> {
            MusicRank dataRank;
            int dataScore = mScore;
            int dataCombo = mCombo;
            int dataPlay = mPlay;
            int dataClear = mClear;
            if (dataScore < 550000) {
                dataRank = MusicRank.D;
            } else if (dataScore < 590000) {
                dataRank = MusicRank.Dp;
            } else if (dataScore < 600000) {
                dataRank = MusicRank.Cm;
            } else if (dataScore < 650000) {
                dataRank = MusicRank.C;
            } else if (dataScore < 690000) {
                dataRank = MusicRank.Cp;
            } else if (dataScore < 700000) {
                dataRank = MusicRank.Bm;
            } else if (dataScore < 750000) {
                dataRank = MusicRank.B;
            } else if (dataScore < 790000) {
                dataRank = MusicRank.Bp;
            } else if (dataScore < 800000) {
                dataRank = MusicRank.Am;
            } else if (dataScore < 850000) {
                dataRank = MusicRank.A;
            } else if (dataScore < 890000) {
                dataRank = MusicRank.Ap;
            } else if (dataScore < 900000) {
                dataRank = MusicRank.AAm;
            } else if (dataScore < 950000) {
                dataRank = MusicRank.AA;
            } else if (dataScore < 990000) {
                dataRank = MusicRank.AAp;
            } else {
                dataRank = MusicRank.AAA;
            }
            FullComboType dataFC;
            switch (stateView.getSelectedItemPosition()) {
                case 0:
                    dataFC = FullComboType.MerverousFullCombo;
                    break;
                case 1:
                    dataFC = FullComboType.PerfectFullCombo;
                    break;
                case 2:
                    dataFC = FullComboType.FullCombo;
                    break;
                case 3:
                    dataFC = FullComboType.GoodFullCombo;
                    break;
                case 4:
                    dataFC = FullComboType.Life4;
                    break;
                case 5:
                    dataFC = FullComboType.None;
                    break;
                case 6:
                    dataFC = FullComboType.None;
                    dataRank = MusicRank.E;
                    break;
                default:
                    dataFC = FullComboType.None;
                    dataRank = MusicRank.Noplay;
                    break;
            }
            switch (mPattern) {
                case bSP:
                    scoredata.bSP.Rank = dataRank;
                    scoredata.bSP.Score = dataScore;
                    scoredata.bSP.MaxCombo = dataCombo;
                    scoredata.bSP.PlayCount = dataPlay;
                    scoredata.bSP.ClearCount = dataClear;
                    scoredata.bSP.FullComboType = dataFC;
                    break;
                case BSP:
                    scoredata.BSP.Rank = dataRank;
                    scoredata.BSP.Score = dataScore;
                    scoredata.BSP.MaxCombo = dataCombo;
                    scoredata.BSP.PlayCount = dataPlay;
                    scoredata.BSP.ClearCount = dataClear;
                    scoredata.BSP.FullComboType = dataFC;
                    break;
                case DSP:
                    scoredata.DSP.Rank = dataRank;
                    scoredata.DSP.Score = dataScore;
                    scoredata.DSP.MaxCombo = dataCombo;
                    scoredata.DSP.PlayCount = dataPlay;
                    scoredata.DSP.ClearCount = dataClear;
                    scoredata.DSP.FullComboType = dataFC;
                    break;
                case ESP:
                    scoredata.ESP.Rank = dataRank;
                    scoredata.ESP.Score = dataScore;
                    scoredata.ESP.MaxCombo = dataCombo;
                    scoredata.ESP.PlayCount = dataPlay;
                    scoredata.ESP.ClearCount = dataClear;
                    scoredata.ESP.FullComboType = dataFC;
                    break;
                case CSP:
                    scoredata.CSP.Rank = dataRank;
                    scoredata.CSP.Score = dataScore;
                    scoredata.CSP.MaxCombo = dataCombo;
                    scoredata.CSP.FullComboType = dataFC;
                    break;
                case BDP:
                    scoredata.BDP.Rank = dataRank;
                    scoredata.BDP.Score = dataScore;
                    scoredata.BDP.MaxCombo = dataCombo;
                    scoredata.BDP.PlayCount = dataPlay;
                    scoredata.BDP.ClearCount = dataClear;
                    scoredata.BDP.FullComboType = dataFC;
                    break;
                case DDP:
                    scoredata.DDP.Rank = dataRank;
                    scoredata.DDP.Score = dataScore;
                    scoredata.DDP.MaxCombo = dataCombo;
                    scoredata.DDP.PlayCount = dataPlay;
                    scoredata.DDP.ClearCount = dataClear;
                    scoredata.DDP.FullComboType = dataFC;
                    break;
                case EDP:
                    scoredata.EDP.Rank = dataRank;
                    scoredata.EDP.Score = dataScore;
                    scoredata.EDP.MaxCombo = dataCombo;
                    scoredata.EDP.PlayCount = dataPlay;
                    scoredata.EDP.ClearCount = dataClear;
                    scoredata.EDP.FullComboType = dataFC;
                    break;
                case CDP:
                    scoredata.CDP.Rank = dataRank;
                    scoredata.CDP.Score = dataScore;
                    scoredata.CDP.MaxCombo = dataCombo;
                    scoredata.CDP.PlayCount = dataPlay;
                    scoredata.CDP.ClearCount = dataClear;
                    scoredata.CDP.FullComboType = dataFC;
                    break;
                default:
                    break;
            }
            mScoreList.put(mItemId, scoredata);
            FileReader.saveScoreData(ScoreEdit.this, mRivalId, mScoreList);
            ScoreEdit.this.finish();
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
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_score_edit);

        initialize();
    }
}

package jp.linanfine.dsma.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.util.html.DifficultyScore;
import jp.linanfine.dsma.util.html.HtmlParseUtil;
import jp.linanfine.dsma.util.html.MusicEntry;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicId;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.WebTitleToMusicIdList;
import jp.linanfine.dsma.value._enum.FullComboType;

public class DialogFromGateList {

    public static int LoginRequestCode = 20001;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    private TreeMap<Integer, MusicData> mMusicList;
    private TreeMap<Integer, MusicScore> mScoreList;
    private WebTitleToMusicIdList mMusicIds;
    private WebView mWebView;
    private ProgressBar mWebProgress;
    private GateSetting mGateSetting;
    private boolean mDouble = false;
    private String mRivalId;
    private String mRivalName;

    private int mPageCount = 0;
    private int mCurrentPage = 0;
    private TextView mLogView = null;
    private ProgressBar mProgress = null;
    private TextView mPercent = null;
    private TextView mCurrent = null;
    private TextView mMax = null;

    private String mUriH;
    private String mUriF;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateList(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate_list, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mMusicList = FileReader.readMusicList(mParent);
        mMusicIds = FileReader.readWebMusicIds(mParent).toWebTitleToMusicIdList();
        mGateSetting = FileReader.readGateSetting(mParent);

        mWebProgress = mView.findViewById(R.id.webProgress);
        mLogView = mView.findViewById(R.id.log);
        mPercent = mView.findViewById(R.id.percent);
        mCurrent = mView.findViewById(R.id.current);
        mMax = mView.findViewById(R.id.max);
        mProgress = mView.findViewById(R.id.mainProgress);

        mLogView.setText(mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //Log.d("huga", url);
                view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                //Log.d("hage", url);
                mWebProgress.setProgress(1);
            }
        };

        WebChromeClient chrome = new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //try { Thread.sleep(10);} catch (InterruptedException e) {}
                mWebProgress.setProgress(1 + progress);
                mProgress.setProgress(mCurrentPage * 100 + (mPageCount == 0 ? 0 : progress));
                mPercent.setText((100 * mCurrentPage + (mPageCount == 0 ? 0 : progress)) / (mPageCount == 0 ? 10000 : mPageCount) + "%");
            }
        };

        mWebView = mView.findViewById(R.id.webView);
        //mWebView.getSettings().setBlockNetworkImage(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(client);
        mWebView.setWebChromeClient(chrome);
        //mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "viewsourceactivity");
    }

    public void setArguments(AlertDialog dialog, boolean getDouble, String rivalId, String rivalName) {
        mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mDouble = getDouble;
        mScoreList = FileReader.readScoreList(mParent, mRivalId);
    }

    public View getView() {
        return mView;
    }

    public void start() {
        if (mDialog == null) {
            return;
        }
        FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);
        String sd;
        if (mDouble) {
            sd = "double";
        } else {
            sd = "single";
        }

        mGateSetting = FileReader.readGateSetting(mParent);
        if (mGateSetting.FromNewSite) {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        } else {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
        }

        if (mRivalId == null) {
            mUriH += "playdata/music_data_" + sd + ".html?offset=";
            mUriF = "";
        } else {
            mUriH += "rival/rival_musicdata_" + sd + ".html?offset=";
            mUriF = "&rival_id=" + mRivalId;
        }
        //String uri = "file:///android_asset/status.html";
        mLogView.setText((mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + (mDouble ? "DP\n" : "SP\n") + mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));
        mWebView.loadUrl(mUriH + "0" + mUriF);
    }

    public void cancel() {
        mCanceled = true;
        WebView web = mView.findViewById(R.id.webView);
        web.stopLoading();
        mPageCount = 1;
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        //Log.d("list", src);
        mHandler.post(() -> {
            mWebProgress.setProgress(0);

            if (mCanceled) {
                return;
            }

            if (mPageCount == 0) {
                mPageCount = getPageCount(src);
                if (mPageCount == 0) {
                    switch (TextUtil.checkLoginStatus(src)) {
                        case NOT_LOGGED_IN:
                            Intent intent = new Intent();
                            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                            cancel();

                            mParent.startActivityForResult(intent, LoginRequestCode);
                            (new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                mHandler.post(() -> mDialog.cancel());
                            }
                            )).start();

                            return;
                        case UNKNOWN:
                            Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
                            (new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                mHandler.post(() -> mDialog.cancel());
                            }
                            )).start();
                            return;
                    }
                }
                mMax.setText(String.valueOf(mPageCount));
                mProgress.setMax(mPageCount * 100);
            }

            try {
                analyzeScoreList(src);
            } catch (Exception e) {
                return;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            ++mCurrentPage;
            mProgress.setProgress(mCurrentPage * 100);
            mLogView.setText(String.format("%s%s%s",
                    mRivalName == null ? "My Score\n"
                            : ("Rival: " + mRivalName + "\n"),
                    mDouble ? "DP\n" : "SP\n",
                    mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPage)));
            mCurrent.setText(String.valueOf(mCurrentPage));
            mPercent.setText((mPageCount == 0 ? 0 : (100 * mCurrentPage / mPageCount)) + "%");
            if (mCurrentPage >= mPageCount) {
                Toast.makeText(mParent, "Finish.", Toast.LENGTH_LONG).show();
                (new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    mHandler.post(() -> mDialog.cancel());
                }
                )).start();
                return;
            }
            mWebView.loadUrl(mUriH + mCurrentPage + mUriF);
        });
    }

    private int getPageCount(String src) {
        String cmpStartPagerBox = "<div id=\"paging_box\">";
        String cmpEndPagerBox = "<div class=\"arrow\"";
        String cmpPageNum = "<div class=\"page_num\"";

        int pointStartPagerBox = src.indexOf(cmpStartPagerBox);
        if (pointStartPagerBox < 0) {
            return 0;
        }
        String blockPagerBox = src.substring(pointStartPagerBox);
        blockPagerBox = blockPagerBox.substring(0, blockPagerBox.indexOf(cmpEndPagerBox));
        return countStringInString(blockPagerBox, cmpPageNum);
    }

    private static int countStringInString(String target, String searchWord) {
        return (target.length() - target.replaceAll(searchWord, "").length()) / searchWord.length();
    }

    private void analyzeScoreList(String src) {
        List<MusicEntry> musicEntries = HtmlParseUtil.parseMusicList(src);

        boolean scoreExists = false;
        for (MusicEntry entry : musicEntries) {
            MusicId mi = mMusicIds.get(entry.getMusicName());
            if (mi == null) continue;

            int musicIdSaved = mi.musicId;

            MusicScore ms = mScoreList.get(musicIdSaved);
            if (ms == null) {
                ms = new MusicScore();
            }

            for (DifficultyScore diffScore : entry.getScores()) {
                ScoreData sd = new ScoreData();
                sd.Score = diffScore.getScore();
                sd.Rank = diffScore.getRank();
                sd.FullComboType = diffScore.getFullComboType();

                ScoreData msd = getScoreDataForDifficulty(ms, diffScore.getDifficultyId());
                updateScoreData(sd, msd);

                setScoreDataForDifficulty(ms, diffScore.getDifficultyId(), sd);
                scoreExists = true;
            }

            mScoreList.put(musicIdSaved, ms);
        }

        if (scoreExists) {
            FileReader.saveScoreData(mParent, mRivalId, mScoreList);
        }
    }

    private ScoreData getScoreDataForDifficulty(MusicScore ms, String diffId) {
        switch (diffId) {
            case "beginner":
                return ms.bSP;
            case "basic":
                return mDouble ? ms.BDP : ms.BSP;
            case "difficult":
                return mDouble ? ms.DDP : ms.DSP;
            case "expert":
                return mDouble ? ms.EDP : ms.ESP;
            case "challenge":
                return mDouble ? ms.CDP : ms.CSP;
            default:
                return new ScoreData();
        }
    }

    private void setScoreDataForDifficulty(MusicScore ms, String diffId, ScoreData sd) {
        switch (diffId) {
            case "beginner":
                ms.bSP = sd;
                break;
            case "basic":
                if (mDouble) ms.BDP = sd;
                else ms.BSP = sd;
                break;
            case "difficult":
                if (mDouble) ms.DDP = sd;
                else ms.DSP = sd;
                break;
            case "expert":
                if (mDouble) ms.EDP = sd;
                else ms.ESP = sd;
                break;
            case "challenge":
                if (mDouble) ms.CDP = sd;
                else ms.CSP = sd;
                break;
        }
    }

    private void updateScoreData(ScoreData sd, ScoreData msd) {
        // 「Life4 に未フルコンを上書きする」 が無効
        if (!mGateSetting.OverWriteLife4) {
            if (sd.FullComboType == FullComboType.None && msd.FullComboType == FullComboType.Life4) {
                sd.FullComboType = msd.FullComboType;
            }
        }

        if (!mGateSetting.OverWriteLowerScores) {
            // スコアが低かったら
            if (sd.Score < msd.Score) {
                sd.Score = msd.Score;
                sd.Rank = msd.Rank;
            }
            // コンボが低かったら
            if (sd.MaxCombo < msd.MaxCombo) {
                sd.MaxCombo = msd.MaxCombo;
            }
            // フルコンボタイプの更新
            if (msd.FullComboType.ordinal() > sd.FullComboType.ordinal()) {
                sd.FullComboType = msd.FullComboType;
            }
        }

        // MaxCombo, ClearCount, PlayCount の更新
        sd.MaxCombo = Math.max(sd.MaxCombo, msd.MaxCombo);
        sd.ClearCount = msd.ClearCount;
        sd.PlayCount = msd.PlayCount;
    }
}

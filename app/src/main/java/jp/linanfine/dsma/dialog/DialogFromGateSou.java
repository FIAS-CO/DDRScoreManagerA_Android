package jp.linanfine.dsma.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value.WebMusicId;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;

/**
 * 曲リスト表示後の[GATEサーバから一括取得(詳細)]関連のコード、のはず
 */
public class DialogFromGateSou {

    public static int LoginRequestCode = 20003;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    //private TreeMap<Integer, MusicData> mMusicList;
    private IdToWebMusicIdList mWebMusicIds;
    private TreeMap<Integer, MusicScore> mScoreList;
    private WebView mWebView;
    private ProgressBar mWebProgress;
    private GateSetting mGateSetting;
    private ArrayList<UniquePattern> mSouList;
    private String mRivalId;
    private String mRivalName;

    private int mPageCount = 0;
    private int mCurrentPage = 0;
    private TextView mLogView = null;
    private TextView mLogView2 = null;
    private ProgressBar mProgress = null;
    private TextView mPercent = null;
    private TextView mCurrent = null;
    private TextView mMax = null;

    private String mUriH;
    private String mUriM;
    private String mUriF;
    private String mRequestUri;
    private int mItemId;
    private String mWebItemId;
    private PatternType mPattern;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateSou(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate_list, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mGateSetting = FileReader.readGateSetting(mParent);

        mWebMusicIds = FileReader.readWebMusicIds(mParent);

        mWebProgress = mView.findViewById(R.id.webProgress);
        mLogView = mView.findViewById(R.id.log);
        mLogView2 = mView.findViewById(R.id.log2);
        mPercent = mView.findViewById(R.id.percent);
        mCurrent = mView.findViewById(R.id.current);
        mMax = mView.findViewById(R.id.max);
        mProgress = mView.findViewById(R.id.mainProgress);

        mLogView2.setVisibility(View.VISIBLE);
        mView.findViewById(R.id.log3).setVisibility(View.VISIBLE);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                mWebProgress.setProgress(1);
            }
        };

        WebChromeClient chrome = new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mWebProgress.setProgress(1 + progress);
                mProgress.setProgress(mCurrentPage * 100 + (mPageCount == 0 ? 0 : progress));
                mPercent.setText((100 * mCurrentPage + (mPageCount == 0 ? 0 : progress)) / (mPageCount == 0 ? 10000 : mPageCount) + "%");
            }
        };

        mWebView = mView.findViewById(R.id.webView);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(client);
        mWebView.setWebChromeClient(chrome);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "viewsourceactivity");
    }

    public boolean setArguments(AlertDialog dialog, ArrayList<UniquePattern> souList, String rivalId, String rivalName) {
        mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mSouList = souList;
        mScoreList = FileReader.readScoreList(mParent, mRivalId);
        return true;
    }

    public View getView() {
        return mView;
    }

    private static int getPatternInt(PatternType pattern) {

        return
                pattern == PatternType.bSP ? 0 :
                        pattern == PatternType.BSP ? 1 :
                                pattern == PatternType.DSP ? 2 :
                                        pattern == PatternType.ESP ? 3 :
                                                pattern == PatternType.CSP ? 4 :
                                                        pattern == PatternType.BDP ? 5 :
                                                                pattern == PatternType.DDP ? 6 :
                                                                        pattern == PatternType.EDP ? 7 :
                                                                                pattern == PatternType.CDP ? 8 :
                                                                                        0;

    }

    public void start() {
        if (mDialog == null) {
            return;
        }
        FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);
        mPageCount = mSouList.size();
        if (mPageCount <= 0) {
            return;
        }
        mProgress.setMax(mPageCount * 100);
        mMax.setText(String.valueOf(mPageCount));

        mGateSetting = FileReader.readGateSetting(mParent);
        if (mGateSetting.FromNewSite) {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        } else {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
        }

        if (mRivalId == null) {
            mUriH += "playdata/music_detail.html?index=";
            mUriM = "&diff=";
            mUriF = "";
        } else {
            mUriH += "rival/music_detail.html?index=";
            mUriM = "&diff=";
            mUriF = "&rival_id=" + mRivalId + "&name=" + mRivalName.trim();
        }

        mCurrentPage = 0;
        UniquePattern c = mSouList.get(mCurrentPage);
        mItemId = c.MusicId;
        if (!mWebMusicIds.containsKey(mItemId) || mWebMusicIds.get(mItemId).idOnWebPage.equals("")) {
            new AlertDialog.Builder(mParent)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(mParent.getResources().getString(R.string.illegal_id_alert))
                    .setCancelable(true)
                    .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                    }).show();

            mDialog.cancel();
            return;
        } else {
            mWebItemId = Objects.requireNonNull(mWebMusicIds.get(mItemId)).idOnWebPage;
        }
        mPattern = c.Pattern;
        mRequestUri = mUriH + mWebItemId + mUriM + getPatternInt(mPattern) + mUriF;
        mLogView.setText("Loading...\n" + (mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + mPattern.toString() + " : " + c.musics.get(mItemId).Name);
        mWebView.loadUrl(mRequestUri);
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

    private boolean _analyzeScore(String src) {
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();

        assert uri != null;
        return analyzeScore(src, uri);
    }

    private boolean analyzeScore(String src, String uri) {
        if (!uri.equals(mRequestUri)) {
            if (mRivalId == null) {
                return false;
            } else {
                if (!uri.contains(mRequestUri.split("&name=")[0])) {
                    return false;
                }
            }
        }

        ScoreData scoreData = new ScoreData();
        Document doc = Jsoup.parse(src);

        // 曲名の確認
        Element musicInfoElement = doc.selectFirst("div.music_name table#music_info tr td:eq(1)");
        if (musicInfoElement != null) {
            String songTitle = musicInfoElement.html().split("<br>")[0].trim();
            WebMusicId webMusicId = mWebMusicIds.get(mItemId);
            if (webMusicId == null) {
                // mItemIdに該当するアイテムが存在しない場合の処理
                Log.e("ScoreAnalyzer", "No WebMusicId found for mItemId: " + mItemId);
                Toast.makeText(mParent, "エラー：楽曲情報が見つかりません", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!songTitle.equals(webMusicId.titleOnWebPage)) {
                new AlertDialog.Builder(mParent)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(mParent.getResources().getString(R.string.name_different_alert) + "\n\n" + webMusicId.titleOnWebPage + "\n↓\n" + songTitle)
                        .setCancelable(true)
                        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        })
                        .show();
                return true;
            }
        } else {
            return false;
        }

        if (mCanceled) {
            return true;
        }

        // NO PLAYチェック
        if (doc.text().contains("NO PLAY...")) {
            return true;
        }

        Elements musicDetailRows = doc.select("#music_detail_table tr");
        boolean scoreInfoFound = false;
        for (Element row : musicDetailRows) {
            Elements headers = row.select("th");
            Elements values = row.select("td");
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i).text();
                String value = values.get(i).text();

                if (mGateSetting.FromNewSite) {
                    switch (header) {
                        case "ハイスコア時のランク":
                            scoreData.Rank = parseRank(value);
                            scoreInfoFound = true;
                            break;
                        case "ハイスコア":
                            scoreData.Score = Integer.parseInt(value);
                            break;
                        case "最大コンボ数":
                            scoreData.MaxCombo = Integer.parseInt(value);
                            break;
                        case "フルコンボ種別":
                            scoreData.FullComboType = parseFullComboType(value);
                            break;
                        case "プレー回数":
                            scoreData.PlayCount = Integer.parseInt(value);
                            break;
                        case "クリア回数":
                            scoreData.ClearCount = Integer.parseInt(value);
                            break;
                    }
                } else {
                    if (mRivalId == null) {
                        switch (header) {
                            case "ハイスコア時のダンスレベル":
                                scoreData.Rank = parseRank(value);
                                scoreInfoFound = true;
                                break;
                            case "ハイスコア":
                                scoreData.Score = Integer.parseInt(value);
                                break;
                            case "最大コンボ数":
                                scoreData.MaxCombo = Integer.parseInt(value);
                                break;
                            case "フルコンボ種別":
                                scoreData.FullComboType = parseFullComboType(value);
                                break;
                            case "プレー回数":
                                scoreData.PlayCount = Integer.parseInt(value);
                                break;
                            case "クリア回数":
                                scoreData.ClearCount = Integer.parseInt(value);
                                break;
                        }
                    } else {
                        switch (header) {
                            case "最高ダンスレベル":
                                scoreData.Rank = parseRank(value);
                                scoreInfoFound = true;
                                break;
                            case "ハイスコア":
                                scoreData.Score = Integer.parseInt(value);
                                break;
                            case "最大コンボ数":
                                scoreData.MaxCombo = Integer.parseInt(value);
                                break;
                            case "フルコンボ種別":
                                scoreData.FullComboType = parseFullComboType(value);
                                break;
                        }
                    }
                }
            }
        }

        if (!scoreInfoFound) {
            Toast.makeText(mParent, "Failed", Toast.LENGTH_LONG).show();
            return false;
        }

        MusicScore musicScore;
        if (mScoreList.containsKey(mItemId)) {
            musicScore = mScoreList.get(mItemId);
            mScoreList.remove(mItemId);
        } else {
            musicScore = new MusicScore();
        }

        ScoreData localScoreData = getLocalScoreData(musicScore);

        // スコアデータの更新ロジック
        if (!mGateSetting.OverWriteLife4) {
            if (scoreData.FullComboType == FullComboType.None && localScoreData.FullComboType == FullComboType.Life4) {
                scoreData.FullComboType = localScoreData.FullComboType;
            }
        }

        if (!mGateSetting.OverWriteLowerScores) {
            if (scoreData.Score < localScoreData.Score) {
                scoreData.Score = localScoreData.Score;
                scoreData.Rank = localScoreData.Rank;
            }
            if (scoreData.MaxCombo < localScoreData.MaxCombo) {
                scoreData.MaxCombo = localScoreData.MaxCombo;
            }
            scoreData.FullComboType = getBetterFullComboType(scoreData.FullComboType, localScoreData.FullComboType);
        }

        setScoreDataForPattern(musicScore, scoreData);

        mScoreList.put(mItemId, musicScore);
        FileReader.saveScoreData(mParent, mRivalId, mScoreList);

        String toastString = generateToastString(localScoreData, scoreData);
        mLogView2.setText(toastString);

        Toast.makeText(mParent, toastString, Toast.LENGTH_LONG).show();

        return true;
    }

    private MusicRank parseRank(String rankString) {
        switch (rankString) {
            case "AAA":
                return MusicRank.AAA;
            case "AA+":
                return MusicRank.AAp;
            case "AA":
                return MusicRank.AA;
            case "AA-":
                return MusicRank.AAm;
            case "A+":
                return MusicRank.Ap;
            case "A":
                return MusicRank.A;
            case "A-":
                return MusicRank.Am;
            case "B+":
                return MusicRank.Bp;
            case "B":
                return MusicRank.B;
            case "B-":
                return MusicRank.Bm;
            case "C+":
                return MusicRank.Cp;
            case "C":
                return MusicRank.C;
            case "C-":
                return MusicRank.Cm;
            case "D+":
                return MusicRank.Dp;
            case "D":
                return MusicRank.D;
            case "E":
                return MusicRank.E;
            default:
                return MusicRank.Noplay;
        }
    }

    private FullComboType parseFullComboType(String fcTypeString) {
        switch (fcTypeString) {
            case "グッドフルコンボ":
                return FullComboType.GoodFullCombo;
            case "グレートフルコンボ":
                return FullComboType.FullCombo;
            case "パーフェクトフルコンボ":
                return FullComboType.PerfectFullCombo;
            case "マーベラスフルコンボ":
                return FullComboType.MerverousFullCombo;
            case "Life4":
                return FullComboType.Life4;
            default:
                return FullComboType.None;
        }
    }

    private ScoreData getLocalScoreData(MusicScore musicScore) {
        switch (mPattern) {
            case bSP:
                return musicScore.bSP;
            case BSP:
                return musicScore.BSP;
            case DSP:
                return musicScore.DSP;
            case ESP:
                return musicScore.ESP;
            case CSP:
                return musicScore.CSP;
            case BDP:
                return musicScore.BDP;
            case DDP:
                return musicScore.DDP;
            case EDP:
                return musicScore.EDP;
            case CDP:
                return musicScore.CDP;
            default:
                return new ScoreData();
        }
    }

    private void setScoreDataForPattern(MusicScore musicScore, ScoreData scoreData) {
        switch (mPattern) {
            case bSP:
                musicScore.bSP = scoreData;
                break;
            case BSP:
                musicScore.BSP = scoreData;
                break;
            case DSP:
                musicScore.DSP = scoreData;
                break;
            case ESP:
                musicScore.ESP = scoreData;
                break;
            case CSP:
                musicScore.CSP = scoreData;
                break;
            case BDP:
                musicScore.BDP = scoreData;
                break;
            case DDP:
                musicScore.DDP = scoreData;
                break;
            case EDP:
                musicScore.EDP = scoreData;
                break;
            case CDP:
                musicScore.CDP = scoreData;
                break;
        }
    }

    private FullComboType getBetterFullComboType(FullComboType newType, FullComboType oldType) {
        FullComboType[] order = {FullComboType.None, FullComboType.GoodFullCombo, FullComboType.FullCombo,
                FullComboType.PerfectFullCombo, FullComboType.MerverousFullCombo};
        int newIndex = Arrays.asList(order).indexOf(newType);
        int oldIndex = Arrays.asList(order).indexOf(oldType);
        return newIndex > oldIndex ? newType : oldType;
    }

    private String generateToastString(ScoreData localScoreData, ScoreData scoreData) {
        DecimalFormat df = new DecimalFormat("0,000,000");
        UniquePattern c = mSouList.get(mCurrentPage);
        return (mRivalName == null ? "" : "Rival: " + mRivalName + "\n") +
                mPattern.toString() + " : " + Objects.requireNonNull(c.musics.get(mItemId)).Name + "\n" +
                "  Full Combo :  " + (localScoreData.FullComboType.equals(scoreData.FullComboType) ? "" : localScoreData.FullComboType + " -> ") + scoreData.FullComboType.toString() + "\n" +
                "  Rank :  " + (localScoreData.Rank.equals(scoreData.Rank) ? "" : (localScoreData.Rank + " -> ")) + scoreData.Rank.toString() + "\n" +
                "  Score :  " + (localScoreData.Score == scoreData.Score ? "" : (df.format(localScoreData.Score) + " -> ")) + df.format(scoreData.Score) + "\n" +
                "Max Combo :  " + (localScoreData.MaxCombo == scoreData.MaxCombo ? "" : (localScoreData.MaxCombo + " -> ")) + scoreData.MaxCombo + "\n" +
                "Play Count:  " + (localScoreData.PlayCount == scoreData.PlayCount ? scoreData.ClearCount + "/" + scoreData.PlayCount : localScoreData.ClearCount + "/" + localScoreData.PlayCount + " -> " + scoreData.ClearCount + "/" + scoreData.PlayCount);
    }

    private int mRetryCount = 0;

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(() -> {
            mWebProgress.setProgress(0);

            if (mCanceled) {
                return;
            }

            try {
                if (!_analyzeScore(src)) {
                    String toastString;
                    switch (TextUtil.checkLoggedIn(src)) {
                        // ログイン済みエラーなし
                        case 0:
                            toastString =
                                    "Complete !!\n\n" +
                                            (mRivalName == null ? "" : "Rival:  " + mRivalName + "\n") +
                                            "No Data...";
                            mLogView2.setText(toastString);
                            mRetryCount = 0;
                            break;
                        // ログインしていない
                        case 1:
                            Intent intent = new Intent();
                            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                            cancel();

                            mParent.startActivityForResult(intent, LoginRequestCode);
                            mRetryCount = 0;
                            (new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                mHandler.post(() -> mDialog.cancel());
                            }
                            )).start();
                            return;
                        // 不明
                        case -1:
                            if (mRetryCount < 3) {
                                toastString =
                                        "Retying...";
                                Toast.makeText(mParent, toastString, Toast.LENGTH_LONG).show();
                                ++mRetryCount;
                                --mCurrentPage;
                            } else {
                                Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
                                mDialog.cancel();
                                return;
                            }
                            break;
                    }
                } else {
                    mRetryCount = 0;
                }
            } catch (Exception e) {
                return;
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            ++mCurrentPage;
            mProgress.setProgress(mCurrentPage * 100);
            mCurrent.setText(String.valueOf(mCurrentPage));
            mPercent.setText((mPageCount == 0 ? 0 : (100 * mCurrentPage / mPageCount)) + "%");
            if (mCurrentPage >= mPageCount) {
                mLogView.setText("Finish.");
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
            UniquePattern c = mSouList.get(mCurrentPage);
            mItemId = c.MusicId;
            if (!mWebMusicIds.containsKey(mItemId) || Objects.requireNonNull(mWebMusicIds.get(mItemId)).idOnWebPage.isEmpty()) {
                new AlertDialog.Builder(mParent)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(mParent.getResources().getString(R.string.illegal_id_alert))
                        .setCancelable(true)
                        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {

                        }).show();
                mDialog.cancel();
                return;
            } else {
                mWebItemId = Objects.requireNonNull(mWebMusicIds.get(mItemId)).idOnWebPage;
            }
            mPattern = c.Pattern;
            mRequestUri = mUriH + mWebItemId + mUriM + getPatternInt(mPattern) + mUriF;
            mLogView.setText("Loading...\n" + (mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + mPattern.toString() + " : " + c.musics.get(mItemId).Name);
            mWebView.loadUrl(mRequestUri);
        });
    }

}

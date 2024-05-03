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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
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

        //mMusicList = FileReader.readMusicList(mParent);
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
        if (mGateSetting.FromA3) {
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
        //String uri = "file:///android_asset/status.html";
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
            mWebItemId = mWebMusicIds.get(mItemId).idOnWebPage;
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

    // TODO analyzeScoreをユニットテストでやれるようにUIまわりのコードを分割したい
    private boolean _analyzeScore(String src) {
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();

        return analyzeScore(src, uri);
    }

    private boolean analyzeScore(String src, String uri) {

        //boolean loggedin = false;
        if (!uri.equals(mRequestUri)) {
            if (mRivalId == null) {
                // TODO return false条件を抜き出してメソッド外に置く
                // 一応、途中まで進めないと行けないパターンがないか探す
                return false;
            } else {
                if (!uri.contains(mRequestUri.split("&name=")[0])) {
                    return false;
                }
            }
        }
        ScoreData scoreData = new ScoreData();
        String cmp = "0\"></td>  <td>";
        Log.d("", "1");
        if (src.contains(cmp)) {
            String dr = src.substring(src.indexOf(cmp) + cmp.length());
            cmp = "<br>";
            dr = TextUtil.escapeWebTitle(dr.substring(0, dr.indexOf(cmp)).trim());
            if (!dr.equals(mWebMusicIds.get(mItemId).titleOnWebPage)) {
                new AlertDialog.Builder(mParent)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(mParent.getResources().getString(R.string.name_different_alert) + "\n\n" + mWebMusicIds.get(mItemId).titleOnWebPage + "\n↓\n" + dr)
                        .setCancelable(true)
                        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        })
                        .show();
                return true;
            }
        } else {
            return false;
        }
        Log.d("", "2");
        cmp = "NO PLAY...";
        //if(src.contains(cmp) || loggedin)
        //{
            /*Toast.makeText(mParent, "Complete !!\n\n(NoPlay)", Toast.LENGTH_LONG).show();
            (new Thread(new Runnable() {
                public void run() {
            		try { Thread.sleep(1000);} catch (InterruptedException e) {}
                    mHandler.post(new Runnable() {
                    	public void run() {
                            mDialog.cancel();
                    	}
                    });
                    }
                }
            )).start();
            return true;*/
        //}
        //else
        if (!src.contains(cmp)) {
            Log.d("", "3");
            if (mCanceled) {
                return true;
            }
            Log.d("", "4");
            if (mGateSetting.FromA3) {
                cmp = "<th>ハイスコア時のランク</th><td>";
            } else {
                if (mRivalId == null) {
                    cmp = "<th>ハイスコア時のダンスレベル</th><td>";
                } else {
                    cmp = "<th>最高ダンスレベル</th><td>";
                }
            }
            if (src.contains(cmp)) {
                Log.d("", "5");
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                // TODO TextUtilに作ったメソッドに置き換える
                switch (dr) {
                    case "AAA":
                        scoreData.Rank = MusicRank.AAA;
                        break;
                    case "AA+":
                        scoreData.Rank = MusicRank.AAp;
                        break;
                    case "AA":
                        scoreData.Rank = MusicRank.AA;
                        break;
                    case "AA-":
                        scoreData.Rank = MusicRank.AAm;
                        break;
                    case "A+":
                        scoreData.Rank = MusicRank.Ap;
                        break;
                    case "A":
                        scoreData.Rank = MusicRank.A;
                        break;
                    case "A-":
                        scoreData.Rank = MusicRank.Am;
                        break;
                    case "B+":
                        scoreData.Rank = MusicRank.Bp;
                        break;
                    case "B":
                        scoreData.Rank = MusicRank.B;
                        break;
                    case "B-":
                        scoreData.Rank = MusicRank.Bm;
                        break;
                    case "C+":
                        scoreData.Rank = MusicRank.Cp;
                        break;
                    case "C":
                        scoreData.Rank = MusicRank.C;
                        break;
                    case "C-":
                        scoreData.Rank = MusicRank.Cm;
                        break;
                    case "D+":
                        scoreData.Rank = MusicRank.Dp;
                        break;
                    case "D":
                        scoreData.Rank = MusicRank.D;
                        break;
                    case "E":
                        scoreData.Rank = MusicRank.E;
                        break;
                    default:
                        scoreData.Rank = MusicRank.Noplay;
                        break;
                }
            } else {
                return false;
            }
            Log.d("", "6");
            cmp = "<th>ハイスコア</th><td>";
            if (src.contains(cmp)) {
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                scoreData.Score = Integer.parseInt(dr);
            } else {
                Toast.makeText(mParent, "Failed", Toast.LENGTH_LONG).show();
            }
            cmp = "<th>最大コンボ数</th><td>";
            if (src.contains(cmp)) {
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                scoreData.MaxCombo = Integer.parseInt(dr);
            } else {
                return false;
            }
            Log.d("", "7");
            if (mRivalId == null) {
                cmp = "<tr><th>フルコンボ種別</th><td>";
                if (src.contains(cmp)) {
                    Log.d("", "8");
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    // TODO 同じ処理がいろんなとこにあるはずなのでまとめる
                    switch (dr) {
                        case "グッドフルコンボ":
                            scoreData.FullComboType = FullComboType.GoodFullCombo;
                            break;
                        case "グレートフルコンボ":
                            scoreData.FullComboType = FullComboType.FullCombo;
                            break;
                        case "パーフェクトフルコンボ":
                            scoreData.FullComboType = FullComboType.PerfectFullCombo;
                            break;
                        case "マーベラスフルコンボ":
                            scoreData.FullComboType = FullComboType.MerverousFullCombo;
                            break;
                        default:
                            scoreData.FullComboType = FullComboType.None;
                            break;
                    }
                } else {
                    return false;
                }
                Log.d("", "9");
                cmp = "<th>プレー回数</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    scoreData.PlayCount = Integer.parseInt(dr);
                } else {
                    return false;
                }
                Log.d("", "10");
                cmp = "<th>クリア回数</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    scoreData.ClearCount = Integer.parseInt(dr);
                } else {
                    return false;
                }
            } else {
                cmp = "<th>フルコンボ種別</th><td>";
                if (src.contains(cmp)) {
                    Log.d("", "8");
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    switch (dr) {
                        case "グッドフルコンボ":
                            scoreData.FullComboType = FullComboType.GoodFullCombo;
                            break;
                        case "グレートフルコンボ":
                            scoreData.FullComboType = FullComboType.FullCombo;
                            break;
                        case "パーフェクトフルコンボ":
                            scoreData.FullComboType = FullComboType.PerfectFullCombo;
                            break;
                        case "マーベラスフルコンボ":
                            scoreData.FullComboType = FullComboType.MerverousFullCombo;
                            break;
                        default:
                            scoreData.FullComboType = FullComboType.None;
                            break;
                    }
                } else {
                    return false;
                }
            }
        }
        Log.d("", "11");
        MusicScore musicScore;
        if (mScoreList.containsKey(mItemId)) {
            musicScore = mScoreList.get(mItemId);
            mScoreList.remove(mItemId);
        } else {
            musicScore = new MusicScore();
        }
        ScoreData localScoreData;
        switch (mPattern) {
            case bSP:
                localScoreData = musicScore.bSP;
                break;
            case BSP:
                localScoreData = musicScore.BSP;
                break;
            case DSP:
                localScoreData = musicScore.DSP;
                break;
            case ESP:
                localScoreData = musicScore.ESP;
                break;
            case CSP:
                localScoreData = musicScore.CSP;
                break;
            case BDP:
                localScoreData = musicScore.BDP;
                break;
            case DDP:
                localScoreData = musicScore.DDP;
                break;
            case EDP:
                localScoreData = musicScore.EDP;
                break;
            case CDP:
                localScoreData = musicScore.CDP;
                break;
            default:
                localScoreData = new ScoreData();
                break;
        }

        // localScoreData(元msd) : 元の値
        // scoreData(元sd)  : 取得した値
        // 取得した値に元の値を上書きすることによって元の値を維持する

        // 「Life4 に未フルコンを上書きする」 が無効
        if (!mGateSetting.OverWriteLife4) {
            // 取得した値が未フルコン
            if (scoreData.FullComboType == FullComboType.None) {
                // 元の値が Life4
                if (localScoreData.FullComboType == FullComboType.Life4) {
                    // 元のフルコンタイプに戻す
                    scoreData.FullComboType = localScoreData.FullComboType;
                }
            }
        }

        // 「取得したFCでGFCを上書き」 が無効
        /*if(!mGateSetting.OverWriteFullCombo)
        {
        	// 取得した値がFC
        	if(sd.FullComboType == FullComboType.FullCombo)
        	{
	        	// 元の値がGFC
	        	if(msd.FullComboType == FullComboType.GoodFullCombo)
	        	{
	        		// 元のフルコンタイプに戻す
	        		sd.FullComboType = msd.FullComboType;
	        	}
        	}
        }*/

        // 「低いスコアを上書き」 が無効
        if (!mGateSetting.OverWriteLowerScores) {
            // スコアが低かったら
            if (scoreData.Score < localScoreData.Score) {
                // スコアを元に戻す
                scoreData.Score = localScoreData.Score;
                scoreData.Rank = localScoreData.Rank;
            }
            // コンボが低かったら
            if (scoreData.MaxCombo < localScoreData.MaxCombo) {
                // コンボを元に戻す
                scoreData.MaxCombo = localScoreData.MaxCombo;
            }
        	/*
        	// 元の値がAAA
        	if(msd.Rank == MusicRank.AAA)
        	{
        		// AAAにする
        		sd.Rank = msd.Rank;
        	}
        	// 元の値がAA
        	else if(msd.Rank == MusicRank.AA)
        	{
        		// 取得した値がAAAでない
        		if(sd.Rank != MusicRank.AAA)
        		{
        			// AAにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	// 元の値がA
        	else if(msd.Rank == MusicRank.A)
        	{
        		// 取得した値がAAAでもAAでもない
        		if(sd.Rank != MusicRank.AAA && sd.Rank != MusicRank.AA)
        		{
        			// Aにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	// 元の値がB
        	else if(msd.Rank == MusicRank.B)
        	{
        		// 取得した値がAAAでもAAでもAでもない
        		if(sd.Rank != MusicRank.AAA && sd.Rank != MusicRank.AA && sd.Rank != MusicRank.A)
        		{
        			// Bにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	// 元の値がC
        	else if(msd.Rank == MusicRank.C)
        	{
        		// 取得した値がNoPlayかEかD
        		if(sd.Rank == MusicRank.Noplay || sd.Rank == MusicRank.E || sd.Rank == MusicRank.D)
        		{
        			// Cにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	// 元の値がD
        	else if(msd.Rank == MusicRank.D)
        	{
        		// 取得した値がNoPlayかE
        		if(sd.Rank == MusicRank.Noplay || sd.Rank == MusicRank.E)
        		{
        			// Dにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	// 元の値がE
        	else if(msd.Rank == MusicRank.E)
        	{
        		// 取得した値がNoPlay
        		if(sd.Rank == MusicRank.Noplay)
        		{
        			// Eにする
        			sd.Rank = msd.Rank;
        		}
        	}
        	*/
            // 元の値がMFC
            if (localScoreData.FullComboType == FullComboType.MerverousFullCombo) {
                // MFCにする
                scoreData.FullComboType = localScoreData.FullComboType;
            }
            // 元の値がPFC
            else if (localScoreData.FullComboType == FullComboType.PerfectFullCombo) {
                // 取得した値がMFCでない
                if (scoreData.FullComboType != FullComboType.MerverousFullCombo) {
                    // PFCにする
                    scoreData.FullComboType = localScoreData.FullComboType;
                }
            }
            // 元の値がFC
            else if (localScoreData.FullComboType == FullComboType.FullCombo) {
                // 取得した値がMFCでもPFCでもない
                if (scoreData.FullComboType != FullComboType.MerverousFullCombo && scoreData.FullComboType != FullComboType.PerfectFullCombo) {
                    // FCにする
                    scoreData.FullComboType = localScoreData.FullComboType;
                }
            }
            // 元の値がGFC
            else if (localScoreData.FullComboType == FullComboType.GoodFullCombo) {
                // 取得した値がMFCでもPFCでもFCでもない
                if (scoreData.FullComboType != FullComboType.MerverousFullCombo && scoreData.FullComboType != FullComboType.PerfectFullCombo && scoreData.FullComboType != FullComboType.FullCombo) {
                    // GFCにする
                    scoreData.FullComboType = localScoreData.FullComboType;
                }
            }
            // 元の値がその他
            else {
                // 取得した値がMFCでもPFCでもFCでもGFCでもない
                if (scoreData.FullComboType != FullComboType.MerverousFullCombo && scoreData.FullComboType != FullComboType.PerfectFullCombo && scoreData.FullComboType != FullComboType.FullCombo && scoreData.FullComboType != FullComboType.GoodFullCombo) {
                    // 元の値にもどす
                    scoreData.FullComboType = localScoreData.FullComboType;
                }
            }
        }
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
        mScoreList.put(mItemId, musicScore);
        FileReader.saveScoreData(mParent, mRivalId, mScoreList);
        DecimalFormat df = new DecimalFormat("0,000,000");
        UniquePattern c = mSouList.get(mCurrentPage);
        String toastString =
                (mRivalName == null ? "" : "Rival: " + mRivalName + "\n") +
                        mPattern.toString() + " : " + c.musics.get(mItemId).Name + "\n" +
                        "  Full Combo :  " + (localScoreData.FullComboType.equals(scoreData.FullComboType) ? "" : localScoreData.FullComboType + " -> ") + scoreData.FullComboType.toString() + "\n" +
                        "  Rank :  " + (localScoreData.Rank.equals(scoreData.Rank) ? "" : (localScoreData.Rank + " -> ")) + scoreData.Rank.toString() + "\n" +
                        "  Score :  " + (localScoreData.Score == scoreData.Score ? "" : (df.format(localScoreData.Score) + " -> ")) + df.format(scoreData.Score) + "\n" +
                        "Max Combo :  " + (localScoreData.MaxCombo == scoreData.MaxCombo ? "" : (localScoreData.MaxCombo + " -> ")) + scoreData.MaxCombo + "\n" +
                        "Play Count:  " + (localScoreData.PlayCount == scoreData.PlayCount ? scoreData.ClearCount + "/" + scoreData.PlayCount : localScoreData.ClearCount + "/" + localScoreData.PlayCount + " -> " + scoreData.ClearCount + "/" + scoreData.PlayCount);
        mLogView2.setText(toastString);
        return true;
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
                mWebItemId = mWebMusicIds.get(mItemId).idOnWebPage;
            }
            mPattern = c.Pattern;
            mRequestUri = mUriH + mWebItemId + mUriM + getPatternInt(mPattern) + mUriF;
            mLogView.setText("Loading...\n" + (mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + mPattern.toString() + " : " + c.musics.get(mItemId).Name);
            mWebView.loadUrl(mRequestUri);
        });
    }

}

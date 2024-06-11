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
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;

public class DialogFromGate {

    public static int LoginRequestCode = 20002;

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
    private String mRivalId;
    private String mRivalName;
    private int mItemId;
    private String mWebItemId;
    private PatternType mPattern;
    private String mRequestUri;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGate(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mWebMusicIds = FileReader.readWebMusicIds(mParent);

        mWebProgress = mView.findViewById(R.id.webProgress);
        TextView mLogView = mView.findViewById(R.id.log);

        mLogView.setText(mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPage));

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

    public boolean setArguments(AlertDialog dialog, int itemId, PatternType pattern, String rivalId, String rivalName) {
        boolean ret = true;
        mDialog = dialog;
        mItemId = itemId;
        if (!mWebMusicIds.containsKey(itemId)) {
            ret = false;
        } else if (mWebMusicIds.get(itemId).idOnWebPage.equals("")) {
            mWebItemId = "";
            ret = false;
        } else {
            mWebItemId = mWebMusicIds.get(itemId).idOnWebPage;
        }
        mPattern = pattern;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mScoreList = FileReader.readScoreList(mParent, mRivalId);
        mGateSetting = FileReader.readGateSetting(mParent);
        return ret;
    }

    public View getView() {
        return mView;
    }

    public void start() {
        if (mDialog == null) {
            return;
        }
        FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);

        int patternInt =
                mPattern == PatternType.bSP ? 0 :
                        mPattern == PatternType.BSP ? 1 :
                                mPattern == PatternType.DSP ? 2 :
                                        mPattern == PatternType.ESP ? 3 :
                                                mPattern == PatternType.CSP ? 4 :
                                                        mPattern == PatternType.BDP ? 5 :
                                                                mPattern == PatternType.DDP ? 6 :
                                                                        mPattern == PatternType.EDP ? 7 :
                                                                                mPattern == PatternType.CDP ? 8 :
                                                                                        0;

        if (mGateSetting.FromNewSite) {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        } else {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
        }

        if (mRivalId == null) {
            mRequestUri += "playdata/music_detail.html?index=" + mWebItemId + "&diff=" + patternInt;
        } else {
            mRequestUri += "rival/music_detail.html?index=" + mWebItemId + "&diff=" + patternInt + "&rival_id=" + mRivalId;
        }
        //String uri = "file:///android_asset/status.html";
        mWebView.loadUrl(mRequestUri);
    }

    public void cancel() {
        mCanceled = true;
        WebView web = mView.findViewById(R.id.webView);
        web.stopLoading();
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    private boolean analyzeScore(String src) {
        Log.d("SRC", src);
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();
        //boolean loggedin = false;
        Log.d("POINT", "0.0");
        Log.d(uri, mRequestUri);
        if (!uri.equals(mRequestUri)) {
            Log.d("POINT", "0.1");
            if (mRivalId == null) {
                Log.d("POINT", "0.2");
                return false;
            } else {
                Log.d("POINT", "0.3");
                Toast.makeText(mParent, mRivalId, Toast.LENGTH_LONG).show();
                if (!uri.contains(mRequestUri.split("&name=")[0])) {
                    Log.d("POINT", "0.4");
                    return false;
                }
            }
        }
        ScoreData sd = new ScoreData();
        String cmp = "0\"></td>  <td>";
        Log.d("POINT", "1");
        if (src.contains(cmp)) {
            String dr = src.substring(src.indexOf(cmp) + cmp.length());
            cmp = "<br>";
            dr = TextUtil.escapeWebTitle(dr.substring(0, dr.indexOf(cmp)).trim());
            if (!dr.equals(mWebMusicIds.get(mItemId).titleOnWebPage)) {
                new AlertDialog.Builder(mParent)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(mParent.getResources().getString(R.string.name_different_alert) + "\n\n\"" + mWebMusicIds.get(mItemId).titleOnWebPage + "\"\n↓\n\"" + dr + "\"")
                        .setCancelable(true)
                        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), (dialog, whichButton) -> {
                        }).show();
                return true;
            }
        } else {
            return false;
        }
        Log.d("POINT", "2");
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
        Log.d("POINT", "3");
        if (!src.contains(cmp)) {
            if (mCanceled) {
                return true;
            }
            Log.d("POINT", "4");
            if (mGateSetting.FromNewSite) {
                cmp = "<th>ハイスコア時のランク</th><td>";
            } else {
                if (mRivalId == null) {
                    cmp = "<th>ハイスコア時のダンスレベル</th><td>";
                } else {
                    cmp = "<th>最高ダンスレベル</th><td>";
                }
            }
            if (src.contains(cmp)) {
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                switch (dr) {
                    case "AAA":
                        sd.Rank = MusicRank.AAA;
                        break;
                    case "AA+":
                        sd.Rank = MusicRank.AAp;
                        break;
                    case "AA":
                        sd.Rank = MusicRank.AA;
                        break;
                    case "AA-":
                        sd.Rank = MusicRank.AAm;
                        break;
                    case "A+":
                        sd.Rank = MusicRank.Ap;
                        break;
                    case "A":
                        sd.Rank = MusicRank.A;
                        break;
                    case "A-":
                        sd.Rank = MusicRank.Am;
                        break;
                    case "B+":
                        sd.Rank = MusicRank.Bp;
                        break;
                    case "B":
                        sd.Rank = MusicRank.B;
                        break;
                    case "B-":
                        sd.Rank = MusicRank.Bm;
                        break;
                    case "C+":
                        sd.Rank = MusicRank.Cp;
                        break;
                    case "C":
                        sd.Rank = MusicRank.C;
                        break;
                    case "C-":
                        sd.Rank = MusicRank.Cm;
                        break;
                    case "D+":
                        sd.Rank = MusicRank.Dp;
                        break;
                    case "D":
                        sd.Rank = MusicRank.D;
                        break;
                    case "E":
                        sd.Rank = MusicRank.E;
                        break;
                    default:
                        sd.Rank = MusicRank.Noplay;
                        break;
                }
            } else {
                return false;
            }
            Log.d("POINT", "5");
            cmp = "<th>ハイスコア</th><td>";
            if (src.contains(cmp)) {
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                sd.Score = Integer.parseInt(dr);
            } else {
                Toast.makeText(mParent, "Failed", Toast.LENGTH_LONG).show();
            }
            Log.d("POINT", "6");
            cmp = "<th>最大コンボ数</th><td>";
            if (src.contains(cmp)) {
                String dr = src.substring(src.indexOf(cmp) + cmp.length());
                cmp = "</td>";
                dr = dr.substring(0, dr.indexOf(cmp));
                sd.MaxCombo = Integer.parseInt(dr);
            } else {
                return false;
            }
            Log.d("POINT", "7");
            if (mRivalId == null) {
                cmp = "<th>フルコンボ種別</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    switch (dr) {
                        case "グッドフルコンボ":
                            sd.FullComboType = FullComboType.GoodFullCombo;
                            break;
                        case "グレートフルコンボ":
                            sd.FullComboType = FullComboType.FullCombo;
                            break;
                        case "パーフェクトフルコンボ":
                            sd.FullComboType = FullComboType.PerfectFullCombo;
                            break;
                        case "マーベラスフルコンボ":
                            sd.FullComboType = FullComboType.MerverousFullCombo;
                            break;
                        default:
                            sd.FullComboType = FullComboType.None;
                            break;
                    }
                } else {
                    return false;
                }
                Log.d("POINT", "8");
                cmp = "<th>プレー回数</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    sd.PlayCount = Integer.parseInt(dr);
                } else {
                    return false;
                }
                Log.d("POINT", "9");
                cmp = "<th>クリア回数</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    sd.ClearCount = Integer.parseInt(dr);
                } else {
                    return false;
                }
            } else {
                cmp = "<th>フルコンボ種別</th><td>";
                if (src.contains(cmp)) {
                    String dr = src.substring(src.indexOf(cmp) + cmp.length());
                    cmp = "</td>";
                    dr = dr.substring(0, dr.indexOf(cmp));
                    switch (dr) {
                        case "グッドフルコンボ":
                            sd.FullComboType = FullComboType.GoodFullCombo;
                            break;
                        case "グレートフルコンボ":
                            sd.FullComboType = FullComboType.FullCombo;
                            break;
                        case "パーフェクトフルコンボ":
                            sd.FullComboType = FullComboType.PerfectFullCombo;
                            break;
                        case "マーベラスフルコンボ":
                            sd.FullComboType = FullComboType.MerverousFullCombo;
                            break;
                        default:
                            sd.FullComboType = FullComboType.None;
                            break;
                    }
                } else {
                    return false;
                }
            }
        }
        Log.d("POINT", "10");
        MusicScore ms;
        if (mScoreList.containsKey(mItemId)) {
            ms = mScoreList.get(mItemId);
            mScoreList.remove(mItemId);
        } else {
            ms = new MusicScore();
        }
        ScoreData msd;
        switch (mPattern) {
            case bSP:
                msd = ms.bSP;
                break;
            case BSP:
                msd = ms.BSP;
                break;
            case DSP:
                msd = ms.DSP;
                break;
            case ESP:
                msd = ms.ESP;
                break;
            case CSP:
                msd = ms.CSP;
                break;
            case BDP:
                msd = ms.BDP;
                break;
            case DDP:
                msd = ms.DDP;
                break;
            case EDP:
                msd = ms.EDP;
                break;
            case CDP:
                msd = ms.CDP;
                break;
            default:
                msd = new ScoreData();
                break;
        }

        // msd : 元の値
        // sd  : 取得した値
        // 取得した値に元の値を上書きすることによって元の値を維持する

        // 「Life4 に未フルコンを上書きする」 が無効
        if (!mGateSetting.OverWriteLife4) {
            // 取得した値が未フルコン
            if (sd.FullComboType == FullComboType.None) {
                // 元の値が Life4
                if (msd.FullComboType == FullComboType.Life4) {
                    // 元のフルコンタイプに戻す
                    sd.FullComboType = msd.FullComboType;
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
            if (sd.Score < msd.Score) {
                // スコアを元に戻す
                sd.Score = msd.Score;
                sd.Rank = msd.Rank;
            }
            // コンボが低かったら
            if (sd.MaxCombo < msd.MaxCombo) {
                // コンボを元に戻す
                sd.MaxCombo = msd.MaxCombo;
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
            if (msd.FullComboType == FullComboType.MerverousFullCombo) {
                // MFCにする
                sd.FullComboType = msd.FullComboType;
            }
            // 元の値がPFC
            else if (msd.FullComboType == FullComboType.PerfectFullCombo) {
                // 取得した値がMFCでない
                if (sd.FullComboType != FullComboType.MerverousFullCombo) {
                    // PFCにする
                    sd.FullComboType = msd.FullComboType;
                }
            }
            // 元の値がFC
            else if (msd.FullComboType == FullComboType.FullCombo) {
                // 取得した値がMFCでもPFCでもない
                if (sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo) {
                    // FCにする
                    sd.FullComboType = msd.FullComboType;
                }
            }
            // 元の値がGFC
            else if (msd.FullComboType == FullComboType.GoodFullCombo) {
                // 取得した値がMFCでもPFCでもFCでもない
                if (sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo && sd.FullComboType != FullComboType.FullCombo) {
                    // GFCにする
                    sd.FullComboType = msd.FullComboType;
                }
            }
            // 元の値がその他
            else {
                // 取得した値がMFCでもPFCでもFCでもGFCでもない
                if (sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo && sd.FullComboType != FullComboType.FullCombo && sd.FullComboType != FullComboType.GoodFullCombo) {
                    // 元の値にもどす
                    sd.FullComboType = msd.FullComboType;
                }
            }
        }
        switch (mPattern) {
            case bSP:
                ms.bSP = sd;
                break;
            case BSP:
                ms.BSP = sd;
                break;
            case DSP:
                ms.DSP = sd;
                break;
            case ESP:
                ms.ESP = sd;
                break;
            case CSP:
                ms.CSP = sd;
                break;
            case BDP:
                ms.BDP = sd;
                break;
            case DDP:
                ms.DDP = sd;
                break;
            case EDP:
                ms.EDP = sd;
                break;
            case CDP:
                ms.CDP = sd;
                break;
        }
        mScoreList.put(mItemId, ms);
        FileReader.saveScoreData(mParent, mRivalId, mScoreList);
        DecimalFormat df = new DecimalFormat("0,000,000");
        String toastString =
                "Complete !!\n\n" +
                        (mRivalName == null ? "" : "Rival:  " + mRivalName + "\n") +
                        "Full Combo :  " + (msd.FullComboType.equals(sd.FullComboType) ? "" : msd.FullComboType + " -> ") + sd.FullComboType.toString() + "\n" +
                        "Rank :  " + (msd.Rank.equals(sd.Rank) ? "" : (msd.Rank + " -> ")) + sd.Rank.toString() + "\n" +
                        "Score :  " + (msd.Score == sd.Score ? "" : (df.format(msd.Score) + " -> ")) + df.format(sd.Score) + "\n" +
                        "Max Combo :  " + (msd.MaxCombo == sd.MaxCombo ? "" : (msd.MaxCombo + " -> ")) + sd.MaxCombo + "\n" +
                        (mRivalName == null ? "Play Count:  " + (msd.PlayCount == sd.PlayCount ? sd.ClearCount + "/" + sd.PlayCount : msd.ClearCount + "/" + msd.PlayCount + " -> " + sd.ClearCount + "/" + sd.PlayCount) : "");
        Toast.makeText(mParent, toastString, Toast.LENGTH_LONG).show();
        return true;
    }

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(() -> {
            mWebProgress.setProgress(0);

            if (mCanceled) {
                return;
            }
            try {
                if (!analyzeScore(src)) {
                    String toastString;
                    switch (TextUtil.checkLoggedIn(src)) {
                        // ログイン済みエラーなし
                        case 0:
                            toastString =
                                    "Complete !!\n\n" +
                                            (mRivalName == null ? "" : "Rival:  " + mRivalName + "\n") +
                                            "No Data...";
                            Toast.makeText(mParent, toastString, Toast.LENGTH_LONG).show();
                            break;
                        // ログインしていない
                        case 1:
                            Intent intent = new Intent();
                            intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                            cancel();

                            mParent.startActivityForResult(intent, LoginRequestCode);
                            break;
                        // 不明
                        case -1:
                            Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            } catch (Exception ignored) {

            }

            (new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                mHandler.post(() -> mDialog.cancel());
            }
            )).start();

        });
    }

}

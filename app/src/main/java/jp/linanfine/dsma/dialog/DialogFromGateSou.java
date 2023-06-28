package jp.linanfine.dsma.dialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TreeMap;

import jp.linanfine.dsma.activity.ScoreList;
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
import jp.linanfine.dsma.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DialogFromGateSou {
	
	public static int LoginRequestCode = 20003;
	
	private Handler mHandler = new Handler();
	private Activity mParent;
	private AlertDialog mDialog;
	private View mView;
	
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
        if(intent == null)
        {
        	return;
        }
        
        //mMusicList = FileReader.readMusicList(mParent);
		mGateSetting = FileReader.readGateSetting(mParent);
        
        mWebMusicIds = FileReader.readWebMusicIds(mParent);

        mWebProgress = (ProgressBar)mView.findViewById(R.id.webProgress);
		mLogView = (TextView)mView.findViewById(R.id.log);
		mLogView2 = (TextView)mView.findViewById(R.id.log2);
		mPercent = (TextView)mView.findViewById(R.id.percent);
		mCurrent = (TextView)mView.findViewById(R.id.current);
		mMax = (TextView)mView.findViewById(R.id.max);
		mProgress = (ProgressBar)mView.findViewById(R.id.mainProgress);
		
		mLogView2.setVisibility(View.VISIBLE);
		mView.findViewById(R.id.log3).setVisibility(View.VISIBLE);
        
        WebViewClient client = new WebViewClient() 
        {
            @Override
            public void onPageFinished(WebView view, String url) {
    			view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            	mWebProgress.setProgress(1);
            }
    	};
    	
    	WebChromeClient chrome = new WebChromeClient() 
    	{
    		public void onProgressChanged(WebView view, int progress) {
    			//try { Thread.sleep(10);} catch (InterruptedException e) {}
    			mWebProgress.setProgress(1+progress);
    			mProgress.setProgress(mCurrentPage*100+(mPageCount==0?0:progress));
    			mPercent.setText(String.valueOf((100*mCurrentPage+(mPageCount==0?0:progress))/(mPageCount==0?10000:mPageCount))+"%");
    		}
    	};

    	mWebView = (WebView) mView.findViewById(R.id.webView);
        //mWebView.getSettings().setBlockNetworkImage(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.setWebViewClient(client);
    	mWebView.setWebChromeClient(chrome);
        //mWebView.getSettings().setJavaScriptEnabled(true);
    	mWebView.getSettings().setJavaScriptEnabled(true);
    	mWebView.addJavascriptInterface(this, "viewsourceactivity");

	}
	
	public boolean setArguments(AlertDialog dialog, ArrayList<UniquePattern> souList, String rivalId, String rivalName)
	{
		mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mSouList = souList;
        mScoreList = FileReader.readScoreList(mParent, mRivalId);
        return true;
    }
	
	public View getView()
	{
		return mView;
	}
	
	private static int getPatternInt(PatternType pattern)
	{

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
	
	public void start()
	{
		if(mDialog == null)
		{
			return;
		}
		FileReader.requestAd((LinearLayout)mView.findViewById(R.id.adContainer), mParent);
		mPageCount = mSouList.size();
		if(mPageCount <= 0)
		{
			return;
		}
    	mProgress.setMax(mPageCount*100);
    	mMax.setText(String.valueOf(mPageCount));

		mGateSetting = FileReader.readGateSetting(mParent);
		if(mGateSetting.FromA3)
		{
			mUriH = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
		}
		else{
			mUriH = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
		}

        if(mRivalId == null)
        {
        	mUriH += "playdata/music_detail.html?index=";
        	mUriM = "&diff=";
        	mUriF = "";
        }
        else
        {
        	mUriH += "rival/music_detail.html?index=";
        	mUriM = "&diff=";
        	mUriF = "&rival_id="+mRivalId+"&name="+mRivalName.trim();
        }
        //String uri = "file:///android_asset/status.html";
        mCurrentPage = 0;
        UniquePattern c = mSouList.get(mCurrentPage);
        mItemId = c.MusicId;
        if(!mWebMusicIds.containsKey(mItemId) || mWebMusicIds.get(mItemId).idOnWebPage == "")
        {
    		new AlertDialog.Builder(mParent)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setMessage(mParent.getResources().getString(R.string.illegal_id_alert))
	        .setCancelable(true)
	        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	
	            }
	        }).show();
    		mDialog.cancel();
        	return;
        }
        else
        {
        	mWebItemId = mWebMusicIds.get(mItemId).idOnWebPage;
        }
        mPattern = c.Pattern;
        mRequestUri = mUriH+String.valueOf(mWebItemId)+mUriM+String.valueOf(getPatternInt(mPattern))+mUriF;
        mLogView.setText("Loading...\n"+(mRivalName==null?"My Score\n":("Rival: "+mRivalName+"\n")) + mPattern.toString() + " : " + c.musics.get(mItemId).Name);
        mWebView.loadUrl(mRequestUri);
	}
	
	public void cancel()
	{
		mCanceled = true;
        WebView web = (WebView) mView.findViewById(R.id.webView);
        web.stopLoading();
        mPageCount = 1;
        if(mDialog != null)
        {
        	mDialog.cancel();
        }
	}

	private boolean analyzeScore(String src)
	{

        WebView web = (WebView) mView.findViewById(R.id.webView);
    	String uri = web.getUrl();
    	//boolean loggedin = false;
    	if(!uri.equals(mRequestUri))
    	{
        	if(mRivalId == null)
            {
        		return false;
            }
            else
            {
            	if (!uri.contains(mRequestUri.split("&name=")[0]))
            	{
        			return false;
            	}
            }
    	}
    	src = src;
    	ScoreData sd = new ScoreData();
		String cmp = "0\"></td>  <td>";
		Log.d("", "1");
        if(src.contains(cmp))
        {
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "<br>";
        	dr = TextUtil.excapeWebTitle(dr.substring(0, dr.indexOf(cmp)).trim());
        	if(!dr.equals(mWebMusicIds.get(mItemId).titleOnWebPage))
        	{
        		new AlertDialog.Builder(mParent)
    	        .setIcon(android.R.drawable.ic_dialog_info)
    	        .setMessage(mParent.getResources().getString(R.string.name_different_alert)+"\n\n"+mWebMusicIds.get(mItemId).titleOnWebPage+"\n↓\n"+dr)
    	        .setCancelable(true)
    	        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int whichButton) {
    	            	
    	            }
    	        }).show();
        		return true;
        	}
        }
        else
        {
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
        if(!src.contains(cmp))
    {
    		Log.d("", "3");
        if(mCanceled)
        {
        	return true;
        }
		Log.d("", "4");
		if(mGateSetting.FromA3)
		{
			cmp = "<th>ハイスコア時のランク</th><td>";
		}
		else
		{
			if (mRivalId == null)
			{
				cmp = "<th>ハイスコア時のダンスレベル</th><td>";
			} else
			{
				cmp = "<th>最高ダンスレベル</th><td>";
			}
		}
        if(src.contains(cmp))
        {
    		Log.d("", "5");
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	if(dr.equals("AAA"))
        	{
        		sd.Rank = MusicRank.AAA;
        	}
        	else if(dr.equals("AA+"))
        	{
        		sd.Rank = MusicRank.AAp;
        	}
        	else if(dr.equals("AA"))
        	{
        		sd.Rank = MusicRank.AA;
        	}
        	else if(dr.equals("AA-"))
        	{
        		sd.Rank = MusicRank.AAm;
        	}
        	else if(dr.equals("A+"))
        	{
        		sd.Rank = MusicRank.Ap;
        	}
        	else if(dr.equals("A"))
        	{
        		sd.Rank = MusicRank.A;
        	}
        	else if(dr.equals("A-"))
        	{
        		sd.Rank = MusicRank.Am;
        	}
        	else if(dr.equals("B+"))
        	{
        		sd.Rank = MusicRank.Bp;
        	}
        	else if(dr.equals("B"))
        	{
        		sd.Rank = MusicRank.B;
        	}
        	else if(dr.equals("B-"))
        	{
        		sd.Rank = MusicRank.Bm;
        	}
        	else if(dr.equals("C+"))
        	{
        		sd.Rank = MusicRank.Cp;
        	}
        	else if(dr.equals("C"))
        	{
        		sd.Rank = MusicRank.C;
        	}
        	else if(dr.equals("C-"))
        	{
        		sd.Rank = MusicRank.Cm;
        	}
        	else if(dr.equals("D+"))
        	{
        		sd.Rank = MusicRank.Dp;
        	}
        	else if(dr.equals("D"))
        	{
        		sd.Rank = MusicRank.D;
        	}
        	else if(dr.equals("E"))
        	{
        		sd.Rank = MusicRank.E;
        	}
        	else
        	{
        		sd.Rank = MusicRank.Noplay;
        	}
        }
        else
        {
        	return false;
        }
		Log.d("", "6");
    	cmp = "<th>ハイスコア</th><td>";
        if(src.contains(cmp))
        {
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	sd.Score = Integer.valueOf(dr);
        }
        else
        {
        	Toast.makeText(mParent, "Failed", Toast.LENGTH_LONG).show();
        }
    	cmp = "<th>最大コンボ数</th><td>";
        if(src.contains(cmp))
        {
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	sd.MaxCombo = Integer.valueOf(dr);
        }
        else
        {
        	return false;
        }
		Log.d("", "7");
    	if(mRivalId == null)
        {
    	cmp = "<tr><th>フルコンボ種別</th><td>";
        if(src.contains(cmp))
        {
    		Log.d("", "8");
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	if(dr.equals("グッドフルコンボ"))
        	{
        		sd.FullComboType = FullComboType.GoodFullCombo;
        	}
        	else if(dr.equals("グレートフルコンボ"))
        	{
        		sd.FullComboType = FullComboType.FullCombo;
        	}
        	else if(dr.equals("パーフェクトフルコンボ"))
        	{
        		sd.FullComboType = FullComboType.PerfectFullCombo;
        	}
        	else if(dr.equals("マーベラスフルコンボ"))
        	{
        		sd.FullComboType = FullComboType.MerverousFullCombo;
        	}
        	else
        	{
        		sd.FullComboType = FullComboType.None;
        	}
        }
        else
        {
        	return false;
        }
		Log.d("", "9");
    	cmp = "<th>プレー回数</th><td>";
        if(src.contains(cmp))
        {
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	sd.PlayCount = Integer.valueOf(dr);
        }
        else
        {
        	return false;
        }
		Log.d("", "10");
    	cmp = "<th>クリア回数</th><td>";
        if(src.contains(cmp))
        {
        	String dr = src.substring(src.indexOf(cmp)+cmp.length());
        	cmp = "</td>";
        	dr = dr.substring(0, dr.indexOf(cmp));
        	sd.ClearCount = Integer.valueOf(dr);
        }
        else
        {
        	return false;
        }
        }
    	else
    	{
        	cmp = "<th>フルコンボ種別</th><td>";
            if(src.contains(cmp))
            {
        		Log.d("", "8");
            	String dr = src.substring(src.indexOf(cmp)+cmp.length());
            	cmp = "</td>";
            	dr = dr.substring(0, dr.indexOf(cmp));
            	if(dr.equals("グッドフルコンボ"))
            	{
            		sd.FullComboType = FullComboType.GoodFullCombo;
            	}
            	else if(dr.equals("グレートフルコンボ"))
            	{
            		sd.FullComboType = FullComboType.FullCombo;
            	}
            	else if(dr.equals("パーフェクトフルコンボ"))
            	{
            		sd.FullComboType = FullComboType.PerfectFullCombo;
            	}
            	else if(dr.equals("マーベラスフルコンボ"))
            	{
            		sd.FullComboType = FullComboType.MerverousFullCombo;
            	}
            	else
            	{
            		sd.FullComboType = FullComboType.None;
            	}
            }
            else
            {
            	return false;
            }
    	}
	}
		Log.d("", "11");
        MusicScore ms;
        if(mScoreList.containsKey(mItemId))
        {
        	ms = mScoreList.get(mItemId);
        	mScoreList.remove(mItemId);
        }
        else
        {
        	ms = new MusicScore();
        }
        ScoreData msd;
        switch(mPattern)
        {
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
        if(!mGateSetting.OverWriteLife4)
        {
        	// 取得した値が未フルコン
        	if(sd.FullComboType == FullComboType.None)
        	{
            	// 元の値が Life4
            	if(msd.FullComboType == FullComboType.Life4)
            	{
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
        if(!mGateSetting.OverWriteLowerScores)
        {
        	// スコアが低かったら
        	if(sd.Score < msd.Score)
        	{
        		// スコアを元に戻す
        		sd.Score = msd.Score;
        		sd.Rank = msd.Rank;
        	}
        	// コンボが低かったら
        	if(sd.MaxCombo < msd.MaxCombo)
        	{
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
        	if(msd.FullComboType == FullComboType.MerverousFullCombo)
        	{
        		// MFCにする
        		sd.FullComboType = msd.FullComboType;
        	}
        	// 元の値がPFC
        	else if(msd.FullComboType == FullComboType.PerfectFullCombo)
        	{
        		// 取得した値がMFCでない
        		if(sd.FullComboType != FullComboType.MerverousFullCombo)
        		{
        			// PFCにする
        			sd.FullComboType = msd.FullComboType;
        		}
        	}
        	// 元の値がFC
        	else if(msd.FullComboType == FullComboType.FullCombo)
        	{
        		// 取得した値がMFCでもPFCでもない
        		if(sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo)
        		{
        			// FCにする
        			sd.FullComboType = msd.FullComboType;
        		}
        	}
        	// 元の値がGFC
        	else if(msd.FullComboType == FullComboType.GoodFullCombo)
        	{
        		// 取得した値がMFCでもPFCでもFCでもない
        		if(sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo && sd.FullComboType != FullComboType.FullCombo)
        		{
        			// GFCにする
        			sd.FullComboType = msd.FullComboType;
        		}
        	}
        	// 元の値がその他
        	else
        	{
        		// 取得した値がMFCでもPFCでもFCでもGFCでもない
        		if(sd.FullComboType != FullComboType.MerverousFullCombo && sd.FullComboType != FullComboType.PerfectFullCombo && sd.FullComboType != FullComboType.FullCombo && sd.FullComboType != FullComboType.GoodFullCombo)
        		{
        			// 元の値にもどす
        			sd.FullComboType = msd.FullComboType;
        		}
        	}
        }
        switch(mPattern)
        {
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
        UniquePattern c = mSouList.get(mCurrentPage);
        String toastString = 
        		(mRivalName==null?"":"Rival: "+mRivalName+"\n") + 
        		mPattern.toString() + " : " + c.musics.get(mItemId).Name+"\n"+
        		"  Full Combo :  "+(msd.FullComboType.equals(sd.FullComboType)?"":msd.FullComboType.toString()+" -> ")+sd.FullComboType.toString()+"\n"+
        		"  Rank :  "+(msd.Rank.equals(sd.Rank)?"":(msd.Rank.toString()+" -> "))+sd.Rank.toString()+"\n"+
        		"  Score :  "+(msd.Score==sd.Score?"":(df.format(msd.Score)+" -> "))+df.format(sd.Score)+"\n"+
        		"Max Combo :  "+(msd.MaxCombo==sd.MaxCombo?"":(String.valueOf(msd.MaxCombo)+" -> "))+String.valueOf(sd.MaxCombo)+"\n"+
        		"Play Count:  "+(msd.PlayCount==sd.PlayCount?String.valueOf(sd.ClearCount)+"/"+String.valueOf(sd.PlayCount):String.valueOf(msd.ClearCount)+"/"+String.valueOf(msd.PlayCount)+" -> "+String.valueOf(sd.ClearCount)+"/"+String.valueOf(sd.PlayCount));
        mLogView2.setText(toastString);
        return true;
	}

	private int mRetryCount = 0;
	@android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(new Runnable() {
            public void run() {
            	mWebProgress.setProgress(0);
            	
            	if(mCanceled)
            	{
            		return;
            	}
            	
            	try
            	{
                	if(!analyzeScore(src))
                	{
                		String toastString;
                		switch(TextUtil.checkLoggedIn(src))
                		{
                			// ログイン済みエラーなし
	                		case 0:
	                            toastString = 
                        		"Complete !!\n\n"+
                        		(mRivalName==null?"":"Rival:  "+mRivalName+"\n")+
                        		"No Data...";
	                            mLogView2.setText(toastString);
                            	mRetryCount = 0;
	                			break;
	                		// ログインしていない
	                		case 1:
	                	        Intent intent=new Intent();
	                	        intent.setClassName("jp.linanfine.dsma","jp.linanfine.dsma.activity.GateLogin");
	                	        
	                	        cancel();
	                	 
	                	        mParent.startActivityForResult(intent, LoginRequestCode);
	                	        mRetryCount = 0;
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
	                    		return;
	                		// 不明
	                		case -1:
	                    		if(mRetryCount < 3)
	                    		{
	                                toastString = 
	                                		"Retying...";
	                                Toast.makeText(mParent, toastString, Toast.LENGTH_LONG).show();
	                                ++mRetryCount;
	                                --mCurrentPage;
	                    		}
	                    		else
	                    		{
	                    			Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
	                    		    mDialog.cancel();
	                    		    return;
	                    		}
	                			break;
                		}
                	}
                	else
                	{
                		mRetryCount = 0;
                	}
            	}
            	catch(Exception e)
            	{
            		return;
            	}
            	
            	try { Thread.sleep(3000);} catch (InterruptedException e) {}
            	++mCurrentPage;
            	mProgress.setProgress(mCurrentPage*100);
            	mCurrent.setText(String.valueOf(mCurrentPage));
            	mPercent.setText(String.valueOf(mPageCount==0?0:(100*mCurrentPage/mPageCount))+"%");
            	if(mCurrentPage >= mPageCount)
            	{
                	mLogView.setText("Finish.");
            		Toast.makeText(mParent, "Finish.", Toast.LENGTH_LONG).show();
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
            		return;
            	}
                UniquePattern c = mSouList.get(mCurrentPage);
                mItemId = c.MusicId;
                if(!mWebMusicIds.containsKey(mItemId) || mWebMusicIds.get(mItemId).idOnWebPage == "")
                {
            		new AlertDialog.Builder(mParent)
        	        .setIcon(android.R.drawable.ic_dialog_alert)
        	        .setMessage(mParent.getResources().getString(R.string.illegal_id_alert))
        	        .setCancelable(true)
        	        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
        	            public void onClick(DialogInterface dialog, int whichButton) {
        	            	
        	            }
        	        }).show();
            		mDialog.cancel();
                	return;
                }
                else
                {
                	mWebItemId = mWebMusicIds.get(mItemId).idOnWebPage;
                }
                mPattern = c.Pattern;
                mRequestUri = mUriH+String.valueOf(mWebItemId)+mUriM+String.valueOf(getPatternInt(mPattern))+mUriF;
                mLogView.setText("Loading...\n"+(mRivalName==null?"My Score\n":("Rival: "+mRivalName+"\n")) + mPattern.toString() + " : " + c.musics.get(mItemId).Name);
                mWebView.loadUrl(mRequestUri);
            }
        });
    }
	
}

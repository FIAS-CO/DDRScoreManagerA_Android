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

import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicId;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.WebTitleToMusicIdList;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

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
        if(intent == null)
        {
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
        
        WebViewClient client = new WebViewClient() 
        {
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
    	
    	WebChromeClient chrome = new WebChromeClient() 
    	{
    		public void onProgressChanged(WebView view, int progress) {
    			//try { Thread.sleep(10);} catch (InterruptedException e) {}
    			mWebProgress.setProgress(1+progress);
    			mProgress.setProgress(mCurrentPage*100+(mPageCount==0?0:progress));
    			mPercent.setText((100 * mCurrentPage + (mPageCount == 0 ? 0 : progress)) / (mPageCount == 0 ? 10000 : mPageCount) +"%");
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
	
	public void setArguments(AlertDialog dialog, boolean getDouble, String rivalId, String rivalName)
	{
		mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mDouble = getDouble;
        mScoreList = FileReader.readScoreList(mParent, mRivalId);
    }
	
	public View getView()
	{
		return mView;
	}
	
	public void start()
	{
		if(mDialog == null)
		{
			return;
		}
		FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);
		String sd;
		if(mDouble)
		{
			sd = "double";
		}
		else
		{
			sd = "single";
		}

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
        	mUriH += "playdata/music_data_"+sd+".html?offset=";
        	mUriF = "";
        }
        else
        {
        	mUriH += "rival/rival_musicdata_"+sd+".html?offset=";
        	mUriF = "&rival_id="+mRivalId;
        }
        //String uri = "file:///android_asset/status.html";
    	mLogView.setText((mRivalName==null?"My Score\n":("Rival: "+mRivalName+"\n"))+(mDouble?"DP\n":"SP\n")+mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));
        mWebView.loadUrl(mUriH+"0"+mUriF);
	}
	
	public void cancel()
	{
		mCanceled = true;
        WebView web = mView.findViewById(R.id.webView);
        web.stopLoading();
        mPageCount = 1;
        if(mDialog != null)
        {
        	mDialog.cancel();
        }
	}

	@android.webkit.JavascriptInterface
    public void viewSource(final String src) {
    	//Log.d("list", src);
        mHandler.post(() -> {
			mWebProgress.setProgress(0);

			if(mCanceled)
			{
				return;
			}

			if(mPageCount == 0)
			{
				mPageCount = getPageCount(src);
				if(mPageCount == 0)
				{
					switch(TextUtil.checkLoginStatus(src))
					{
						case NOT_LOGGED_IN:
							Intent intent = new Intent();
							intent.setClassName("jp.linanfine.dsma","jp.linanfine.dsma.activity.GateLogin");

							cancel();

							mParent.startActivityForResult(intent, LoginRequestCode);
							(new Thread(() -> {
								try { Thread.sleep(1000);} catch (InterruptedException ignored) {}
								mHandler.post(() -> mDialog.cancel());
								}
							)).start();

							return;
						case UNKNOWN:
							Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
							(new Thread(() -> {
								try { Thread.sleep(1000);} catch (InterruptedException ignored) {}
								mHandler.post(() -> mDialog.cancel());
								}
							)).start();
							return;
					}
				}
				mMax.setText(String.valueOf(mPageCount));
				mProgress.setMax(mPageCount*100);
			}

			try
			{
				analyzeScoreList(src);
			}
			catch(Exception e)
			{
				return;
			}

			try { Thread.sleep(3000);} catch (InterruptedException ignored) {}
			++mCurrentPage;
			mProgress.setProgress(mCurrentPage*100);
			mLogView.setText(String.format("%s%s%s",
					mRivalName == null ? "My Score\n"
							: ("Rival: " + mRivalName + "\n"),
					mDouble ? "DP\n" : "SP\n",
					mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPage)));
			mCurrent.setText(String.valueOf(mCurrentPage));
			mPercent.setText((mPageCount == 0 ? 0 : (100 * mCurrentPage / mPageCount)) +"%");
			if(mCurrentPage >= mPageCount)
			{
				Toast.makeText(mParent, "Finish.", Toast.LENGTH_LONG).show();
				(new Thread(() -> {
					try { Thread.sleep(1000);} catch (InterruptedException ignored) {}
					mHandler.post(() -> mDialog.cancel());
					}
				)).start();
				return;
			}
			mWebView.loadUrl(mUriH + mCurrentPage + mUriF);
		});
    }

	private int getPageCount(String src)
	{
		String cmpStartPagerBox = "<div id=\"paging_box\">";
		String cmpEndPagerBox = "<div class=\"arrow\"";
		String cmpPageNum = "<div class=\"page_num\"";

		int pointStartPagerBox = src.indexOf(cmpStartPagerBox);
		if(pointStartPagerBox < 0)
		{
			return 0;
		}
		String blockPagerBox = src.substring(pointStartPagerBox);
		blockPagerBox = blockPagerBox.substring(0, blockPagerBox.indexOf(cmpEndPagerBox));
		return countStringInString(blockPagerBox, cmpPageNum);
	}

	private static int countStringInString(String target, String searchWord) {
		return (target.length() - target.replaceAll(searchWord, "").length()) / searchWord.length();
	}

	private void analyzeScoreList(String src)
	{
		WebView web = mView.findViewById(R.id.webView);
		String uri = web.getUrl();

		String idDiffEnd;
		if(mRivalId == null)
		{
			idDiffEnd = "\"";
		}
		else
		{
			idDiffEnd = "&";
		}

		if(uri != null)
		{
			if(mRivalId == null)
			{
				if(!uri.contains("playdata/music_data"))
				{
					return;
				}
			}
			else
			{
				if(!uri.contains("rival/rival_musicdata"))
				{
					return;
				}
			}
		}

		String musicBlockStartText = "<tr class=\"data\">";
		int musicBlockStartTextLength = musicBlockStartText.length();
		String musicBlockEndText = "</tr>";
		int musicBlockEndTextLength = musicBlockEndText.length();
		String titleLinkText = "<a href=\"/game/ddr/ddra3/p/playdata/music_detail.html?index=";
		if(mRivalId != null)
		{
			titleLinkText = "<a href=\"/game/ddr/ddra3/p/rival/music_detail.html?index=";
		}
		int titleLinkTextLength = titleLinkText.length();
		String patternBlockStartText = "<td class=\"rank\" id=\"";
		int patternBlockStartTextLength = patternBlockStartText.length();
		String patternBlockEndText = "</td>";
		int patternBlockEndTextLength = patternBlockEndText.length();

		String parsingText = src;

		StringBuilder sb = new StringBuilder();

		boolean scoreExists = false;
		while(parsingText.contains(musicBlockStartText))
		{
			parsingText = parsingText.substring(parsingText.indexOf(musicBlockStartText)+musicBlockStartTextLength);
			Log.d("current", parsingText);
			String musicBlock = parsingText.substring(0, parsingText.indexOf(musicBlockEndText));
			//Log.e("DSM", musicBlock);
			parsingText = parsingText.substring(musicBlock.length()+musicBlockEndTextLength);
			musicBlock = musicBlock.substring(musicBlock.indexOf(titleLinkText)+titleLinkTextLength);
			String idText = musicBlock.substring(0, musicBlock.indexOf(idDiffEnd));
			//int musicId = Integer.valueOf(idText);
			String musicName = musicBlock.substring(musicBlock.indexOf(">")+1);
			musicName = musicName.substring(0, musicName.indexOf("</")).trim();
			musicName = TextUtil.escapeWebTitle(musicName);
			MusicId mi;
			if(mMusicIds.containsKey(musicName))
			{
				mi = mMusicIds.get(musicName);
			}
			else
			{
				//mi = new MusicId();
				//mi.musicId = musicId; //////////////////////////// zero
				continue; // skip
			}
			//mi.idOnWebPage = musicId;
			//mMusicIds.put(musicName, mi);
			int musicIdSaved = mi.musicId;
			MusicData musicData;
			if(mMusicList.containsKey(musicIdSaved))
			{
				musicData = mMusicList.get(musicIdSaved);
			}
			else
			{
				musicData = new MusicData();
			}
			sb.append(musicData.Name).append(": \n    ");

			MusicScore ms;
			if(mScoreList.containsKey(musicIdSaved))
			{
				ms = mScoreList.get(musicIdSaved);
				mScoreList.remove(musicIdSaved);
			}
			else
			{
				ms = new MusicScore();
			}

			while(musicBlock.contains(patternBlockStartText))
			{
				ScoreData sd = new ScoreData();

				String patternLinkText = titleLinkText+idText+"&amp;diff=";
				int patternLinkTextLength = patternLinkText.length();
				musicBlock = musicBlock.substring(musicBlock.indexOf(patternBlockStartText)+patternBlockStartTextLength);
				String patternBlock = musicBlock.substring(0, musicBlock.indexOf(patternBlockEndText));
				musicBlock = musicBlock.substring(patternBlock.length()+patternBlockEndTextLength);
				patternBlock = patternBlock.substring(patternBlock.indexOf(patternLinkText));
				String diffText = patternBlock.substring(patternLinkTextLength);
				diffText = diffText.substring(0, diffText.indexOf(idDiffEnd));
				int diff = Integer.parseInt(diffText);
				if (patternBlock.contains(">")) {
					patternBlock = patternBlock.substring(patternBlock.indexOf("<"));
				}
				if (patternBlock.contains("full_none")) {
					sd.FullComboType = FullComboType.None;
				}
				else if (patternBlock.contains("full_good")) {
					sd.FullComboType = FullComboType.GoodFullCombo;
				}
				else if (patternBlock.contains("full_great")) {
					sd.FullComboType = FullComboType.FullCombo;
				}
				else if (patternBlock.contains("full_perfect")) {
					sd.FullComboType = FullComboType.PerfectFullCombo;
				}
				else if (patternBlock.contains("full_mar")) {
					sd.FullComboType = FullComboType.MerverousFullCombo;
				}
				Log.d(musicData.Name, "a");
				if(patternBlock.contains("rank_s_none"))
				{
					Log.d(musicData.Name, "a-1");
					sd.Score = 0;
					sd.Rank = MusicRank.Noplay;
					Log.d(musicData.Name, "a-2");
				}
				else
				{
					Log.d(musicData.Name, "a-3");
					String scoreText = patternBlock.replaceAll("<.*?>", "");
					Log.d(musicData.Name, scoreText);
					sd.Score = Integer.parseInt(scoreText);
					Log.d(musicData.Name, "a-4");
					if(patternBlock.contains("rank_s_e"))
					{
						sd.Rank = MusicRank.E;
					}
					else if(sd.Score < 550000) {
						sd.Rank = MusicRank.D;
					}
					else if(sd.Score < 590000) {
						sd.Rank = MusicRank.Dp;
					}
					else if(sd.Score < 600000) {
						sd.Rank = MusicRank.Cm;
					}
					else if(sd.Score < 650000) {
						sd.Rank = MusicRank.C;
					}
					else if(sd.Score < 690000) {
						sd.Rank = MusicRank.Cp;
					}
					else if(sd.Score < 700000) {
						sd.Rank = MusicRank.Bm;
					}
					else if(sd.Score < 750000) {
						sd.Rank = MusicRank.B;
					}
					else if(sd.Score < 790000) {
						sd.Rank = MusicRank.Bp;
					}
					else if(sd.Score < 800000) {
						sd.Rank = MusicRank.Am;
					}
					else if(sd.Score < 850000) {
						sd.Rank = MusicRank.A;
					}
					else if(sd.Score < 890000) {
						sd.Rank = MusicRank.Ap;
					}
					else if(sd.Score < 900000) {
						sd.Rank = MusicRank.AAm;
					}
					else if(sd.Score < 950000) {
						sd.Rank = MusicRank.AA;
					}
					else if(sd.Score < 990000) {
						sd.Rank = MusicRank.AAp;
					}
					else {
						sd.Rank = MusicRank.AAA;
					}
					Log.d(musicData.Name, "a-5");
				}
				Log.d(musicData.Name, "b");

				ScoreData msd;
				switch(diff)
				{
					case 0:
						msd = ms.bSP;
						sb.append("bSP:");
						break;
					case 1:
						msd = ms.BSP;
						sb.append("BSP:");
						break;
					case 2:
						msd = ms.DSP;
						sb.append("DSP:");
						break;
					case 3:
						msd = ms.ESP;
						sb.append("ESP:");
						break;
					case 4:
						msd = ms.CSP;
						sb.append("CSP:");
						break;
					case 5:
						msd = ms.BDP;
						sb.append("BDP:");
						break;
					case 6:
						msd = ms.DDP;
						sb.append("DDP:");
						break;
					case 7:
						msd = ms.EDP;
						sb.append("EDP:");
						break;
					case 8:
						msd = ms.CDP;
						sb.append("CDP:");
						break;
					default:
						msd = new ScoreData();
						sb.append("?:");
						break;
				}
				Log.d(musicData.Name, "c");
				sb.append(sd.Score).append(" / ");
				sd.MaxCombo = msd.MaxCombo;
				sd.ClearCount = msd.ClearCount;
				sd.PlayCount = msd.PlayCount;

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
				Log.d(musicData.Name, "d");
				switch(diff)
				{
					case 0:
						ms.bSP = sd;
						break;
					case 1:
						ms.BSP = sd;
						break;
					case 2:
						ms.DSP = sd;
						break;
					case 3:
						ms.ESP = sd;
						break;
					case 4:
						ms.CSP = sd;
						break;
					case 5:
						ms.BDP = sd;
						break;
					case 6:
						ms.DDP = sd;
						break;
					case 7:
						ms.EDP = sd;
						break;
					case 8:
						ms.CDP = sd;
						break;
				}
				Log.d(musicData.Name, "e");
				mScoreList.put(musicIdSaved, ms);
				scoreExists = true;
				Log.d(musicData.Name, String.valueOf(diff));

				//Log.d("dsm", musicBlock);
			}
			sb.append("\n");
			Log.d(musicData.Name, "huga");
		}

		Log.d("hage", "hage");
		if(!scoreExists)
		{
			return;
		}

		FileReader.saveScoreData(mParent, mRivalId, mScoreList);
	}
}

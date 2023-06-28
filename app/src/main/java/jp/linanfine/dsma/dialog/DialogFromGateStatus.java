package jp.linanfine.dsma.dialog;

import java.text.DecimalFormat;
import java.util.TreeMap;

import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.StatusData;
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

public class DialogFromGateStatus {
	
	public static int LoginRequestCode = 20006;
	
	private Handler mHandler = new Handler();
	private Activity mParent;
	private AlertDialog mDialog;
	private View mView;
	
	private WebView mWebView;
	private TextView mLogView;
	private ProgressBar mWebProgress;
	private String mRivalId;
	private String mRivalName;
	private String mRequestUri;
	private GateSetting mGateSetting;
	
	private StatusData mStatusData;

	private boolean mCanceled = false;
	
	@SuppressLint("SetJavaScriptEnabled")
	public DialogFromGateStatus(Activity parent) {
		mParent = parent;
		mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if(intent == null)
        {
        	return;
        }
        
		mWebProgress = (ProgressBar)mView.findViewById(R.id.webProgress);
		mLogView = (TextView)mView.findViewById(R.id.log);
		
		mLogView.setText(mParent.getResources().getString(R.string.dialog_log_get_status));
		
		mStatusData = FileReader.readStatusData(mParent);
        
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
    			mWebProgress.setProgress(1+progress);
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
	
	public void setArguments(AlertDialog dialog, String rivalId, String rivalName)
	{
		mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
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
		FileReader.requestAd((LinearLayout)mView.findViewById(R.id.adContainer), mParent);

		mGateSetting = FileReader.readGateSetting(mParent);
		if(mGateSetting.FromA3)
		{
			mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
		}
		else{
			mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
		}

        if(mRivalId == null)
        {
        	mRequestUri += "playdata/index.html";
        }
        else
        {
        	mRequestUri += "rival/rival_status.html?rival_id="+mRivalId;
        }
        //String uri = "file:///android_asset/status.html";
        mWebView.loadUrl(mRequestUri);
	}
	
	public void cancel()
	{
		mCanceled = true;
        WebView web = (WebView) mView.findViewById(R.id.webView);
        web.stopLoading();
        if(mDialog != null)
        {
        	mDialog.cancel();
        }
	}

	private boolean analyzeStatus(String src)
	{
        WebView web = (WebView) mView.findViewById(R.id.webView);
    	String uri = web.getUrl();
    	Log.e("hoge", "!!!1");
		Log.e("uri", uri);
		Log.e("ruri", mRequestUri);
    	if(!uri.equals(mRequestUri))
    	{
    		return false;
    	}
    	
    	Log.e("hoge", "!!!2");
    	try
    	{
    	
    	String parsingText = src;
    	parsingText = parsingText;
    	String block = "";
    	
    	String cmpTdEnd = "</td>";
    	int cmpTdEndLength = cmpTdEnd.length();
    	String cmpDancerName = "<th>ダンサーネーム</th><td>";
    	int cmpDancerNameLength = cmpDancerName.length();
    	String cmpDdrCode = "<th>DDR-CODE</th><td>";
    	int cmpDdrCodeLength = cmpDdrCode.length();
    	String cmpTodofuken = "<th>所属都道府県</th><td>";
    	int cmpTodofukenLength = cmpTodofuken.length();
    	//String cmpEnjoyLevel = "<th>エンジョイレベル</th><td>";
    	//int cmpEnjoyLevelLength = cmpEnjoyLevel.length();
    	//String cmpEnjoyLevelEnd = "<span style=\"color:black;font-size:12px;\">";
    	//int cmpEnjoyLevelEndLength = cmpEnjoyLevelEnd.length();
    	//String cmpEnjoyLevelNextExp = "次のレベルまであと<b>";
    	//int cmpEnjoyLevelNextExpLength = cmpEnjoyLevelNextExp.length();
    	//String cmpEnjoyLevelNextExpEnd = "</b>Exp</span>";
    	//int cmpEnjoyLevelNextExpEndLength = cmpEnjoyLevelNextExpEnd.length();
    	String cmpPlayCount = "<th>総プレー回数</th><td>";
    	int cmpPlayCountLength = cmpPlayCount.length();
    	String cmpLastPlay = "<th>最終プレー日時</th><td>";
    	int cmpLastPlayLength = cmpLastPlay.length();
    	Log.e("hoge", "!!!3");

		parsingText = parsingText.substring(parsingText.indexOf(cmpDancerName)+cmpDancerNameLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.DancerName = block;

		parsingText = parsingText.substring(parsingText.indexOf(cmpDdrCode)+cmpDdrCodeLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.DdrCode = block;

		parsingText = parsingText.substring(parsingText.indexOf(cmpTodofuken)+cmpTodofukenLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.Todofuken = block;
/*
		parsingText = parsingText.substring(parsingText.indexOf(cmpEnjoyLevel)+cmpEnjoyLevelLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpEnjoyLevelEnd));
		parsingText = parsingText.substring(block.length()+cmpEnjoyLevelEndLength+1);
		mStatusData.EnjoyLevel = Integer.valueOf(block.replace("　", "").trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpEnjoyLevelNextExp)+cmpEnjoyLevelNextExpLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpEnjoyLevelNextExpEnd));
		parsingText = parsingText.substring(block.length()+cmpEnjoyLevelNextExpEndLength+1);
		mStatusData.EnjoyLevelNextExp = Integer.valueOf(block.trim());
*/
    	Log.e("hoge", "!!!4");
		parsingText = parsingText.substring(parsingText.indexOf(cmpPlayCount)+cmpPlayCountLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.PlayCount = Integer.valueOf(block.replace("回", "").trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpLastPlay)+cmpLastPlayLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.LastPlay = block;
    	Log.e("hoge", "!!!5");

    	/*String cmpStream = "<th>STREAM</th><td>:</td><td>";
    	int cmpStreamLength = cmpStream.length();
    	String cmpChaos = "<th>CHAOS</th><td>:</td><td>";
    	int cmpChaosLength = cmpChaos.length();
    	String cmpVoltage = "<th>VOLTAGE</th><td>:</td><td>";
    	int cmpVoltageLength = cmpVoltage.length();
    	String cmpFreeze = "<th>FREEZE</th><td>:</td><td>";
    	int cmpFreezeLength = cmpFreeze.length();
    	String cmpAir = "<th>AIR</th><td>:</td><td>";
    	int cmpAirLength = cmpAir.length();
    	String cmpShougou = "<th>称号</th><td>";
    	int cmpShougouLength = cmpShougou.length();*/
    	String cmpOPlayCount = "<th>プレー回数</th><td>";
    	int cmpOPlayCountLength = cmpOPlayCount.length();
    	String cmpOLastPlay = "<th>最終プレー日時</th><td>";
    	int cmpOLastPlayLength = cmpOLastPlay.length();
    	/*
		parsingText = parsingText.substring(parsingText.indexOf(cmpStream)+cmpStreamLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.SingleMGR.Stream = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpChaos)+cmpChaosLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.SingleMGR.Chaos = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpVoltage)+cmpVoltageLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.SingleMGR.Voltage = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpFreeze)+cmpFreezeLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.SingleMGR.Freeze = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpAir)+cmpAirLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength+1);
		mStatusData.SingleMGR.Air = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpShougou)+cmpShougouLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.SingleMGR.Shougou = block;
*/
		parsingText = parsingText.substring(parsingText.indexOf(cmpOPlayCount)+cmpOPlayCountLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.PlayCountSingle = Integer.valueOf(block.replace("回", "").trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpOLastPlay)+cmpOLastPlayLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.LastPlaySingle = block;
    	Log.e("hoge", "!!!6");
/*
		parsingText = parsingText.substring(parsingText.indexOf(cmpStream)+cmpStreamLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Stream = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpChaos)+cmpChaosLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Chaos = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpVoltage)+cmpVoltageLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Voltage = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpFreeze)+cmpFreezeLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Freeze = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpAir)+cmpAirLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Air = Integer.valueOf(block.trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpShougou)+cmpShougouLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.DoubleMGR.Shougou = block;
*/
		parsingText = parsingText.substring(parsingText.indexOf(cmpOPlayCount)+cmpOPlayCountLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.PlayCountDouble = Integer.valueOf(block.replace("回", "").trim());

		parsingText = parsingText.substring(parsingText.indexOf(cmpOLastPlay)+cmpOLastPlayLength);
		block = parsingText.substring(0, parsingText.indexOf(cmpTdEnd));
		parsingText = parsingText.substring(block.length()+cmpTdEndLength);
		mStatusData.LastPlayDouble = block;

    	Log.e("hoge", "!!!7");
		FileReader.saveStatusData(mParent, mStatusData);
    	Log.e("hoge", "!!!8");

    	}
    	catch(Exception e)
    	{
        	Log.e("hoge", "!!!9");
    		e.printStackTrace();
    		return false;
    	}
    	Log.e("hoge", "!!!10");

        return true;
	}

	@android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(new Runnable() {
            public void run() {
            	mWebProgress.setProgress(0);

            	if(mCanceled)
            	{
            		return;
            	}
            	if(!analyzeStatus(src))
            	{
            	switch(TextUtil.checkLoggedIn(src))
            	{
	            	case 1:
	        	        Intent intent=new Intent();
	        	        intent.setClassName("jp.linanfine.dsma","jp.linanfine.dsma.activity.GateLogin");
	        	        
	        	        cancel();
	        	 
	        	        mParent.startActivityForResult(intent, LoginRequestCode);
	        	        
	        	        break;
	            	case -1:
	            		Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
	            		break;
            		default:
                    	//analyzeStatus(src);
            			break;
            	}
            	}
            	

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

            }
        });
    }
	
}

package jp.linanfine.dsma.dialog;

import java.util.ArrayList;

import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DialogFromGateRivalList {
	
	public static int LoginRequestCode = 20004;
	
	private Handler mHandler = new Handler();
	private Activity mParent;
	private AlertDialog mDialog;
	private View mView;
	
	private WebView mWebView;
	private TextView mLogView;
	private ProgressBar mWebProgress;
	private String mRequestUri;
	private GateSetting mGateSetting;
	
	private String mToastString;

	private boolean mCanceled = false;
	
	@SuppressLint("SetJavaScriptEnabled")
	public DialogFromGateRivalList(Activity parent) {
		mParent = parent;
		mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if(intent == null)
        {
        	return;
        }
        
		mWebProgress = (ProgressBar)mView.findViewById(R.id.webProgress);
		mLogView = (TextView)mView.findViewById(R.id.log);
		
		//mLogView.setText(mParent.getResources().getString(R.string.dialog_log_get_page));
		mLogView.setVisibility(View.GONE);
        
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
	
	public void setArguments(AlertDialog dialog)
	{
		mDialog = dialog;
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
        //String uri = "file:///android_asset/status.html";
		mGateSetting = FileReader.readGateSetting(mParent);
		if(mGateSetting.FromA3)
		{
			mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/rival/index.html";
		}
		else{
			mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra20/p/rival/index.html";
		}
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

	private boolean analyzeRivalList(String src)
	{

        WebView web = (WebView) mView.findViewById(R.id.webView);
    	String uri = web.getUrl();
    	if(!uri.equals(mRequestUri))
    	{
    		return false;
    	}
    	
    	String idSearchText = "<a href=\"/game/ddr/ddra3/p/rival/rival_status.html?rival_id=";
    	int idSearchTextLength = idSearchText.length();
    	String nameSearchText = "\">";
    	int nameSearchTextLength = nameSearchText.length();

		String parsingText = src;
		
		ArrayList<RivalData> rivals;
		rivals = new ArrayList<RivalData>();

        StringBuilder toastString = new StringBuilder(); 
        toastString.append("Complete !!\n\n");
        
        try
        {
        	
        	if(!parsingText.contains("ACTIVE"))
        	{
        		return false;
        	}

		while(parsingText.contains(idSearchText))
		{
			parsingText = parsingText.substring(parsingText.indexOf(idSearchText)+idSearchTextLength);
			String idText = parsingText.substring(0, 8);
        	try
        	{
            	Integer.valueOf(idText);
        	}
        	catch(Exception e)
        	{
        		idText = "";
        	}
        	if(idText.length() < 8)
        	{
        		continue;
        	}
        	parsingText = parsingText.substring(parsingText.indexOf(nameSearchText)+nameSearchTextLength);
        	String nameText = parsingText.substring(0, parsingText.indexOf("<"));
        	RivalData r = new RivalData();
        	r.Id = idText;
        	r.Name = nameText;
        	rivals.add(r);
        	toastString.append(r.Id+" / "+r.Name+"\n");
		}
		
		mToastString = toastString.toString();
		
		if(rivals.size() == 0)
		{
            Toast.makeText(mParent, "No Rival Data found.", Toast.LENGTH_LONG).show();
            return true;
		}
		
		//if("Add".equals(mWriteMode))
		{
			ArrayList<RivalData> savedRivals = FileReader.readRivals(mParent);
			for(RivalData r: rivals)
			{
				savedRivals.add(r);
			}
			rivals = savedRivals;
		}
		
        FileReader.saveRivals(mParent, rivals, false);
        if(FileReader.readActiveRival(mParent) >= rivals.size())
        {
        	FileReader.saveActiveRival(mParent, -1);
        }
        
        }
        catch(Exception e)
        {
        	return false;
        }

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

            	if(!analyzeRivalList(src))
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
                    	/*try
                    	{
                        	analyzeRivalList(src);
                    	}
                    	catch(Exception e)
                    	{
                    		return;
                    	}*/

                    	Toast.makeText(mParent, mToastString, Toast.LENGTH_LONG).show();
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

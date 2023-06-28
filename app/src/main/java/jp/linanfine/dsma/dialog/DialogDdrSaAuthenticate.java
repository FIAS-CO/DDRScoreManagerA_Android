package jp.linanfine.dsma.dialog;

import java.text.DecimalFormat;
import java.util.TreeMap;

import jp.linanfine.dsma.util.debug.DebugOutput;
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

public class DialogDdrSaAuthenticate {
	
	private static String sPasswordEncryptionUri = "http://skillattack.com/sa4/sa4_encrypt.php";

	private String mSaUri = "";	
	private String mRequestUri = "ddr_score_manager_score_import.php";

	private Handler mHandler = new Handler();
	private Activity mParent;
	private AlertDialog mDialog;
	private View mView;
	
	private WebView mWebView;
	private TextView mLogView;
	private ProgressBar mWebProgress;
	private String mDdrCode;
	private String mPassword;
	private String mEncryptedPassword;
	private String mPostQuery;
	
	private int mPhase = 0;
	
	private boolean mCanceled = false;
	
	@SuppressLint("SetJavaScriptEnabled")
	public DialogDdrSaAuthenticate(Activity parent) {
		mParent = parent;
		mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if(intent == null)
        {
        	return;
        }
        
		mWebProgress = (ProgressBar)mView.findViewById(R.id.webProgress);
		mLogView = (TextView)mView.findViewById(R.id.log);
		
		mLogView.setText(mParent.getResources().getString(R.string.authenticating));
		
        WebViewClient client = new WebViewClient() 
        {
            @Override
            public void onPageFinished(WebView view, String url) {
				//Log.e("SAA", "10");
    			view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            	//mWebProgress.setProgress(1+(mPhase*50));
				//Log.e("SAA", "20");
            }
    	};
    	
    	WebChromeClient chrome = new WebChromeClient() 
    	{
    		public void onProgressChanged(WebView view, int progress) {
    			mWebProgress.setProgress(1+progress/2+(mPhase*50));
				//Log.e("SAA", "30");
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
		//Log.e("SAA", "1");

	}
	
	public void setArguments(AlertDialog dialog, String saUri, String ddrCode, String password)
	{
		mDialog = dialog;
		mSaUri = saUri;
        mDdrCode = ddrCode;
        mPassword = password;
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

		mPostQuery = "plain="+mPassword;
        mWebView.postUrl(sPasswordEncryptionUri, mPostQuery.getBytes());
		//Log.e("SAA", "2");
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

	@android.webkit.JavascriptInterface
    public void viewSource(final String src) {
		Log.e("SAA", "40");
        mHandler.post(new Runnable() {
            public void run() {
            	//mWebProgress.setProgress(0);
				//Log.e("SAA", "3");
            	if(mCanceled || mPhase > 1)
            	{
            		return;
            	}
            	//Log.e("SAA", "4");
                WebView web = (WebView) mView.findViewById(R.id.webView);
            	String uri = web.getUrl();
            	if(uri.equals(sPasswordEncryptionUri))
            	{
            		mEncryptedPassword = src.substring(0, src.indexOf("</body>")).substring(src.indexOf("<body>")+6);
            		DebugOutput.getInstance(mParent).ToastLong(mEncryptedPassword);
            		mPostQuery = "data="+mDdrCode+"\t"+mEncryptedPassword+"\n";
            		FileReader.saveText(mParent, mPostQuery, "Request.txt");
            		mPhase = 1;
                    mWebView.postUrl(mSaUri+mRequestUri, mPostQuery.getBytes());
            	}
            	else
            	{
            		//analyzeExportResult(src);

            		FileReader.saveText(mParent, src, "Result.txt");
            		
            		String dat = src;
            		if(!src.contains("<pre>") || !src.contains("</pre>"))
            		{
            			dat = "<pre>Authentication Failure</pre>";
            		}

            		String result = dat.substring(0, dat.indexOf("</pre>")).substring(dat.indexOf("<pre>")+5);

            		if(result.startsWith("Authentication Failure"))
            		{
            			Toast.makeText(mParent, mParent.getResources().getString(R.string.authentication_failure), Toast.LENGTH_LONG).show();
            		}
            		else
            		{
            			FileReader.saveDdrSaAuthentication(mParent, mDdrCode, mEncryptedPassword);
            			Toast.makeText(mParent, mParent.getResources().getString(R.string.authentication_succeed), Toast.LENGTH_LONG).show();
            		}
            		
            		mPhase = 2;
            		
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
            }
        });
    }
	
}

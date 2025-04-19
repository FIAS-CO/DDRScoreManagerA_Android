package jp.linanfine.dsma.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;

public class GateLoginManually extends Activity {

    private Handler handler = new Handler();
	private String mRequestUri;
	private ProgressBar mProgressBar;
	private WebView mWebView;

	@Override
	public void onBackPressed() 
	{
        WebView web = (WebView) findViewById(R.id.webView);
        web.stopLoading();
        super.onBackPressed();
	};

	@Override
	public void onResume()
	{
        super.onResume();
        ActivitySetting.setTitleBarShown(this.findViewById(R.id.titleBar));
		FileReader.requestAd((LinearLayout)this.findViewById(R.id.adContainer), this);
	}

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
		setContentView(R.layout.activity_gate_login);

		 Toast.makeText(GateLoginManually.this, GateLoginManually.this.getResources().getString(R.string.message_gate_login_manually), Toast.LENGTH_LONG).show();
		 
		Intent intent = getIntent();
        if(intent == null)
        {
        	return;
        }
        this.setResult(Activity.RESULT_CANCELED);
        
        mProgressBar = (ProgressBar)this.findViewById(R.id.progressBar);
        
        WebViewClient client = new WebViewClient() 
        {
            @Override
            public void onPageFinished(WebView view, String url) {
            	//Log.d("", view.getUrl());
                //((TextView)GateLogin.this.findViewById(R.id.uri)).setText(url);
    			view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            	mProgressBar.setProgress(5);
            }
    	};
    	
    	WebChromeClient chrome = new WebChromeClient() 
    	{
    		public void onProgressChanged(WebView view, int progress) {
    			mProgressBar.setProgress(5+progress);
    		}
    	};

    	mWebView = (WebView) findViewById(R.id.webView);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
        //mWebView.getSettings().setBlockNetworkImage(true);
    	mWebView.getSettings().setBuiltInZoomControls(true);
    	mWebView.setWebViewClient(client);
    	mWebView.setWebChromeClient(chrome);
        //mWebView.getSettings().setJavaScriptEnabled(true);
    	mWebView.getSettings().setJavaScriptEnabled(true);
    	mWebView.addJavascriptInterface(this, "viewsourceactivity");
        mRequestUri = "https://p.eagate.573.jp/gate/p/login.html";
        //mRequestUri = "file:///android_asset/1.html";
        //((TextView)this.findViewById(R.id.uri)).setText(mRequestUri);
        mWebView.loadUrl(mRequestUri);

	}

	@android.webkit.JavascriptInterface
    public void viewSource(final String src) {

    }
}

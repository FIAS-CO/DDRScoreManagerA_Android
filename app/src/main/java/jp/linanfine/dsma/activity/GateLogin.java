package jp.linanfine.dsma.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;

public class GateLogin extends Activity {

    private final Handler handler = new Handler();
    private String mRequestUri;
    private ProgressBar mProgressBar;
    private WebView mWebView;

    @Override
    public void onBackPressed() {
        WebView web = findViewById(R.id.webView);
        web.stopLoading();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
        FileReader.requestAd(this.findViewById(R.id.adContainer), this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        setContentView(R.layout.activity_gate_login);

        mRetryCount = 0;
        mLoginFormShown = false;

        Toast.makeText(GateLogin.this, GateLogin.this.getResources().getString(R.string.message_gate_login), Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        this.setResult(Activity.RESULT_CANCELED);

        mProgressBar = this.findViewById(R.id.progressBar);

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
//            	 Log.d("", view.getUrl());
//                 ((TextView)GateLogin.this.findViewById(R.id.uri)).setText(url);
                view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                mProgressBar.setProgress(5);
            }
        };

        WebChromeClient chrome = new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setProgress(5 + progress);
            }
        };

        mWebView = findViewById(R.id.webView);
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

    private int mRetryCount = 0;
    private boolean mLoginFormShown = false;

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        Log.d("hoge", src);
        handler.post(() -> {
            mProgressBar.setProgress(0);
            (new Thread(() -> {
                if (0 == TextUtil.checkLoggedIn(src)) {
                    if (mLoginFormShown) {
                        Log.e("LOGIN", "1");
                        //try { Thread.sleep(1000);} catch (InterruptedException e) {}
                        handler.post(() -> {
                            Toast.makeText(GateLogin.this, GateLogin.this.getResources().getString(R.string.message_gate_loggedin), Toast.LENGTH_SHORT).show();
                            //try { Thread.sleep(1000);} catch (InterruptedException e) {}
                            GateLogin.this.setResult(Activity.RESULT_OK);
                            GateLogin.this.finish();
                        });
                    } else {
                        Log.e("LOGIN", "2");
                        handler.post(() -> {
                            Toast.makeText(GateLogin.this, GateLogin.this.getResources().getString(R.string.message_gate_already_logged_in_but_shown), Toast.LENGTH_LONG).show();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            GateLogin.this.finish();
                        });
                    }
                } else if (!TextUtil.isLoginForm(src)) {
                    ++mRetryCount;
                    if (mRetryCount > 3) {
                        Log.e("LOGIN", "3");
                        handler.post(() -> {
                            Toast.makeText(GateLogin.this, GateLogin.this.getResources().getString(R.string.message_gate_loggedin_failed), Toast.LENGTH_SHORT).show();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            GateLogin.this.finish();
                        });
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    handler.post(() -> {
                        Toast.makeText(GateLogin.this, GateLogin.this.getResources().getString(R.string.message_gate_login), Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        mWebView.loadUrl(mRequestUri);
                    });
                } else {
                    Log.e("LOGIN", "4");
                    mLoginFormShown = true;
                }
            })).start();
        });
    }
}

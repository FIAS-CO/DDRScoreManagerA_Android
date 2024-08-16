package jp.linanfine.dsma.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.RivalData;

public class DialogFromGateRivalList {

    public static int LoginRequestCode = 20004;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    private WebView mWebView;
    private ProgressBar mWebProgress;
    private String mRequestUri;

    private String mToastString;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateRivalList(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mWebProgress = mView.findViewById(R.id.webProgress);
        TextView mLogView = mView.findViewById(R.id.log);

        mLogView.setVisibility(View.GONE);

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
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebViewClient(client);
        mWebView.setWebChromeClient(chrome);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(this, "viewsourceactivity");

    }

    public void setArguments(AlertDialog dialog) {
        mDialog = dialog;
    }

    public View getView() {
        return mView;
    }

    public void start() {
        if (mDialog == null) {
            return;
        }
        FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);
        GateSetting mGateSetting = FileReader.readGateSetting(mParent);
        if (mGateSetting.FromNewSite) {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/rival/index.html";
        } else {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra20/p/rival/index.html";
        }
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

    private boolean analyzeRivalList(String src) {
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();
        assert uri != null;
        if (!uri.equals(mRequestUri)) {
            return false;
        }

        String idSearchText = "<a href=\"/game/ddr/ddra3/p/rival/rival_status.html?rival_id=";
        int idSearchTextLength = idSearchText.length();
        String nameSearchText = "\">";
        int nameSearchTextLength = nameSearchText.length();

        String parsingText = src;

        ArrayList<RivalData> rivals;
        rivals = new ArrayList<>();

        StringBuilder toastString = new StringBuilder();
        toastString.append("Complete !!\n\n");

        try {
            if (!parsingText.contains("ACTIVE")) {
                return false;
            }

            while (parsingText.contains(idSearchText)) {
                parsingText = parsingText.substring(parsingText.indexOf(idSearchText) + idSearchTextLength);
                String idText = parsingText.substring(0, 8);
                try {
                    Integer.valueOf(idText);
                } catch (Exception e) {
                    idText = "";
                }
                if (idText.length() < 8) {
                    continue;
                }
                parsingText = parsingText.substring(parsingText.indexOf(nameSearchText) + nameSearchTextLength);
                String nameText = parsingText.substring(0, parsingText.indexOf("<"));
                RivalData r = new RivalData();
                r.Id = idText;
                r.Name = nameText;
                rivals.add(r);
                toastString.append(r.Id).append(" / ").append(r.Name).append("\n");
            }

            mToastString = toastString.toString();

            if (rivals.isEmpty()) {
                Toast.makeText(mParent, "No Rival Data found.", Toast.LENGTH_LONG).show();
                return true;
            }

            {
                ArrayList<RivalData> savedRivals = FileReader.readRivals(mParent);
                savedRivals.addAll(rivals);
                rivals = savedRivals;
            }

            FileReader.saveRivals(mParent, rivals, false);
            if (FileReader.readActiveRival(mParent) >= rivals.size()) {
                FileReader.saveActiveRival(mParent, -1);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(() -> {
            mWebProgress.setProgress(0);

            if (mCanceled) {
                return;
            }

            if (!analyzeRivalList(src)) {
                switch (TextUtil.checkLoggedIn(src)) {
                    case 1:
                        Intent intent = new Intent();
                        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                        cancel();

                        mParent.startActivityForResult(intent, LoginRequestCode);

                        break;
                    case -1:
                        Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(mParent, mToastString, Toast.LENGTH_LONG).show();
                        break;
                }
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

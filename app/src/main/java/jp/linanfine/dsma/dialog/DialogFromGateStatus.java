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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.StatusData;

public class DialogFromGateStatus {

    public static int LoginRequestCode = 20006;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    private WebView mWebView;
    private ProgressBar mWebProgress;
    private String mRivalId;
    private String mRequestUri;

    private StatusData mStatusData;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateStatus(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mWebProgress = mView.findViewById(R.id.webProgress);
        TextView mLogView = mView.findViewById(R.id.log);

        mLogView.setText(mParent.getResources().getString(R.string.dialog_log_get_status));

        mStatusData = FileReader.readStatusData(mParent);

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

    public void setArguments(AlertDialog dialog, String rivalId) {
        mDialog = dialog;
        mRivalId = rivalId;
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
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        } else {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        }

        if (mRivalId == null) {
            mRequestUri += "playdata/index.html";
        } else {
            mRequestUri += "rival/rival_status.html?rival_id=" + mRivalId;
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

    private boolean analyzeStatus(String src) {
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();

        assert uri != null;
        if (!uri.equals(mRequestUri)) {
            return false;
        }

        try {
            Document doc = Jsoup.parse(src);
            Elements statusTable = doc.select("table#status tr");

            for (Element row : statusTable) {
                String header = row.select("th").text();
                String value = row.select("td").text();

                switch (header) {
                    case "ダンサーネーム":
                        mStatusData.DancerName = value;
                        break;
                    case "DDR-CODE":
                        mStatusData.DdrCode = value;
                        break;
                    case "所属都道府県":
                        mStatusData.Todofuken = value;
                        break;
                    case "総プレー回数":
                        mStatusData.PlayCount = Integer.parseInt(value.replace("回", "").trim());
                        break;
                    case "最終プレー日時":
                        mStatusData.LastPlay = value;
                        break;
                }
            }

            // TODO ここ以降不要。StatusDataとかsaveStatusDataメソッドからも削ること
            Elements playDataSingle = doc.select("div.diff_back#single tr");
            Elements playDataDouble = doc.select("div.diff_back#double tr");

            for (Element row : playDataSingle) {
                String header = row.select("th").text();
                String value = row.select("td").text();

                if (header.equals("プレー回数")) {
                    mStatusData.PlayCountSingle = Integer.parseInt(value.replace("回", "").trim());
                } else if (header.equals("最終プレー日時")) {
                    mStatusData.LastPlaySingle = value;
                }
            }

            for (Element row : playDataDouble) {
                String header = row.select("th").text();
                String value = row.select("td").text();

                if (header.equals("プレー回数")) {
                    mStatusData.PlayCountDouble = Integer.parseInt(value.replace("回", "").trim());
                } else if (header.equals("最終プレー日時")) {
                    mStatusData.LastPlayDouble = value;
                }
            }
            // TODO ここまで
            FileReader.saveStatusData(mParent, mStatusData);

        } catch (Exception e) {
            e.printStackTrace();
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
            if (!analyzeStatus(src)) {
                switch (TextUtil.checkLoggedIn(src)) {
                    case 1:
                        Intent intent = new Intent();
                        intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                        cancel();

                        // ログイン画面を起動。結果は onActivityResult で処理される
                        mParent.startActivityForResult(intent, LoginRequestCode);

                        break;
                    case -1:
                        Toast.makeText(mParent, mParent.getResources().getString(R.string.dialog_networkerrorexit), Toast.LENGTH_LONG).show();
                        break;
                    default:
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

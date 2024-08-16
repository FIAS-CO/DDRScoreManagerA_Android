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
import java.util.Objects;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.RecentData;
import jp.linanfine.dsma.value.WebIdToMusicIdWebTitleList;
import jp.linanfine.dsma.value._enum.PatternType;

public class DialogFromGateRecent {

    public static int LoginRequestCode = 20005;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    private WebIdToMusicIdWebTitleList mMusicIds;

    private WebView mWebView;
    private ProgressBar mWebProgress;

    private boolean mCanceled = false;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateRecent(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mMusicIds = FileReader.readWebMusicIds(mParent).toWebIdToMusicIdWebTitleList();

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
        String mRequestUri;
        if (mGateSetting.FromNewSite) {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra3/p/playdata/music_recent.html";
        } else {
            mRequestUri = "https://p.eagate.573.jp/game/ddr/ddra20/p/playdata/music_recent.html";
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

    private boolean analyzeRecent(String src) {

        String musicDetailLinkStartText = "/game/ddr/ddra3/p/playdata/music_detail.html?index=";
        int musicDetailLinkStartTextLength = musicDetailLinkStartText.length();
        String musicDetailLinkEndText = "&amp;";
        String patternTypeNumberStartText = "diff=";
        int patternTypeNumberStartTextLength = patternTypeNumberStartText.length();
        String patternTypeNumberEndText = "\" class=";

        ArrayList<RecentData> recent = new ArrayList<>();

        try {

            String parsingText = src;
            while (parsingText.contains(musicDetailLinkStartText)) {
                parsingText = parsingText.substring(parsingText.indexOf(musicDetailLinkStartText) + musicDetailLinkStartTextLength);
                String idText = parsingText.substring(0, parsingText.indexOf(musicDetailLinkEndText));
                parsingText = parsingText.substring(parsingText.indexOf(patternTypeNumberStartText) + patternTypeNumberStartTextLength);
                String patternTypeText = parsingText.substring(0, parsingText.indexOf(patternTypeNumberEndText));
                if (mMusicIds.containsKey(idText)) {
                    switch (patternTypeText) {
                        case "0": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.bSP;
                            recent.add(data);
                            break;
                        }
                        case "1": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.BSP;
                            recent.add(data);
                            break;
                        }
                        case "2": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.DSP;
                            recent.add(data);
                            break;
                        }
                        case "3": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.ESP;
                            recent.add(data);
                            break;
                        }
                        case "4": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.CSP;
                            recent.add(data);
                            break;
                        }
                        case "5": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.BDP;
                            recent.add(data);
                            break;
                        }
                        case "6": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.DDP;
                            recent.add(data);
                            break;
                        }
                        case "7": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.EDP;
                            recent.add(data);
                            break;
                        }
                        case "8": {
                            RecentData data = new RecentData();
                            data.Id = Objects.requireNonNull(mMusicIds.get(idText)).musicId;
                            data.PatternType_ = PatternType.CDP;
                            recent.add(data);
                            break;
                        }
                    }
                }
            }

            if (recent.isEmpty()) {
                return false;
            }

            FileReader.saveRecentList(mParent, recent);

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

            if (!analyzeRecent(src)) {
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
                        Toast.makeText(mParent, "Finish.", Toast.LENGTH_LONG).show();
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

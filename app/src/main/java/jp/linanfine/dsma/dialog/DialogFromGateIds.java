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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;

public class DialogFromGateIds {

    public static int LoginRequestCode = 20007;

    private final Handler mHandler = new Handler();
    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    //private TreeMap<Integer, MusicData> mMusicList;
    //private TreeMap<Integer, MusicScore> mScoreList;
    //private WebTitleToMusicIdList mMusicIds;
    private WebView mWebView;
    private ProgressBar mWebProgress;
    //private GateSetting mGateSetting;
    private boolean mDouble = false;
    private String mRivalId;
    private String mRivalName;
    private GateSetting mGateSetting;

    private int mPageCount = 0;
    private int mCurrentPage = 0;
    private TextView mLogView = null;

    private String mUriH;
    private String mUriF;

    private boolean mCanceled = false;

    private HashMap<String, String> mIdList;
    //private String mResult;

    @SuppressLint("SetJavaScriptEnabled")
    public DialogFromGateIds(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate_list, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        //mMusicList = FileReader.readMusicList(mParent);
        //mMusicIds = FileReader.readWebMusicIds(mParent).toWebTitleToMusicIdList();
        //mGateSetting = FileReader.readGateSetting(mParent);

        mWebProgress = mView.findViewById(R.id.webProgress);
        mLogView = mView.findViewById(R.id.log);
        TextView mPercent = mView.findViewById(R.id.percent);
        TextView mCurrent = mView.findViewById(R.id.current);
        TextView mMax = mView.findViewById(R.id.max);
        ProgressBar mProgress = mView.findViewById(R.id.mainProgress);

        mProgress.setVisibility(View.INVISIBLE);
        mPercent.setVisibility(View.INVISIBLE);
        mCurrent.setVisibility(View.INVISIBLE);

        mLogView.setText(mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));

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
                //try { Thread.sleep(10);} catch (InterruptedException e) {}
                mWebProgress.setProgress(1 + progress);
                //mProgress.setProgress(mCurrentPage*100+(mPageCount==0?0:progress));
                //mPercent.setText(String.valueOf((100*mCurrentPage+(mPageCount==0?0:progress))/(mPageCount==0?10000:mPageCount))+"%");
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

    public void setArguments(AlertDialog dialog, boolean getDouble, String rivalId, String rivalName) {
        mDialog = dialog;
        mRivalId = rivalId;
        mRivalName = rivalName;
        mDouble = getDouble;
        //mScoreList = FileReader.readScoreList(mParent, mRivalId);
    }

    public View getView() {
        return mView;
    }

    public void start() {
        if (mDialog == null) {
            return;
        }
        mIdList = new HashMap<>();
        //mResult = "";
        FileReader.requestAd(mView.findViewById(R.id.adContainer), mParent);
        String sd;
        if (mDouble) {
            sd = "double";
        } else {
            sd = "single";
        }

        mGateSetting = FileReader.readGateSetting(mParent);
        if (mGateSetting.FromNewSite) {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
        } else {
            mUriH = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
        }

        if (mRivalId == null) {
            mUriH += "playdata/music_data_" + sd + ".html?offset=";
            mUriF = "";
        } else {
            mUriH += "rival/rival_musicdata_" + sd + ".html?offset=";
            mUriF = "&rival_id=" + mRivalId + "&name=" + mRivalName;
        }
        //String uri = "file:///android_asset/status.html";
        mLogView.setText((mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + (mDouble ? "DP\n" : "SP\n") + mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));
        mWebView.loadUrl(mUriH + "0" + mUriF);
    }

    public void cancel() {
        mCanceled = true;
        WebView web = mView.findViewById(R.id.webView);
        web.stopLoading();
        mPageCount = 1;
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    private static int countStringInString(String target, String searchWord) {
        return (target.length() - target.replaceAll(searchWord, "").length()) / searchWord.length();
    }

    // TODO 共通化できる？
    private int getPageCount(String src) {
        String cmpStartPagerBox = "<div id=\"paging_box\">";
        String cmpEndPagerBox = "<div class=\"arrow\"";
        String cmpPangeNum = "<div class=\"page_num\"";

        int pointStartPagerBox = src.indexOf(cmpStartPagerBox);
        if (pointStartPagerBox < 0) {
            return 0;
        }
        String blockPagerBox = src.substring(pointStartPagerBox);
        blockPagerBox = blockPagerBox.substring(0, blockPagerBox.indexOf(cmpEndPagerBox));
        return countStringInString(blockPagerBox, cmpPangeNum);
    }

    private HashMap<String, String> getSongIds(String html) {
        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("tr.data");

        HashMap<String, String> musicMap = new HashMap<>();

        for (Element row : rows) {
            Element linkElement = row.select("td:first-child a").first();
            if (linkElement != null) {
                String href = linkElement.attr("href");
                String id = extractId(href);
                String musicName = linkElement.text();

                musicMap.put(id, musicName);
            }
        }

        return musicMap;
    }

    private static String extractId(String href) {
        String[] parts = href.split("index=");
        if (parts.length > 1) {
            String[] idParts = parts[1].split("&");
            return idParts[0];
        }
        return "";
    }

    private boolean urlIsCollect() {
        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();

        if (uri != null) {
            if (mRivalId == null) {
                return uri.contains("playdata/music_data");
            } else {
                return uri.contains("rival/rival_musicdata");
            }
        }

        return false;
    }

    private void analyzeScoreList(String src) {

        WebView web = mView.findViewById(R.id.webView);
        String uri = web.getUrl();

        String idDiffEnd;
        if (mRivalId == null) {
            idDiffEnd = "\"";
        } else {
            idDiffEnd = "&";
        }

        if (uri != null) {
            if (mRivalId == null) {
                if (!uri.contains("playdata/music_data")) {
                    return;
                }
            } else {
                if (!uri.contains("rival/rival_musicdata")) {
                    return;
                }
            }
        }

        String musicBlockStartText = "<tr class=\"data\">";
        int musicBlockStartTextLength = musicBlockStartText.length();
        String musicBlockEndText = "</tr>";
        int musicBlockEndTextLength = musicBlockEndText.length();
        String titleLinkText = "<a href=\"/game/ddr/ddra3/p/playdata/music_detail.html?index=";
        if (mRivalId != null) {
            titleLinkText = "<a href=\"/game/ddr/ddra3/p/rival/music_detail.html?index=";
        }
        int titleLinkTextLength = titleLinkText.length();
        String patternBlockStartText = "<td class=\"rank\" id=\"";
        int patternBlockStartTextLength = patternBlockStartText.length();
        String patternBlockEndText = "</td>";
        int patternBlockEndTextLength = patternBlockEndText.length();

        String parsingText = src;

        //StringBuilder sb = new StringBuilder();

        while (parsingText.contains(musicBlockStartText)) {
            parsingText = parsingText.substring(parsingText.indexOf(musicBlockStartText) + musicBlockStartTextLength);
            String musicBlock = parsingText.substring(0, parsingText.indexOf(musicBlockEndText));
            //Log.e("DSM", musicBlock);
            parsingText = parsingText.substring(musicBlock.length() + musicBlockEndTextLength + 1);
            musicBlock = musicBlock.substring(musicBlock.indexOf(titleLinkText) + titleLinkTextLength);
            String idText = musicBlock.substring(0, musicBlock.indexOf(idDiffEnd));
            //int musicId = Integer.valueOf(idText);
            String musicName = musicBlock.substring(musicBlock.indexOf(">") + 1);
            musicName = musicName.substring(0, musicName.indexOf("</")).trim();
            musicName = TextUtil.escapeWebTitle(musicName);

            mIdList.put(idText, musicName);
            //sb.append(musicId);
            //sb.append("\t");
            //sb.append(musicName);
            //sb.append("\n");

        }

        //mResult = mResult+sb.toString();

    }

    @android.webkit.JavascriptInterface
    public void viewSource(final String src) {
        mHandler.post(() -> {
            mWebProgress.setProgress(0);

            if (mCanceled) {
                return;
            }

            if (mPageCount == 0) {
                mPageCount = getPageCount(src);
                if (mPageCount == 0) {
                    //Toast.makeText(mParent, String.valueOf(mPageCount), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClassName("jp.linanfine.dsma", "jp.linanfine.dsma.activity.GateLogin");

                    cancel();

                    mParent.startActivityForResult(intent, LoginRequestCode);

                    return;
                }
                //mMax.setText(String.valueOf(mPageCount));
                //mProgress.setMax(mPageCount*100);
            }

            try {
//                analyzeScoreList(src);
                if (urlIsCollect()) {
                   mIdList.putAll(getSongIds(src));
                }
            } catch (Exception e) {
                return;
            }

            //FileReader.saveWebMusicIds(mParent, mMusicIds.toIdToWebMusicIdList());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            ++mCurrentPage;
            //mProgress.setProgress(mCurrentPage*100);
            //mLogView.setText((mRivalName==null?"My Score\n":("Rival: "+mRivalName+"\n"))+(mDouble?"DP\n":"SP\n")+mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPage));
            //mCurrent.setText(String.valueOf(mCurrentPage));
            //mPercent.setText(String.valueOf(100*mCurrentPage/mPageCount)+"%");
            if (mCurrentPage >= mPageCount) {

                if (!mDouble) {
                    mDouble = true;
                    String sd;
                    if (mDouble) {
                        sd = "double";
                    } else {
                        sd = "single";
                    }
                    mCurrentPage = 0;

                    mGateSetting = FileReader.readGateSetting(mParent);
                    if (mGateSetting.FromNewSite) {
                        mUriH = "https://p.eagate.573.jp/game/ddr/ddra3/p/";
                    } else {
                        mUriH = "https://p.eagate.573.jp/game/ddr/ddra20/p/";
                    }

                    if (mRivalId == null) {
                        mUriH += "playdata/music_data_" + sd + ".html?offset=";
                        mUriF = "";
                    } else {
                        mUriH += "rival/rival_musicdata_" + sd + ".html?offset=";
                        mUriF = "&rival_id=" + mRivalId + "&name=" + mRivalName;
                    }
                    //String uri = "file:///android_asset/status.html";
                    mLogView.setText((mRivalName == null ? "My Score\n" : ("Rival: " + mRivalName + "\n")) + (mDouble ? "DP\n" : "SP\n") + mParent.getResources().getString(R.string.strings____Dialog_FromGate__Dialog_FromGateList____logGetPageCount));
                    mWebView.loadUrl(mUriH + "0" + mUriF);
                    return;

                }

                Toast.makeText(mParent, "Finish.", Toast.LENGTH_LONG).show();

                StringBuilder sb = new StringBuilder();

                for (HashMap.Entry<String, String> v : mIdList.entrySet()) {
                    sb.append(v.getKey());
                    sb.append('\t');
                    sb.append(v.getValue());
                    sb.append('\n');
                }

                //テキスト入力を受け付けるビューを作成します。
                final EditText editView = mParent.getLayoutInflater().inflate(R.layout.view_multiline_edit_text, null).findViewById(R.id.editText);
                editView.setText(sb.toString());
                new AlertDialog.Builder(mParent)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle(mParent.getResources().getString(R.string.strings_global____app_name))
                        //setViewにてビューを設定します。
                        .setView(editView)
                        .setPositiveButton("OK", (dialog, whichButton) -> {

                        })
                        .show();


                (new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    mHandler.post(() -> mDialog.cancel());
                }
                )).start();
                return;
            }
            mWebView.loadUrl(mUriH + mCurrentPage + mUriF);
        });
    }

}

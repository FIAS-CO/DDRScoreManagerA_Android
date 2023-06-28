package jp.linanfine.dsma.dialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.TreeMap;

import jp.linanfine.dsma.activity.DDRSA;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.GateSetting;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.StatusData;
import jp.linanfine.dsma.value.WebMusicId;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value.adapter.MusicDataAdapter;
import jp.linanfine.dsma.value.collection.UniquePatternCollection;
import jp.linanfine.dsma.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DialogDdrSaExport {
	
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
	private String mEncryptedPassword;
	private String mPostQuery;

	private TreeMap<Integer, MusicScore> mScoreList            = null;
	private IdToWebMusicIdList mWebMusicIds;

	private boolean mCanceled = false;
	
	@SuppressLint("SetJavaScriptEnabled")
	public DialogDdrSaExport(Activity parent) {
		mParent = parent;
		mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if(intent == null)
        {
        	return;
        }
        
		mWebProgress = (ProgressBar)mView.findViewById(R.id.webProgress);
		mLogView = (TextView)mView.findViewById(R.id.log);
		
		mLogView.setText(mParent.getResources().getString(R.string.exporting));
		
        WebViewClient client = new WebViewClient() 
        {
            @Override
            public void onPageFinished(WebView view, String url) {
    			view.loadUrl("javascript:window.viewsourceactivity.viewSource(document.documentElement.outerHTML);");
            }
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            	mWebProgress.setProgress(51);
            }
    	};
    	
    	WebChromeClient chrome = new WebChromeClient() 
    	{
    		public void onProgressChanged(WebView view, int progress) {
    			mWebProgress.setProgress(51+progress/2);
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
	
	public void setArguments(AlertDialog dialog, String saUri, String ddrCode, String encryptedPassword)
	{
		mDialog = dialog;
		mSaUri = saUri;
        mDdrCode = ddrCode;
        mEncryptedPassword = encryptedPassword;
    }
	
	public View getView()
	{
		return mView;
	}

private class PostDataCreater extends AsyncTask<Void, Void, Void> {
	
	public PostDataCreater()
	{
		
	}

	@Override
	protected Void doInBackground(Void... params) 
	{
		mWebProgress.setProgress(1);
		mPostQuery = "data="+mDdrCode+"\t"+mEncryptedPassword+"\tShift_JIS\n";
		
		mScoreList = FileReader.readScoreList(mParent, null);
		mWebMusicIds = FileReader.readWebMusicIds(mParent);
		
		StringBuilder sb = new StringBuilder();
		
		int c = mScoreList.size();
		int i = 0;
		
		for (Integer id : mScoreList.keySet()) 
		{
			mWebProgress.setProgress(1+(++i)*50/c);
			WebMusicId wmi = mWebMusicIds.get(id);
			if(wmi == null)
			{
				//Toast.makeText(mParent, String.valueOf(id), Toast.LENGTH_LONG).show();
			}
			else
			{
				sb.append(TextUtil.getSaExportText(id, mScoreList));
				sb.append("\t");
				sb.append(String.valueOf(wmi.idOnWebPage));
				sb.append("\t");
				try
				{
					sb.append(URLEncoder.encode(wmi.titleOnWebPage.replace("<", "&lt;").replace(">", "&gt;").replace("♡", "&#9825;"), "shift_jis"));
				}
				catch(UnsupportedEncodingException e)
				{
					sb.append(wmi.titleOnWebPage);
				}
				sb.append("\n");
			}
		}
		
		mPostQuery = mPostQuery+sb.toString();
				
		return null;
	}

    @Override  
    protected void onPostExecute(Void result) 
    {
		FileReader.saveText(mParent, mPostQuery, "Request.txt");
		Log.e("PostQuery", mPostQuery);
        mWebView.postUrl(mSaUri+mRequestUri, mPostQuery.getBytes());
    }
    
}

	private PostDataCreater mDataCreater = new PostDataCreater();
	public void start()
	{
		if(mDialog == null)
		{
			return;
		}
		FileReader.requestAd((LinearLayout)mView.findViewById(R.id.adContainer), mParent);
		
		mDataCreater = new PostDataCreater();
		mDataCreater.execute();
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
        mHandler.post(new Runnable() {
            public void run() {
            	mWebProgress.setProgress(0);

            	if(mCanceled)
            	{
            		return;
            	}
            	
                WebView web = (WebView) mView.findViewById(R.id.webView);
            	String uri = web.getUrl();
            		//analyzeExportResult(src);

        		String dat = src;
        		if(!src.contains("<pre>") || !src.contains("</pre>"))
        		{
        			dat = "<pre>Authentication Failure</pre>";
        		}

        		String result = dat.substring(0, dat.indexOf("</pre>")).substring(dat.indexOf("<pre>")+5);
        		
            		FileReader.saveText(mParent, dat, "Result.txt");
            		
            		if(result.startsWith("Authentication Failure"))
            		{
            			Toast.makeText(mParent, mParent.getResources().getString(R.string.authentication_failure), Toast.LENGTH_LONG).show();
            		}
            		else
            		{
            			Toast.makeText(mParent, mParent.getResources().getString(R.string.export_succeed), Toast.LENGTH_LONG).show();

            			View v = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);
            			TextView tv = (TextView)v.findViewById(R.id.log);
            			tv.setHorizontallyScrolling(true);
            			tv.setHorizontalScrollBarEnabled(true);
            			tv.setEllipsize(TruncateAt.END);
            			
            			StringBuilder sb = new StringBuilder();
            			String[] lines = result.split("\n");
            			            
            			String[] fctype = new String[] {"   ", "FC ", "PFC", "MFC"};
            			boolean s = false;
            			for(String line: lines)
            			{
            				String[] data = line.split("\t"); 
            				if(s)
            				{
	            				if(data.length > 1)
	            				{
		            				sb.append(data[1]);
		            				sb.append(" \t");
		            				sb.append(data[2]);
		            				sb.append(" \t");
		            				sb.append(fctype[Integer.parseInt(data[3])]);
		            				sb.append(" \t");
		            				sb.append(data[4]);
		            				sb.append(" \n");
	            				}
	            				else
	            				{
	            					break;
	            				}
            				}
            				else if(line.startsWith("[Success]"))
            				{
                    			sb.append(mParent.getResources().getString(R.string.exported_count));
                    			sb.append(line.substring(9));
                    			sb.append("\n\n");
                    			s = true;
            				}
            			}
            			
            			tv.setText(sb.toString());
            			
    				    new AlertDialog.Builder(mParent)
				        .setIcon(android.R.drawable.ic_dialog_info)
				        //setView縺ォ縺ヲ繝薙Η繝シ繧定ィュ螳壹＠縺セ縺吶
				        .setTitle(mParent.getResources().getString(R.string.exported_scores))
				        .setView(v)
				        .setPositiveButton(mParent.getResources().getString(R.string.strings_global____ok), new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int whichButton) {
					            
				            }
				        })
				        .show();

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

package jp.linanfine.dsma.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;

public class DialogRefreshMusicList {
    private static final String TAG = "DialogRefreshMusicList";
    private static final String urlBase = "https://docs.google.com/spreadsheets/d/1HA8RH2ozKQTPvvq2BVcVoOSEMVIldRw89tFtNg8Z4V8/export?format=tsv&gid=";
    private static final String sMusicListVersionTxt = urlBase + "334969595";
    private static final String sMusicNamesTxt = urlBase + "0";
    private static final String sShockArrowExistsTxt = urlBase + "1975740187";
    private static final String sWebMusicIdsTxt = urlBase + "1376903169";

    private final Activity mParent;
    private AlertDialog mDialog;
    private final View mView;

    private ProgressBar mWebProgress;
    private String mRequestUri;

    private RefreshMusicListTask mTask;

    public DialogRefreshMusicList(Activity parent) {
        mParent = parent;
        mView = mParent.getLayoutInflater().inflate(R.layout.view_from_gate, null);

        Intent intent = mParent.getIntent();
        if (intent == null) {
            return;
        }

        mWebProgress = mView.findViewById(R.id.webProgress);
        TextView mLogView = mView.findViewById(R.id.log);

        mLogView.setText(mParent.getResources().getString(R.string.strings____Dialog_UpdateMusicList____refreshingMusiclist));

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

        mTask = new RefreshMusicListTask(this);
        mTask.execute();
    }

    private void onTaskComplete() {
        if (mParent.isFinishing() || mParent.isDestroyed()) {
            return;
        }
        cancel();
    }

    public void cancel() {
        if (mDialog != null && mDialog.isShowing()) {
            try {
                mDialog.dismiss();
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Error dismissing dialog", e);
            }
        }
        mDialog = null;

        if (mTask != null && !mTask.isCancelled()) {
            mTask.cancel(true);
        }
        mTask = null;
    }

    private class RefreshMusicListTask extends AsyncTask<Void, Integer, Void> {

        private final WeakReference<DialogRefreshMusicList> mDialogRef;

        RefreshMusicListTask(DialogRefreshMusicList dialog) {
            mDialogRef = new WeakReference<>(dialog);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            if (mWebProgress != null) {
                mWebProgress.setProgress(progress[0]);
            }
            if (progress.length > 1) {
                String message = "";
                switch (progress[1]) {
                    case 1:
                        message = mParent.getResources().getString(R.string.no_update);
                        break;
                    case 2:
                        message = mParent.getResources().getString(R.string.musiclist_updated);
                        break;
                    case 3:
                        message = mParent.getResources().getString(R.string.invalid_data);
                        break;
                    case 4:
                        message = mParent.getResources().getString(R.string.editing);
                        break;
                }
                if (!TextUtils.isEmpty(message)) {
                    Toast.makeText(mParent, message, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                String version = "";

                publishProgress(1, 0);
                {
                    mRequestUri = sMusicListVersionTxt;

                    URL url = new URL(mRequestUri);
                    URLConnection conn = url.openConnection();

                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setConnectTimeout(20000);
                    httpConn.setReadTimeout(200000);
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                    int response = httpConn.getResponseCode();

                    if (response != HttpURLConnection.HTTP_OK) {
                        return null;//throw new Exception();//HttpException();
                    }
                    int contentLength = httpConn.getContentLength();

                    InputStream in = httpConn.getInputStream();
                    DataInputStream dataInStream = new DataInputStream(in);
                    FileOutputStream outStream = mParent.openFileOutput("Version.tmp", mParent.MODE_PRIVATE);
                    DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));

                    byte[] b = new byte[128];
                    int readByte = 0, totalByte = 0;

                    while (-1 != (readByte = dataInStream.read(b))) {
                        dataOutStream.write(b, 0, readByte);
                        totalByte += readByte;
                        //publishProgress(1+(int)(totalByte*100.0f/contentLength/3.0f), totalByte, contentLength);
                    }

                    dataInStream.close();
                    dataOutStream.close();

                    version = FileReader.readText(mParent, "Version.tmp");
                    version = version.split("\n")[0].split("\t")[0];

                    if ("Editing".equals(version)) {
                        publishProgress(101, 4);
                        return null;
                    }

                    if (FileReader.readMusicListVersion(mParent).equals(version)) {
                        publishProgress(101, 1);
                        return null;
                    }
                }

                publishProgress(36, 0);
                {
                    mRequestUri = sMusicNamesTxt;

                    URL url = new URL(mRequestUri);
                    URLConnection conn = url.openConnection();

                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setConnectTimeout(20000);
                    httpConn.setReadTimeout(200000);
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                    int response = httpConn.getResponseCode();

                    if (response != HttpURLConnection.HTTP_OK) {
                        return null;//throw new Exception();//HttpException();
                    }
                    int contentLength = httpConn.getContentLength();

                    InputStream in = httpConn.getInputStream();
                    DataInputStream dataInStream = new DataInputStream(in);
                    FileOutputStream outStream = mParent.openFileOutput("MusicNames.tmp", mParent.MODE_PRIVATE);
                    DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));

                    byte[] b = new byte[128];
                    int readByte = 0, totalByte = 0;

                    while (-1 != (readByte = dataInStream.read(b))) {
                        dataOutStream.write(b, 0, readByte);
                        totalByte += readByte;
                        //publishProgress(1+(int)(totalByte*100.0f/contentLength/3.0f), totalByte, contentLength);
                    }

                    dataInStream.close();
                    dataOutStream.close();
                }

                publishProgress(51, 0);
                {
                    mRequestUri = sShockArrowExistsTxt;

                    URL url = new URL(mRequestUri);
                    URLConnection conn = url.openConnection();

                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setConnectTimeout(20000);
                    httpConn.setReadTimeout(200000);
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                    int response = httpConn.getResponseCode();

                    if (response != HttpURLConnection.HTTP_OK) {
                        return null;//throw new Exception();//HttpException();
                    }
                    int contentLength = httpConn.getContentLength();

                    InputStream in = httpConn.getInputStream();
                    DataInputStream dataInStream = new DataInputStream(in);
                    FileOutputStream outStream = mParent.openFileOutput("ShockArrowExists.tmp", mParent.MODE_PRIVATE);
                    DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));

                    byte[] b = new byte[128];
                    int readByte = 0, totalByte = 0;

                    while (-1 != (readByte = dataInStream.read(b))) {
                        dataOutStream.write(b, 0, readByte);
                        totalByte += readByte;
                        //publishProgress(34+(int)(totalByte*100.0f/contentLength/3.0f), totalByte, contentLength);
                    }

                    dataInStream.close();
                    dataOutStream.close();
                }

                publishProgress(76, 0);
                {
                    mRequestUri = sWebMusicIdsTxt;

                    URL url = new URL(mRequestUri);
                    URLConnection conn = url.openConnection();

                    HttpURLConnection httpConn = (HttpURLConnection) conn;
                    httpConn.setConnectTimeout(20000);
                    httpConn.setReadTimeout(200000);
                    httpConn.setAllowUserInteraction(false);
                    httpConn.setInstanceFollowRedirects(true);
                    httpConn.setRequestMethod("GET");
                    httpConn.connect();
                    int response = httpConn.getResponseCode();

                    if (response != HttpURLConnection.HTTP_OK) {
                        return null;//throw new Exception();//HttpException();
                    }
                    int contentLength = httpConn.getContentLength();

                    InputStream in = httpConn.getInputStream();
                    DataInputStream dataInStream = new DataInputStream(in);
                    FileOutputStream outStream = mParent.openFileOutput("WebMusicIds.tmp", mParent.MODE_PRIVATE);
                    DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(outStream));

                    byte[] b = new byte[128];
                    int readByte = 0, totalByte = 0;

                    while (-1 != (readByte = dataInStream.read(b))) {
                        dataOutStream.write(b, 0, readByte);
                        totalByte += readByte;
                        //publishProgress(67+(int)(totalByte*100.0f/contentLength/3.0f), totalByte, contentLength);
                    }

                    dataInStream.close();
                    dataOutStream.close();
                }

                String MusicNames = FileReader.readText(mParent, "MusicNames.tmp");
                String ShockArrowExists = FileReader.readText(mParent, "ShockArrowExists.tmp");
                String WebMusicIds = FileReader.readText(mParent, "WebMusicIds.tmp");
                int MusicNamesLength = MusicNames.split("\n").length;
                int ShockArrowExistsLength = ShockArrowExists.split("\n").length;
                int WebMusicIdsLength = WebMusicIds.split("\n").length;

                if (MusicNamesLength == ShockArrowExistsLength && MusicNamesLength == WebMusicIdsLength) {
                    FileReader.saveMusicListVersion(mParent, version);
                    FileReader.saveText(mParent, MusicNames, "MusicNames.txt");
                    FileReader.saveText(mParent, ShockArrowExists, "ShockArrowExists.txt");
                    FileReader.saveText(mParent, WebMusicIds, "WebMusicIds.txt");
                    publishProgress(101, 2);
                } else {
                    publishProgress(101, 3);
                }

            } catch (SocketTimeoutException e) {
                Toast.makeText(mParent, mParent.getResources().getString(R.string.timedout), Toast.LENGTH_LONG).show();
            } catch (IOException e) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            DialogRefreshMusicList dialog = mDialogRef.get();
            if (dialog != null) {
                dialog.onTaskComplete();
            }
        }
    }
}

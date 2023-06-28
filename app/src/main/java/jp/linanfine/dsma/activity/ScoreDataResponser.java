package jp.linanfine.dsma.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;

import jp.linanfine.dsma.R;
import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.WebMusicId;

public class ScoreDataResponser extends Activity {

    Intent pSummoner;

    @Override
    public void onResume()
    {
        super.onResume();
        pSummoner = getIntent();
        //Toast.makeText(this,"Processing on DDR SM",Toast.LENGTH_LONG).show();

        TreeMap<Integer, MusicScore> mScoreList = FileReader.readScoreList(this, null);
        IdToWebMusicIdList mWebMusicIds = FileReader.readWebMusicIds(this);

        StringBuilder sb = new StringBuilder();

        int c = mScoreList.size();
        int i = 0;
        int dSet = 0;

        for (Integer id : mScoreList.keySet())
        {
            //mWebProgress.setProgress(1+(++i)*50/c);
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
            if(i>=10){
                i = 0;
                pSummoner.putExtra("SCORE_DATA_"+String.valueOf(dSet), sb.toString());
                sb = new StringBuilder();
                ++dSet;
            }
            else {
                ++i;
            }
        }

        pSummoner.putExtra("SCORE_DATA_"+String.valueOf(dSet), sb.toString());
        pSummoner.putExtra("SET_COUNT", dSet);
        setResult(RESULT_OK, pSummoner);
        Toast.makeText(this,"DDR Score Manager: Score Data Exported.",Toast.LENGTH_LONG).show();
        finish();
    }

    private void Toast() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}

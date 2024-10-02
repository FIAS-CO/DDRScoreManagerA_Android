package jp.linanfine.dsma.activity;

import java.util.ArrayList;
import java.util.TreeMap;

import jp.linanfine.dsma.util.common.ActivitySetting;
import jp.linanfine.dsma.util.file.FileReader;
import jp.linanfine.dsma.value.IdToWebMusicIdList;
import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicFilter;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.RivalData;
import jp.linanfine.dsma.value.WebMusicId;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.R;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff.Mode;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class StatisticsTable extends Activity {
	
    private Handler  mHandler   = new Handler();        //Handlerのインスタンス生成
	private MgrView mMgr;
	private int mMgrWidth;
	private TreeMap<Integer, MusicData>  mMusicList            = null;
	private IdToWebMusicIdList           mWebMusicIds          = null;
	private TreeMap<Integer, MusicScore> mScoreList            = null;
	private String                       mActiveRivalId        = null;
	private String                       mActiveRivalName      = null;
	private TreeMap<Integer, MusicScore> mActiveRivalScoreList = null;
	private Spinner        mFilterSpinner     = null;
	private MusicFilter        mMusicFilter  = null;
	private int[][] mList = new int[21][21];
	private int mAxV = 0;
	private int mAxH = 0;

    private void userActionSelectFilter()
    {

    	int id = mFilterSpinner.getSelectedItemPosition();
		if(id == mFilterSpinner.getAdapter().getCount()-1)
		{
			FileReader.saveMusicFilterCount(StatisticsTable.this, id+1);
			FileReader.saveMusicFilterName(StatisticsTable.this, id, "Filter"+String.valueOf(id));
			FileReader.saveActiveMusicFilter(StatisticsTable.this, id);

	        Intent intent=new Intent();
	        intent.setClassName("jp.linanfine.dsma","jp.linanfine.dsma.activity.FilterSetting");
	        intent.putExtra("jp.linanfine.dsma.pagerid", id);
	 
	        startActivityForResult(intent, 1);
		}
		else
		{
			if(FileReader.readActiveMusicFilter(StatisticsTable.this) != id)
			{
				FileReader.saveActiveMusicFilter(StatisticsTable.this, id);
				StatisticsTable.this.initialize();
			}
		}

    }
    private void userActionOpenFilterSetting()
    {

        Intent intent=new Intent();
        intent.setClassName("jp.linanfine.dsma","jp.linanfine.dsma.activity.FilterSetting");
        intent.putExtra("jp.linanfine.dsma.pagerid", mFilterSpinner.getSelectedItemPosition());
 
        startActivityForResult(intent, 1);

    }

    public void initialize()
	{
        int activeRival = FileReader.readActiveRival(this);
        if(activeRival < 0)
        {
        	mActiveRivalScoreList = null;
        	mActiveRivalId = null;
        	mActiveRivalName = null;
        }
        else
        {
        	RivalData r = FileReader.readRivals(this).get(activeRival);
            mActiveRivalId = r.Id;
            mActiveRivalName = r.Name;
        	mActiveRivalScoreList = FileReader.readScoreList(StatisticsTable.this, mActiveRivalId);
        	//Log.e("DSM", String.valueOf(activeRival));
        }
		LinearLayout mgrw = (LinearLayout)this.findViewById(R.id.mgr);
		mgrw.removeAllViews();
		mMgr = new MgrView(this);
		mgrw.addView(mMgr);
		mWebMusicIds = FileReader.readWebMusicIds(this);
        mScoreList = FileReader.readScoreList(this, null);
        mMusicList = FileReader.readMusicList(this);
        {
        	mFilterSpinner = (Spinner)this.findViewById(R.id.filterSelect);
        	
    		mFilterSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
				public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
					userActionSelectFilter();
				}
				public void onNothingSelected(AdapterView<?> arg0) {				}});    			
        }
        this.findViewById(R.id.filterSetting).setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				userActionOpenFilterSetting();
			}
        });
        ((Spinner)this.findViewById(R.id.verticalAxis)).setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				mAxV = ((Spinner)arg0).getSelectedItemPosition();
				mvCount = getAxisPointCount(mAxV);
				StatisticsTable.this.initialize();
			}
			public void onNothingSelected(AdapterView<?> arg0) {				}});    			
        ((Spinner)this.findViewById(R.id.horizontalAxis)).setOnItemSelectedListener(new OnItemSelectedListener(){
			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
				mAxH = ((Spinner)arg0).getSelectedItemPosition();
				mhCount = getAxisPointCount(mAxH);
				StatisticsTable.this.initialize();
			}
			public void onNothingSelected(AdapterView<?> arg0) {				}});    			
		{
			int count = FileReader.readMusicFilterCount(this);
			ArrayList<String> items = new ArrayList<String>();
	    	for(int i = 0; i < count; i++)
	    	{
	    		items.add(FileReader.readMusicFilterName(this, i));
	    	}
	    	items.add(getResources().getString(R.string.filter_new));
	
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mFilterSpinner.setAdapter(adapter);
		}
        int activeFilter = FileReader.readActiveMusicFilter(this);
        mMusicFilter = FileReader.readMusicFilter(this, activeFilter);
        mFilterSpinner.setSelection(activeFilter);
        MusicScore zeroScore = new MusicScore();
        for(int y = 0; y < 21; ++y)
        {
        	for(int x = 0; x < 21; ++x)
        	{
        		mList[y][x] = 0;
        	}
        }
        mTempFilterH = new MusicFilter();
        mTempFilterV = new MusicFilter();
        for(int musicid: mMusicList.keySet())
        {
        	MusicData md = mMusicList.get(musicid);
        	MusicScore ms = mScoreList.get(musicid);
        	MusicScore rs = null;
        	if(ms == null)
        	{
        		ms = zeroScore;
        	}
        	if(activeRival >= 0)
        	{
	        	rs = mActiveRivalScoreList.get(musicid);
	        	if(rs == null)
	        	{
	        		rs = zeroScore;
	        	}
        	}
        	WebMusicId webId = mWebMusicIds.get(musicid);
        	if(mMusicFilter.checkFilter(webId, md, PatternType.bSP, ms, rs))
        	{
        		checkPatterns(md, PatternType.bSP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.BSP, ms, rs))
        	{
        		checkPatterns(md, PatternType.BSP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.DSP, ms, rs))
        	{
        		checkPatterns(md, PatternType.DSP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.ESP, ms, rs))
        	{
        		checkPatterns(md, PatternType.ESP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.CSP, ms, rs))
        	{
        		checkPatterns(md, PatternType.CSP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.BDP, ms, rs))
        	{
        		checkPatterns(md, PatternType.BDP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.DDP, ms, rs))
        	{
        		checkPatterns(md, PatternType.DDP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.EDP, ms, rs))
        	{
        		checkPatterns(md, PatternType.EDP, ms, rs);
        	}
        	if(mMusicFilter.checkFilter(webId, md, PatternType.CDP, ms, rs))
        	{
        		checkPatterns(md, PatternType.CDP, ms, rs);
        	}
        }
	}
    
    private int getAxisPointCount(int ax)
    {
    	switch(ax)
		{
			case 0:
				return 20;
			case 1:
				return 9;
			case 2:
				return 15;
			case 3:
				return 5;
			case 4:
				return 8;
			case 5:
				return 11;
			case 6:
				return 20;
			case 7:
				return 20;
			default:
				return 20;
		}
    }
    
    private MusicFilter mTempFilterH = null;
    private MusicFilter mTempFilterV = null;
    
    private void checkPatterns(MusicData md, PatternType pt, MusicScore ms, MusicScore rs)
    {
    	for(int y = 0; y < 21; ++y)
    	{
    		if(!checkPattern(md, pt, ms, rs, mTempFilterV, mAxV, y))
    		{
    			continue;
    		}
    		for(int x = 0; x < 21; ++x)
    		{
    			if(checkPattern(md, pt, ms, rs, mTempFilterH, mAxH, x))
    			{
    				++mList[y][x];
    			}
    		}
    	}
    }
    
    private boolean checkPattern(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int ax, int count)
    {
    	switch(ax)
		{
			case 0:
				return checkPatternDifficulty(md, pt, ms, rs, tempfilter, count);
			case 1:
				return checkPatternPatternType(md, pt, ms, rs, tempfilter, count);
			case 2:
				return checkPatternSeriesTitle(md, pt, ms, rs, tempfilter, count);
			case 3:
				return checkPatternFcType(md, pt, ms, rs, tempfilter, count);
			case 4:
				return checkPatternDanceLevel(md, pt, ms, rs, tempfilter, count);
			case 5:
				return checkPatternScoresC(md, pt, ms, rs, tempfilter, count);
			case 6:
				return checkPatternScoresB(md, pt, ms, rs, tempfilter, count);
			case 7:
				return checkPatternScoresA(md, pt, ms, rs, tempfilter, count);
			default:
				return false;
		}
    }
    
    private boolean checkPatternDifficulty(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	++count;
    	tempfilter.AllowExpertWhenNoChallenge = false;
    	tempfilter.AllowOnlyChallenge = false;
    	tempfilter.Dif1 = false;
    	tempfilter.Dif2 = false;
    	tempfilter.Dif3 = false;
    	tempfilter.Dif4 = false;
    	tempfilter.Dif5 = false;
    	tempfilter.Dif6 = false;
    	tempfilter.Dif7 = false;
    	tempfilter.Dif8 = false;
    	tempfilter.Dif9 = false;
    	tempfilter.Dif10 = false;
    	tempfilter.Dif11 = false;
    	tempfilter.Dif12 = false;
    	tempfilter.Dif13 = false;
    	tempfilter.Dif14 = false;
    	tempfilter.Dif15 = false;
    	tempfilter.Dif16 = false;
    	tempfilter.Dif17 = false;
    	tempfilter.Dif18 = false;
    	tempfilter.Dif19 = false;
    	switch(count)
    	{
    		case 1: tempfilter.Dif1 = true; break;
    		case 2: tempfilter.Dif2 = true; break;
    		case 3: tempfilter.Dif3 = true; break;
    		case 4: tempfilter.Dif4 = true; break;
    		case 5: tempfilter.Dif5 = true; break;
    		case 6: tempfilter.Dif6 = true; break;
    		case 7: tempfilter.Dif7 = true; break;
    		case 8: tempfilter.Dif8 = true; break;
    		case 9: tempfilter.Dif9 = true; break;
    		case 10: tempfilter.Dif10 = true; break;
    		case 11: tempfilter.Dif11 = true; break;
    		case 12: tempfilter.Dif12 = true; break;
    		case 13: tempfilter.Dif13 = true; break;
    		case 14: tempfilter.Dif14 = true; break;
    		case 15: tempfilter.Dif15 = true; break;
    		case 16: tempfilter.Dif16 = true; break;
    		case 17: tempfilter.Dif17 = true; break;
    		case 18: tempfilter.Dif18 = true; break;
    		case 19: tempfilter.Dif19 = true; break;
    		case 20: break;
    		case 21: return true;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternPatternType(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	tempfilter.AllowExpertWhenNoChallenge = false;
    	tempfilter.AllowOnlyChallenge = false;
    	tempfilter.bSP = false;
    	tempfilter.BSP = false;
    	tempfilter.DSP = false;
    	tempfilter.ESP = false;
    	tempfilter.CSP = false;
    	tempfilter.BDP = false;
    	tempfilter.DDP = false;
    	tempfilter.EDP = false;
    	tempfilter.CDP = false;
    	switch(count)
    	{
    		case 0: tempfilter.bSP = true; break;
			case 1: tempfilter.BSP = true; break;
    		case 2: tempfilter.DSP = true; break;
    		case 3: tempfilter.ESP = true; break;
    		case 4: tempfilter.CSP = true; break;
    		case 5: tempfilter.BDP = true; break;
    		case 6: tempfilter.DDP = true; break;
    		case 7: tempfilter.EDP = true; break;
    		case 8: tempfilter.CDP = true; break;
    		case 9: 
    			return true;
    		case 10: 
    		case 11:
    		case 12:
    		case 13:
    		case 14:
    		case 15:
    		case 16:
    		case 17:
    		case 18:
    		case 19:
    		case 20:
    			return false;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternSeriesTitle(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	tempfilter.AllowExpertWhenNoChallenge = false;
    	tempfilter.AllowOnlyChallenge = false;
    	tempfilter.Ser1st = false;
    	tempfilter.Ser2nd = false;
    	tempfilter.Ser3rd = false;
    	tempfilter.Ser4th = false;
    	tempfilter.Ser5th = false;
    	tempfilter.SerEXTREME = false;
    	tempfilter.SerMAX = false;
    	tempfilter.SerMAX2 = false;
    	tempfilter.SerSuperNova = false;
    	tempfilter.SerSuperNova2 = false;
    	tempfilter.SerX = false;
    	tempfilter.SerX2 = false;
    	tempfilter.SerX3 = false;
    	tempfilter.SerX3vs2ndMIX = false;
    	tempfilter.Ser2013 = false;
       	tempfilter.Ser2014 = false;
       	tempfilter.SerA = false;
		tempfilter.SerA20 = false;
		tempfilter.SerA3 = false;
           	switch(count)
    	{
    		case 0: tempfilter.Ser1st = true; break;
			case 1: tempfilter.Ser2nd = true; break;
    		case 2: tempfilter.Ser3rd = true; break;
    		case 3: tempfilter.Ser4th = true; break;
    		case 4: tempfilter.Ser5th = true; break;
    		case 5: tempfilter.SerEXTREME = true; break;
    		case 6: tempfilter.SerMAX = true; break;
    		case 7: tempfilter.SerMAX2 = true; break;
    		case 8: tempfilter.SerSuperNova = true; break;
    		case 9: tempfilter.SerSuperNova2 = true; break;
    		case 10: tempfilter.SerX = true; break;
    		case 11: tempfilter.SerX2 = true; break;
    		case 12: tempfilter.SerX3 = true; break;
    		case 13: tempfilter.Ser2013 = true; break;
    		case 14: tempfilter.Ser2014 = true; break;
    		case 15: tempfilter.SerA = true; break;
			case 16: tempfilter.SerA20 = true; break;
			case 17: tempfilter.SerA3 = true; break;
    		case 18:
    			return true;
    		case 19:
    		case 20:
    			return false;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternFcType(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	tempfilter.AllowExpertWhenNoChallenge = false;
    	tempfilter.AllowOnlyChallenge = false;
    	tempfilter.FcMFC = false;
    	tempfilter.FcPFC = false;
    	tempfilter.FcFC = false;
    	tempfilter.FcGFC = false;
    	tempfilter.FcNoFC = false;
    	switch(count)
    	{
    		case 0: tempfilter.FcMFC = true; break;
			case 1: tempfilter.FcPFC = true; break;
    		case 2: tempfilter.FcFC = true; break;
    		case 3: tempfilter.FcGFC = true; break;
    		case 4: tempfilter.FcNoFC = true; break;
    		case 5: 
    			return true;
    		case 6: 
    		case 7: 
    		case 8: 
    		case 9: 
    		case 10: 
    		case 11: 
    		case 12: 
    		case 13: 
    		case 14: 
    		case 15: 
    		case 16: 
    		case 17:
    		case 18:
    		case 19:
    		case 20:
    			return false;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternDanceLevel(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	tempfilter.AllowExpertWhenNoChallenge = false;
    	tempfilter.AllowOnlyChallenge = false;
    	tempfilter.RankAAA = false;
    	tempfilter.RankAAp = false;
    	tempfilter.RankAA = false;
    	tempfilter.RankAAm = false;
    	tempfilter.RankAp = false;
    	tempfilter.RankA = false;
    	tempfilter.RankAm = false;
    	tempfilter.RankBp = false;
    	tempfilter.RankB = false;
    	tempfilter.RankBm = false;
    	tempfilter.RankCp = false;
    	tempfilter.RankC = false;
    	tempfilter.RankCm = false;
    	tempfilter.RankDp = false;
    	tempfilter.RankD = false;
    	tempfilter.RankE = false;
    	tempfilter.RankNoPlay = false;
    	switch(count)
    	{
    		case 0: tempfilter.RankAAA = true; break;
			case 1: tempfilter.RankAA = true; break;
			case 2: tempfilter.RankAA = true; break;
			case 3: tempfilter.RankAA = true; break;
    		case 4: tempfilter.RankA = true; break;
    		case 5: tempfilter.RankA = true; break;
    		case 6: tempfilter.RankA = true; break;
    		case 7: tempfilter.RankB = true; break;
    		case 8: tempfilter.RankB = true; break;
    		case 9: tempfilter.RankB = true; break;
    		case 10: tempfilter.RankC = true; break;
    		case 11: tempfilter.RankC = true; break;
    		case 12: tempfilter.RankC = true; break;
    		case 13: tempfilter.RankD = true; break;
    		case 14: tempfilter.RankD = true; break;
    		case 15: tempfilter.RankE = true; break;
    		case 16: tempfilter.RankNoPlay = true; break;
    		case 17: 
    			return true;
    		case 18:
    		case 19:
    		case 20:
    			return false;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternScoresC(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	switch(count)
    	{
    		case 0: tempfilter.ScoreMin = 1000000; tempfilter.ScoreMax = 1000000; break;
    		case 1: tempfilter.ScoreMin = 900000; tempfilter.ScoreMax = 999999; break;
			case 2: tempfilter.ScoreMin = 800000; tempfilter.ScoreMax = 899999; break;
    		case 3: tempfilter.ScoreMin = 700000; tempfilter.ScoreMax = 799999; break;
    		case 4: tempfilter.ScoreMin = 600000; tempfilter.ScoreMax = 699999; break;
    		case 5: tempfilter.ScoreMin = 500000; tempfilter.ScoreMax = 599999; break;
    		case 6: tempfilter.ScoreMin = 400000; tempfilter.ScoreMax = 499999; break;
    		case 7: tempfilter.ScoreMin = 300000; tempfilter.ScoreMax = 399999; break;
    		case 8: tempfilter.ScoreMin = 200000; tempfilter.ScoreMax = 299999; break;
    		case 9: tempfilter.ScoreMin = 100000; tempfilter.ScoreMax = 199999; break;
    		case 10: tempfilter.ScoreMin = 0; tempfilter.ScoreMax = 99999; break;
    		case 11: 
    			return true;
    		case 12: 
    		case 13: 
    		case 14: 
    		case 15: 
    		case 16: 
    		case 17:
    		case 18:
    		case 19:
    		case 20:
    			return false;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternScoresB(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	switch(count)
    	{
			case 0: tempfilter.ScoreMin = 1000000; tempfilter.ScoreMax = 1000000; break;
    		case 1: tempfilter.ScoreMin = 990000; tempfilter.ScoreMax = 999999; break;
			case 2: tempfilter.ScoreMin = 980000; tempfilter.ScoreMax = 989999; break;
    		case 3: tempfilter.ScoreMin = 970000; tempfilter.ScoreMax = 979999; break;
    		case 4: tempfilter.ScoreMin = 960000; tempfilter.ScoreMax = 969999; break;
    		case 5: tempfilter.ScoreMin = 950000; tempfilter.ScoreMax = 959999; break;
    		case 6: tempfilter.ScoreMin = 940000; tempfilter.ScoreMax = 949999; break;
    		case 7: tempfilter.ScoreMin = 930000; tempfilter.ScoreMax = 939999; break;
    		case 8: tempfilter.ScoreMin = 920000; tempfilter.ScoreMax = 929999; break;
    		case 9: tempfilter.ScoreMin = 910000; tempfilter.ScoreMax = 919999; break;
    		case 10: tempfilter.ScoreMin = 900000; tempfilter.ScoreMax = 909999; break;
    		case 11: tempfilter.ScoreMin = 890000; tempfilter.ScoreMax = 899999; break;
    		case 12: tempfilter.ScoreMin = 880000; tempfilter.ScoreMax = 889999; break;
    		case 13: tempfilter.ScoreMin = 870000; tempfilter.ScoreMax = 879999; break;
    		case 14: tempfilter.ScoreMin = 860000; tempfilter.ScoreMax = 869999; break;
    		case 15: tempfilter.ScoreMin = 850000; tempfilter.ScoreMax = 859999; break;
    		case 16: tempfilter.ScoreMin = 840000; tempfilter.ScoreMax = 849999; break;
    		case 17: tempfilter.ScoreMin = 830000; tempfilter.ScoreMax = 839999; break;
    		case 18: tempfilter.ScoreMin = 820000; tempfilter.ScoreMax = 829999; break;
    		case 19: tempfilter.ScoreMin = 810000; tempfilter.ScoreMax = 819999; break;
    		case 20: 
    			return true;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))

    	{
    		return true;
    	}
    	return false;
    }

    private boolean checkPatternScoresA(MusicData md, PatternType pt, MusicScore ms, MusicScore rs, MusicFilter tempfilter, int count)
    {
    	switch(count)
    	{
			case 0: tempfilter.ScoreMin = 1000000; tempfilter.ScoreMax = 1000000; break;
			case 1: tempfilter.ScoreMin = 999990; tempfilter.ScoreMax = 999999; break;
			case 2: tempfilter.ScoreMin = 999980; tempfilter.ScoreMax = 999989; break;
			case 3: tempfilter.ScoreMin = 999970; tempfilter.ScoreMax = 999979; break;
			case 4: tempfilter.ScoreMin = 999960; tempfilter.ScoreMax = 999969; break;
			case 5: tempfilter.ScoreMin = 999950; tempfilter.ScoreMax = 999959; break;
			case 6: tempfilter.ScoreMin = 999940; tempfilter.ScoreMax = 999949; break;
			case 7: tempfilter.ScoreMin = 999930; tempfilter.ScoreMax = 999939; break;
			case 8: tempfilter.ScoreMin = 999920; tempfilter.ScoreMax = 999929; break;
			case 9: tempfilter.ScoreMin = 999910; tempfilter.ScoreMax = 999919; break;
			case 10: tempfilter.ScoreMin = 999900; tempfilter.ScoreMax = 999909; break;
			case 11: tempfilter.ScoreMin = 999890; tempfilter.ScoreMax = 999899; break;
			case 12: tempfilter.ScoreMin = 999880; tempfilter.ScoreMax = 999889; break;
			case 13: tempfilter.ScoreMin = 999870; tempfilter.ScoreMax = 999879; break;
			case 14: tempfilter.ScoreMin = 999860; tempfilter.ScoreMax = 999869; break;
			case 15: tempfilter.ScoreMin = 999850; tempfilter.ScoreMax = 999859; break;
			case 16: tempfilter.ScoreMin = 999840; tempfilter.ScoreMax = 999849; break;
			case 17: tempfilter.ScoreMin = 999830; tempfilter.ScoreMax = 999839; break;
			case 18: tempfilter.ScoreMin = 999820; tempfilter.ScoreMax = 999829; break;
			case 19: tempfilter.ScoreMin = 999810; tempfilter.ScoreMax = 999819; break;
			case 20: 
				return true;
    	}
    	tempfilter.Deleted = false;
    	if(tempfilter.checkFilter(null, md, pt, ms, rs))
    	{
    		return true;
    	}
    	return false;
    }
    
	private int mhCount;
	private int mvCount;

	public class MgrView extends View {

    	public MgrView(Context context) 
    	{
    		super(context);
    		//this.setBackgroundColor(0xff000000);
    	}
    	
    	protected void onMeasure(int widthMeasureSpec,int heightMeasureSpec){
    	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	    mMgrWidth = MeasureSpec.getSize(widthMeasureSpec);
    	    setMeasuredDimension(mMgrWidth, mMgrWidth);
    	    //mW = mMgrWidth / 22.0f;
    	    //mH = mW;
    	}
    	
    	private float mW;
    	private float mH;
    	
    	private Paint mPaint = new Paint();
    	private Rect mRect = new Rect();
    	private Path mClip = new Path();
    	private Path mSingle = new Path();
    	private Path mDouble = new Path();
    	
    	@Override
    	public void onDraw(Canvas canvas) 
    	{
    		canvas.drawColor(0, Mode.CLEAR);
    		mPaint.setAntiAlias(true);
	    	mPaint.setColor(0xff000000);
	    	mPaint.setStyle(Paint.Style.FILL);
	    	mRect = canvas.getClipBounds();
	    	canvas.drawRect(mRect, mPaint);
	    	mW = mMgrWidth / (mhCount+2.0f);
    	    mH = mMgrWidth / 22;//mMgrWidth / (mvCount+2.0f);
	    	for(int i = 0; i < mhCount+2; i += 2)
	    	{
	    		mPaint.setColor(0x220000ff);
	    		canvas.drawRect(i*mW, 0, i*mW+mW, mH*(mvCount+2), mPaint);
	    	}
	    	for(int i = 0; i < mvCount+2; i += 2)
	    	{
		    	mPaint.setColor(0x22ff0000);
	    		canvas.drawRect(0, i*mH, mMgrWidth, i*mH+mH, mPaint);
	    	}
	    	mPaint.setColor(0xff999999);
	    	mPaint.setStrokeWidth(mH/20.0f);
	    	canvas.drawLine(0, mH, mMgrWidth, mH, mPaint);
	    	canvas.drawLine(mW, 0, mW, mH*(mvCount+2), mPaint);
	    	mPaint.setColor(0xffffffff);
	    	mPaint.setTextAlign(Align.RIGHT);
	    	mPaint.setTextSize(Math.min(mW/2.5f, mH/1.1f));
	    	mPaint.setStrokeWidth(mW/20.0f);
	    	FontMetrics fontMetrics = mPaint.getFontMetrics();
    		for(int x = 0; x < mhCount+1; ++x)
    		{
    			canvas.drawText(getAxString(1, x), (x+1+1)*mW-1, (0+1)*mH-fontMetrics.bottom, mPaint);
    		}
	    	for(int y = 0; y < mvCount+1; ++y)
	    	{
	    		canvas.drawText(getAxString(0, y), (0+1)*mW-1, (y+1+1)*mH-fontMetrics.bottom, mPaint); 
	    		for(int x = 0; x < mhCount+1; ++x)
	    		{
	    			canvas.drawText(String.valueOf(mList[y][x]), (x+1+1)*mW-1, (y+1+1)*mH-fontMetrics.bottom, mPaint);
	    		}
	    	}
    	}

	}
    
    private String getAxString(int xy, int count)
    {
    	String str = "";
    	int ax = xy==0?mAxV:mAxH;
		switch(ax)
		{
			case 0: 
				if(count == 20)
				{
					str = "Ttl";
				}
				else
				{
    				str = String.valueOf(count+1);
				}
				break;
			case 1: 
				switch(count)
				{
					case 0: str = "bSP"; break;
					case 1: str = "BSP"; break;
					case 2: str = "DSP"; break;
					case 3: str = "ESP"; break;
					case 4: str = "CSP"; break;
					case 5: str = "BDP"; break;
					case 6: str = "DDP"; break;
					case 7: str = "EDP"; break;
					case 8: str = "CDP"; break;
					case 9: str = "Ttl"; break;
				}
				break;
			case 2:
				switch(count)
				{
					case 0: str = "1st"; break;
					case 1: str = "2nd"; break;
					case 2: str = "3rd"; break;
					case 3: str = "4th"; break;
					case 4: str = "5th"; break;
					case 5: str = "MAX"; break;
					case 6: str = "MA2"; break;
					case 7: str = "EXR"; break;
					case 8: str = "SNV"; break;
					case 9: str = "SN2"; break;
					case 10: str = "X"; break;
					case 11: str = "X2"; break;
					case 12: str = "X3"; break;
					case 13: str = "2x3"; break;
					case 14: str = "2x4"; break;
					case 15: str = "A"; break;
					case 16: str = "A20"; break;
					case 17: str = "A3"; break;
					case 18: str = "Ttl"; break;
				}
				break;
			case 3: 
				switch(count)
				{
					case 0: str = "MFC"; break;
					case 1: str = "PFC"; break;
					case 2: str = "FC"; break;
					case 3: str = "GFC"; break;
					case 4: str = "NFC"; break;
					case 5: str = "Ttl"; break;
				}
				break;
			case 4: 
				switch(count)
				{
					case 0: str = "AAA"; break;
					case 1: str = "AA+"; break;
					case 2: str = "AA"; break;
					case 3: str = "AA-"; break;
					case 4: str = "A+"; break;
					case 5: str = "A"; break;
					case 6: str = "A-"; break;
					case 7: str = "B+"; break;
					case 8: str = "B"; break;
					case 9: str = "B-"; break;
					case 10: str = "C+"; break;
					case 11: str = "C"; break;
					case 12: str = "C-"; break;
					case 13: str = "D+"; break;
					case 14: str = "D"; break;
					case 15: str = "E"; break;
					case 16: str = "NP"; break;
					case 17: str = "Ttl"; break;
				}
				break;
			case 5: 
				switch(count)
				{
					case 0: str = "MFC"; break;
					case 1: str = "900,"; break;
					case 2: str = "800,"; break;
					case 3: str = "700,"; break;
					case 4: str = "600,"; break;
					case 5: str = "500,"; break;
					case 6: str = "400,"; break;
					case 7: str = "300,"; break;
					case 8: str = "200,"; break;
					case 9: str = "100,"; break;
					case 10: str = "000,"; break;
					case 11: str = "Ttl"; break;
				}
				break;
			case 6: 
				switch(count)
				{
					case 0: str = "MFC"; break;
					case 1: str = "990,"; break;
					case 2: str = "980,"; break;
					case 3: str = "970,"; break;
					case 4: str = "960,"; break;
					case 5: str = "950,"; break;
					case 6: str = "940,"; break;
					case 7: str = "930,"; break;
					case 8: str = "920,"; break;
					case 9: str = "910,"; break;
					case 10: str = "900,"; break;
					case 11: str = "890,"; break;
					case 12: str = "880,"; break;
					case 13: str = "870,"; break;
					case 14: str = "860,"; break;
					case 15: str = "850,"; break;
					case 16: str = "840,"; break;
					case 17: str = "830,"; break;
					case 18: str = "820,"; break;
					case 19: str = "810,"; break;
					case 20: str = "Ttl"; break;
				}
				break;
			case 7: 
				switch(count)
				{
					case 0: str = "MFC"; break;
					case 1: str = ",990"; break;
					case 2: str = ",980"; break;
					case 3: str = ",970"; break;
					case 4: str = ",960"; break;
					case 5: str = ",950"; break;
					case 6: str = ",940"; break;
					case 7: str = ",930"; break;
					case 8: str = ",920"; break;
					case 9: str = ",910"; break;
					case 10: str = ",900"; break;
					case 11: str = ",890"; break;
					case 12: str = ",880"; break;
					case 13: str = ",870"; break;
					case 14: str = ",860"; break;
					case 15: str = ",850"; break;
					case 16: str = ",840"; break;
					case 17: str = ",830"; break;
					case 18: str = ",820"; break;
					case 19: str = ",810"; break;
					case 20: str = "Ttl"; break;
				}
				break;
		}
		return str;
    }
    
	@Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) 
   {
   	if(requestCode == 1)
   	{
   		initialize();
   		return;
   	}
   }

	  @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
  	if(keyCode == KeyEvent.KEYCODE_MENU){
  		
  	}
	    return super.onKeyDown(keyCode, event);
	  }

	@Override
	public void onResume()
	{
        super.onResume();
        ActivitySetting.setTitleBarShown(this, this.findViewById(R.id.titleBar));
		FileReader.requestAd((LinearLayout)this.findViewById(R.id.adContainer), this);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mainView = this.getLayoutInflater().inflate(R.layout.activity_statistics_table, null);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivitySetting.setFullScreen(this);
        this.setContentView(mainView);
        
        initialize();
    }
}

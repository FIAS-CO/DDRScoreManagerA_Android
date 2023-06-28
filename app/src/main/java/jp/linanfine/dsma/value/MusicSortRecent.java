package jp.linanfine.dsma.value;

import java.util.ArrayList;

import jp.linanfine.dsma.value._enum.MusicSortType;

public class MusicSortRecent extends MusicSort {
	
	ArrayList<RecentData> mRecent;

	public MusicSortRecent(MusicSort base, ArrayList<RecentData> recent)
	{
		mRecent = recent;
    	_5thType = base._4thType;
    	_5thOrder = base._4thOrder;
    	_4thType = base._3rdType;
    	_4thOrder = base._3rdOrder;
    	_3rdType = base._2ndType;
    	_3rdOrder = base._2ndOrder;
    	_2ndType = base._1stType;
    	_2ndOrder = base._1stOrder;
    	_1stType = MusicSortType.Recent;
	}
	
	@Override
	protected int compare(UniquePattern p, UniquePattern m, int section)
	{
		MusicSortType type;
		if(section == 1)
		{
			type = _1stType;
		}
		else if(section == 2)
		{
			type = _2ndType;
		}
		else if(section == 3){
			type = _3rdType;
		}
		else if(section == 4){
			type = _4thType;
		}
		else if(section == 5){
			type = _5thType;
		}
		else
		{
			return 0;
		}
		if(type != MusicSortType.Recent)
		{
			return super.compare(p, m, section);
		}
		int pi = -1;
		for(int i = 0; i < mRecent.size(); ++i)
		{
			if(mRecent.get(i).Id == p.MusicId)
			{
				pi = i;
				break;
			}
		}
		int mi = -1;
		for(int i = 0; i < mRecent.size(); ++i)
		{
			if(mRecent.get(i).Id == m.MusicId)
			{
				mi = i;
				break;
			}
		}
		if(pi < 0 || mi < 0)
		{
			return 0;
		}
		return pi-mi;
	}
}

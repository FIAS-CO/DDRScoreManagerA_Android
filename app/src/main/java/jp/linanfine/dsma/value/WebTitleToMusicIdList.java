package jp.linanfine.dsma.value;

import java.util.TreeMap;

public class WebTitleToMusicIdList extends TreeMap<String, MusicId> {
	public IdToWebMusicIdList toIdToWebMusicIdList()
	{
		IdToWebMusicIdList ret = new IdToWebMusicIdList();
		for (String musicName : this.keySet()) 
		{
			MusicId mi = this.get(musicName);
			WebMusicId wmi = new WebMusicId();
			wmi.idOnWebPage = mi.idOnWebPage;
			wmi.titleOnWebPage = musicName;
			wmi.isDeleted = mi.isDeleted;
			//if(ret.containsKey(mi.musicId) && ret.get(mi.musicId).idOnWebPage == mi.idOnWebPage)
			//{
			//	ret.put(mi.musicId, wmi);
			//}
			//else
			{
				ret.put(mi.musicId, wmi);
			}
		}
		return ret;
	}
}

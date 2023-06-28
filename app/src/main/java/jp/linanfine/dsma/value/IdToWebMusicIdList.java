package jp.linanfine.dsma.value;

import java.util.TreeMap;

public class IdToWebMusicIdList extends TreeMap<Integer, WebMusicId> {
	public WebTitleToMusicIdList toWebTitleToMusicIdList()
	{
		WebTitleToMusicIdList ret = new WebTitleToMusicIdList();
		for (Integer id : this.keySet()) 
		{
			WebMusicId wmi = this.get(id);
			MusicId mi = new MusicId();
			mi.idOnWebPage = wmi.idOnWebPage;
			mi.musicId = id;
			ret.put(wmi.titleOnWebPage, mi);
		}
		return ret;
	}
	public WebIdToMusicIdWebTitleList toWebIdToMusicIdWebTitleList()
	{
		WebIdToMusicIdWebTitleList ret = new WebIdToMusicIdWebTitleList();
		for (Integer id : this.keySet()) 
		{
			WebMusicId wmi = this.get(id);
			MusicIdWebTitle miwt = new MusicIdWebTitle();
			miwt.musicId = id;
			miwt.titleOnWebPage = wmi.titleOnWebPage;
			ret.put(wmi.idOnWebPage, miwt);
		}
		return ret;
	}
}

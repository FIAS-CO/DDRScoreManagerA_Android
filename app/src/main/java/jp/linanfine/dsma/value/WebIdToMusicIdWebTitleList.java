package jp.linanfine.dsma.value;

import java.util.TreeMap;

public class WebIdToMusicIdWebTitleList extends TreeMap<String, MusicIdWebTitle> {
	public IdToWebMusicIdList toIdToWebMusicIdList()
	{
		IdToWebMusicIdList ret = new IdToWebMusicIdList();
		for (String id : this.keySet()) 
		{
			MusicIdWebTitle miwt = this.get(id);
			WebMusicId wmi = new WebMusicId();
			wmi.idOnWebPage = id;
			wmi.titleOnWebPage = miwt.titleOnWebPage;
			wmi.isDeleted = miwt.isDeleted;
			//if(ret.containsKey(miwt.musicId))
			//{
			//	int mii = miwt.musicId;
			//	do
			//	{
			//		++mii;
			//	} while(ret.containsKey(mii));
			//	ret.put(mii, wmi);
			//}
			//else
			{
				ret.put(miwt.musicId, wmi);
			}
		}
		return ret;
	}
}

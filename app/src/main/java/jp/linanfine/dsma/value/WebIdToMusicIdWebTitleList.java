package jp.linanfine.dsma.value;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WebIdToMusicIdWebTitleList extends TreeMap<String, MusicIdWebTitle> {
    public IdToWebMusicIdList toIdToWebMusicIdList() {
        IdToWebMusicIdList ret = new IdToWebMusicIdList();
        for (String id : this.keySet()) {
            MusicIdWebTitle miwt = this.get(id);
            WebMusicId wmi = new WebMusicId();
            wmi.idOnWebPage = id;
            wmi.titleOnWebPage = miwt.titleOnWebPage;
            wmi.isDeleted = miwt.isDeleted;

            ret.put(miwt.musicId, wmi);
        }
        return ret;
    }

    public Map<String, Integer> getWebIdToMusicIdMap() {
        Map<String, Integer> webIdToMusicIdMap = new HashMap<>();
        for (String webId : this.keySet()) {
            MusicIdWebTitle musicIdWebTitle = this.get(webId);
            int musicId = musicIdWebTitle != null ? musicIdWebTitle.musicId : -1;
            webIdToMusicIdMap.put(webId, musicId);
        }
        return webIdToMusicIdMap;
    }
}

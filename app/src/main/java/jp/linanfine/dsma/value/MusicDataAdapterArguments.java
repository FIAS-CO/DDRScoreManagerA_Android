package jp.linanfine.dsma.value;

import java.util.TreeMap;


public class MusicDataAdapterArguments {
	public boolean UseAsyncDraw = true;
	public IdToWebMusicIdList WebMusicIds;
	public TreeMap<Integer, MusicData> Musics;
	public TreeMap<Integer, MusicScore> Scores;
	public String RivalName;
	public TreeMap<Integer, MusicScore> RivalScores;
	public TreeMap<Integer, String[]> Comments;
	public MusicFilter Filter;
	public MusicSort Sort;
}

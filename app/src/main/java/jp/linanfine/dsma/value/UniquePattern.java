package jp.linanfine.dsma.value;

import java.util.TreeMap;

import jp.linanfine.dsma.value._enum.PatternType;

public class UniquePattern implements Comparable<UniquePattern> {
	public int MusicId;
	public IdToWebMusicIdList WebMusicIds;
	public PatternType Pattern;
	public MusicSort Comparer;
	public TreeMap<Integer, MusicData> musics;
	public TreeMap<Integer, MusicScore> scores;
	public String RivalName;
	public TreeMap<Integer, MusicScore> rivalScores;
	public String Comment;
	
	public int compareTo(UniquePattern arg0) 
	{
		return Comparer.compare(this, arg0);
	}
}

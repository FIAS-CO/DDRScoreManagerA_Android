package jp.linanfine.dsma.value.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicDataAdapterArguments;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value.WebMusicId;
import jp.linanfine.dsma.value._enum.PatternType;

public class UniquePatternCollection extends ArrayList<UniquePattern> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1840255985807253324L;
	
	public UniquePatternCollection()
	{
		
	}

	public UniquePatternCollection(MusicDataAdapterArguments args)
	{
		super();

		//long startTime = System.nanoTime();
		
		UniquePattern pattern;
        Iterator<Integer> it = args.Musics.keySet().iterator();
        while (it.hasNext()) {
        	Integer key = it.next();
			MusicData md = args.Musics.get(key);
			MusicScore ms;
			if(args.Scores.containsKey(key))
			{
				ms = args.Scores.get(key);
			}
			else
			{
				ms = new MusicScore();
			}
			MusicScore rs;
			if(args.RivalScores != null && args.RivalScores.containsKey(key))
			{
				rs = args.RivalScores.get(key);
			}
			else
			{
				rs = new MusicScore();
			}
			String[] comments;
			if(args.Comments.containsKey(key))
			{
				comments = args.Comments.get(key);
			}
			else
			{
				comments = new String[9];
			}
			WebMusicId webId = args.WebMusicIds.get(key);
			if(args.Filter.checkFilter(webId, md, PatternType.bSP, ms, rs) && md.Difficulty_bSP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.bSP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[0];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.BSP, ms, rs) && md.Difficulty_BSP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.BSP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[1];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.DSP, ms, rs) && md.Difficulty_DSP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.DSP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[2];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.ESP, ms, rs) && md.Difficulty_ESP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.ESP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[3];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.CSP, ms, rs) && md.Difficulty_CSP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.CSP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[4];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.BDP, ms, rs) && md.Difficulty_BDP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.BDP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[5];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.DDP, ms, rs) && md.Difficulty_DDP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.DDP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[6];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.EDP, ms, rs) && md.Difficulty_EDP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.EDP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[7];
				this.add(pattern);
			}
			if(args.Filter.checkFilter(webId, md, PatternType.CDP, ms, rs) && md.Difficulty_CDP != 0)
			{
				pattern = new UniquePattern();
				pattern.musics = args.Musics;
				pattern.scores = args.Scores;
				pattern.Comparer = args.Sort;
				pattern.WebMusicIds = args.WebMusicIds;
				pattern.MusicId = md.Id;
				pattern.Pattern = PatternType.CDP;
				pattern.RivalName = args.RivalName;
				pattern.rivalScores = args.RivalScores;
				pattern.Comment = comments[8];
				this.add(pattern);
			}
		}
        {
	        //pattern = new UniquePattern();
	        //pattern.MusicId = -1;
	        //this.add(pattern);
        }
        Collections.sort(this);

		 //long endTime = System.nanoTime();
		 
		 //Log.d("RqTime", String.valueOf((endTime-startTime)/1000000.0f) + " / " + String.valueOf(this.size()));

	}
}

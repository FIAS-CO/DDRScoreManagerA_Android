package jp.linanfine.dsma.value;

import java.util.TreeMap;

import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value._enum.SeriesTitle;

public class MusicData {
	public int Id;
	public String Name;
	public String Ruby;
	public SeriesTitle SeriesTitle;
	public int MinBPM;
	public int MaxBPM;
	public int Difficulty_bSP;
	public int Difficulty_BSP;
	public int Difficulty_DSP;
	public int Difficulty_ESP;
	public int Difficulty_CSP;
	public int Difficulty_BDP;
	public int Difficulty_DDP;
	public int Difficulty_EDP;
	public int Difficulty_CDP;
	public TreeMap<Integer, BooleanPair> ShockArrowExists;
	
    public int getDifficulty(PatternType pat)
    {
		switch(pat)
		{
		case bSP:
			return Difficulty_bSP;
		case BSP:
			return Difficulty_BSP;
		case DSP:
			return Difficulty_DSP;
		case ESP:
			return Difficulty_ESP;
		case CSP:
			return Difficulty_CSP;
		case BDP:
			return Difficulty_BDP;
		case DDP:
			return Difficulty_DDP;
		case EDP:
			return Difficulty_EDP;
		case CDP:
			return Difficulty_CDP;
		default:
			return 0;
		}
    }
}

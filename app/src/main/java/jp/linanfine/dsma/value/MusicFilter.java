package jp.linanfine.dsma.value;

import java.util.ArrayList;

import android.util.Log;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value._enum.SeriesTitle;
import jp.linanfine.dsma.value._enum.ShockArrowInclude;

public class MusicFilter {
	
	public String Title = null;
	public String StartsWith = null;
	public ArrayList<Integer> MusicIdList = null;
	public ArrayList<PatternType> MusicPatternList = null;
	
	public int ScoreMin = 0;
	public int ScoreMax = 1000000;	
	public int ScoreMinRival = 0;
	public int ScoreMaxRival = 1000000;
	
	public int MaxComboMin = -1;
	public int MaxComboMax = Integer.MAX_VALUE;
	public int MaxComboMinRival = -1;
	public int MaxComboMaxRival = Integer.MAX_VALUE;

	public int PlayCountMin = -1;
	public int PlayCountMax = Integer.MAX_VALUE;
	public int PlayCountMinRival = -1;
	public int PlayCountMaxRival = Integer.MAX_VALUE;

	public int ClearCountMin = -1;
	public int ClearCountMax = Integer.MAX_VALUE;
	public int ClearCountMinRival = -1;
	public int ClearCountMaxRival = Integer.MAX_VALUE;
	
	public boolean RivalWin = true;
	public boolean RivalLose = true;
	public boolean RivalDraw = true;

	public int ScoreDifferenceMinusMax = -1000000;
	public int ScoreDifferenceMinusMin = 0;
	public int ScoreDifferencePlusMax = 1000000;
	public int ScoreDifferencePlusMin = 0;

	public int MaxComboDifferenceMinusMax = Integer.MIN_VALUE;
	public int MaxComboDifferenceMinusMin = 1;
	public int MaxComboDifferencePlusMax = Integer.MAX_VALUE;
	public int MaxComboDifferencePlusMin = -1;

	public int PlayCountDifferenceMinusMax = Integer.MIN_VALUE;
	public int PlayCountDifferenceMinusMin = 1;
	public int PlayCountDifferencePlusMax = Integer.MAX_VALUE;
	public int PlayCountDifferencePlusMin = -1;

	public int ClearCountDifferenceMinusMax = Integer.MIN_VALUE;
	public int ClearCountDifferenceMinusMin = 1;
	public int ClearCountDifferencePlusMax = Integer.MAX_VALUE;
	public int ClearCountDifferencePlusMin = -1;
	
	public boolean bSP = true;
	public boolean BSP = true;
	public boolean DSP = true;
	public boolean ESP = true;
	public boolean CSP = true;
	public boolean BDP = true;
	public boolean DDP = true;
	public boolean EDP = true;
	public boolean CDP = true;
	public boolean AllowOnlyChallenge = true;
	public boolean AllowExpertWhenNoChallenge = true;
	public ShockArrowInclude ShockArrowSP = ShockArrowInclude.Include;
	public ShockArrowInclude ShockArrowDP = ShockArrowInclude.Include;
	
	public boolean Dif1 = true;
	public boolean Dif2 = true;
	public boolean Dif3 = true;
	public boolean Dif4 = true;
	public boolean Dif5 = true;
	public boolean Dif6 = true;
	public boolean Dif7 = true;
	public boolean Dif8 = true;
	public boolean Dif9 = true;
	public boolean Dif10 = true;
	public boolean Dif11 = true;
	public boolean Dif12 = true;
	public boolean Dif13 = true;
	public boolean Dif14 = true;
	public boolean Dif15 = true;
	public boolean Dif16 = true;
	public boolean Dif17 = true;
	public boolean Dif18 = true;
	public boolean Dif19 = true;

	public boolean SerWorld = true;
	public boolean SerA3 = true;
	public boolean SerA20PLUS = true;
	public boolean SerA20 = true;
	public boolean SerA = true;
	public boolean Ser2014 = true;
	public boolean Ser2013 = true;
	public boolean SerX3 = true;
	public boolean SerX3vs2ndMIX = true;
	public boolean SerX2 = true;
	public boolean SerX = true;
	public boolean SerEXTREME = true;
	public boolean SerMAX2 = true;
	public boolean SerMAX = true;
	public boolean SerSuperNova2 = true;
	public boolean SerSuperNova = true;
	public boolean Ser5th = true;
	public boolean Ser4th = true;
	public boolean Ser3rd = true;
	public boolean Ser2nd = true;
	public boolean Ser1st = true;
	
	public boolean RankAAA = true;
	public boolean RankAAp = true;
	public boolean RankAA = true;
	public boolean RankAAm = true;
	public boolean RankAp = true;
	public boolean RankA = true;
	public boolean RankAm = true;
	public boolean RankBp = true;
	public boolean RankB = true;
	public boolean RankBm = true;
	public boolean RankCp = true;
	public boolean RankC = true;
	public boolean RankCm = true;
	public boolean RankDp = true;
	public boolean RankD = true;
	public boolean RankE = true;
	public boolean RankNoPlay = true;
	
	public boolean FcMFC = true;
	public boolean FcPFC = true;
	public boolean FcFC = true;
	public boolean FcGFC = true;
	public boolean FcLife4 = true;
	public boolean FcNoFC = true;

	public boolean RankAAArival = true;
	public boolean RankAAprival = true;
	public boolean RankAArival = true;
	public boolean RankAAmrival = true;
	public boolean RankAprival = true;
	public boolean RankArival = true;
	public boolean RankAmrival = true;
	public boolean RankBprival = true;
	public boolean RankBrival = true;
	public boolean RankBmrival = true;
	public boolean RankCprival = true;
	public boolean RankCrival = true;
	public boolean RankCmrival = true;
	public boolean RankDprival = true;
	public boolean RankDrival = true;
	public boolean RankErival = true;
	public boolean RankNoPlayrival = true;
	
	public boolean FcMFCrival = true;
	public boolean FcPFCrival = true;
	public boolean FcFCrival = true;
	public boolean FcGFCrival = true;
	public boolean FcLife4rival = true;
	public boolean FcNoFCrival = true;
	
	public boolean Deleted = true;

	public boolean checkFilter(WebMusicId webId, MusicData music, PatternType pattern, MusicScore score, MusicScore rivalScore)
	{
		if(!Deleted && webId.isDeleted)
		{
			return false;
		}
		int diff;
		switch(pattern)
		{
		case bSP:
			diff = music.Difficulty_bSP;
			break;
		case BSP:
			diff = music.Difficulty_BSP;
			break;
		case DSP:
			diff = music.Difficulty_DSP;
			break;
		case ESP:
			diff = music.Difficulty_ESP;
			break;
		case CSP:
			diff = music.Difficulty_CSP;
			break;
		case BDP:
			diff = music.Difficulty_BDP;
			break;
		case DDP:
			diff = music.Difficulty_DDP;
			break;
		case EDP:
			diff = music.Difficulty_EDP;
			break;
		case CDP:
			diff = music.Difficulty_CDP;
			break;
		default:
			diff = 0;
			break;
		}
		if(diff == 0) return false;
		if(Title != null && music.Name != Title) return false;
		if(StartsWith != null)
		{
			if(StartsWith.equals("***NUM***"))
			{
				String lc1 = music.Ruby.toLowerCase();
				if(lc1.startsWith("a")) return false;
				if(lc1.startsWith("b")) return false;
				if(lc1.startsWith("c")) return false;
				if(lc1.startsWith("d")) return false;
				if(lc1.startsWith("e")) return false;
				if(lc1.startsWith("f")) return false;
				if(lc1.startsWith("g")) return false;
				if(lc1.startsWith("h")) return false;
				if(lc1.startsWith("i")) return false;
				if(lc1.startsWith("j")) return false;
				if(lc1.startsWith("k")) return false;
				if(lc1.startsWith("l")) return false;
				if(lc1.startsWith("m")) return false;
				if(lc1.startsWith("n")) return false;
				if(lc1.startsWith("o")) return false;
				if(lc1.startsWith("p")) return false;
				if(lc1.startsWith("q")) return false;
				if(lc1.startsWith("r")) return false;
				if(lc1.startsWith("s")) return false;
				if(lc1.startsWith("t")) return false;
				if(lc1.startsWith("u")) return false;
				if(lc1.startsWith("v")) return false;
				if(lc1.startsWith("w")) return false;
				if(lc1.startsWith("x")) return false;
				if(lc1.startsWith("y")) return false;
				if(lc1.startsWith("z")) return false;
			}
			else if(!music.Ruby.toLowerCase().startsWith(StartsWith.toLowerCase())) return false;
		}
		if(!bSP && pattern == PatternType.bSP) return false;
		if(!BSP && pattern == PatternType.BSP) return false;
		if(!DSP && pattern == PatternType.DSP) return false;
		if(!ESP && pattern == PatternType.ESP && !(AllowExpertWhenNoChallenge && (CSP && music.Difficulty_CSP==0))) return false;
		if(!CSP && pattern == PatternType.CSP && !(AllowOnlyChallenge && ((ESP && music.Difficulty_ESP==0)||(DSP && music.Difficulty_DSP==0)||(BSP && music.Difficulty_BSP==0)||(bSP && music.Difficulty_bSP==0)))) return false;
		if(!BDP && pattern == PatternType.BDP) return false;
		if(!DDP && pattern == PatternType.DDP) return false;
		if(!EDP && pattern == PatternType.EDP && !(AllowExpertWhenNoChallenge && (CDP && music.Difficulty_CDP==0))) return false;
		if(!CDP && pattern == PatternType.CDP && !(AllowOnlyChallenge && ((EDP && music.Difficulty_EDP==0)||(DDP && music.Difficulty_DDP==0)||(BDP && music.Difficulty_BDP==0)))) return false;
		
		if(pattern == PatternType.CSP)
		{
			boolean exists = music.ShockArrowExists.containsKey(music.Id) && music.ShockArrowExists.get(music.Id).b1;
			if(ShockArrowSP == ShockArrowInclude.Only && !exists) return false;
			if(ShockArrowSP == ShockArrowInclude.Exclude && exists) return false;
		}

		if(pattern == PatternType.CDP)
		{
			boolean exists = music.ShockArrowExists.containsKey(music.Id) && music.ShockArrowExists.get(music.Id).b2;
			if(ShockArrowDP == ShockArrowInclude.Only && !exists) return false;
			if(ShockArrowDP == ShockArrowInclude.Exclude && exists) return false;
		}
		
		ScoreData myscore;
		{
			ScoreData scoreData;
			switch(pattern)
			{
			case bSP:
				scoreData = score.bSP;
				break;
			case BSP:
				scoreData = score.BSP;
				break;
			case DSP:
				scoreData = score.DSP;
				break;
			case ESP:
				scoreData = score.ESP;
				break;
			case CSP:
				scoreData = score.CSP;
				break;
			case BDP:
				scoreData = score.BDP;
				break;
			case DDP:
				scoreData = score.DDP;
				break;
			case EDP:
				scoreData = score.EDP;
				break;
			case CDP:
				scoreData = score.CDP;
				break;
			default:
				scoreData = new ScoreData();
				break;
			}
			//Log.w("XXX", String.valueOf(Dif1)+":"+String.valueOf(diff));
			if(!Dif1 && diff == 1) return false;
			//Log.w("XXX", "!!!!");
			if(!Dif2 && diff == 2) return false;
			if(!Dif3 && diff == 3) return false;
			if(!Dif4 && diff == 4) return false;
			if(!Dif5 && diff == 5) return false;
			if(!Dif6 && diff == 6) return false;
			if(!Dif7 && diff == 7) return false;
			if(!Dif8 && diff == 8) return false;
			if(!Dif9 && diff == 9) return false;
			if(!Dif10 && diff == 10) return false;
			if(!Dif11 && diff == 11) return false;
			if(!Dif12 && diff == 12) return false;
			if(!Dif13 && diff == 13) return false;
			if(!Dif14 && diff == 14) return false;
			if(!Dif15 && diff == 15) return false;
			if(!Dif16 && diff == 16) return false;
			if(!Dif17 && diff == 17) return false;
			if(!Dif18 && diff == 18) return false;
			if(!Dif19 && diff == 19) return false;
			
			if(!RankAAA && scoreData.Rank == MusicRank.AAA) return false;
			if(!RankAAp && scoreData.Rank == MusicRank.AAp) return false;
			if(!RankAA && scoreData.Rank == MusicRank.AA) return false;
			if(!RankAAm && scoreData.Rank == MusicRank.AAm) return false;
			if(!RankAp && scoreData.Rank == MusicRank.Ap) return false;
			if(!RankA && scoreData.Rank == MusicRank.A) return false;
			if(!RankAm && scoreData.Rank == MusicRank.Am) return false;
			if(!RankBp && scoreData.Rank == MusicRank.Bp) return false;
			if(!RankB && scoreData.Rank == MusicRank.B) return false;
			if(!RankBm && scoreData.Rank == MusicRank.Bm) return false;
			if(!RankCp && scoreData.Rank == MusicRank.Cp) return false;
			if(!RankC && scoreData.Rank == MusicRank.C) return false;
			if(!RankCm && scoreData.Rank == MusicRank.Cm) return false;
			if(!RankDp && scoreData.Rank == MusicRank.Dp) return false;
			if(!RankD && scoreData.Rank == MusicRank.D) return false;
			if(!RankE && scoreData.Rank == MusicRank.E) return false;
			if(!RankNoPlay && scoreData.Rank == MusicRank.Noplay) return false;
			
			if(!FcMFC && scoreData.FullComboType == FullComboType.MerverousFullCombo) return false;
			if(!FcPFC && scoreData.FullComboType == FullComboType.PerfectFullCombo) return false;
			if(!FcFC && scoreData.FullComboType == FullComboType.FullCombo) return false;
			if(!FcGFC && scoreData.FullComboType == FullComboType.GoodFullCombo) return false;
			if(!FcLife4 && scoreData.FullComboType == FullComboType.Life4) return false;
			if(!FcNoFC && scoreData.FullComboType == FullComboType.None) return false;
			
			if(scoreData.Score < ScoreMin || ScoreMax < scoreData.Score) return false;
			if(scoreData.MaxCombo < MaxComboMin || MaxComboMax < scoreData.MaxCombo) return false;
			if(scoreData.PlayCount < PlayCountMin || PlayCountMax < scoreData.PlayCount) return false;
			if(scoreData.ClearCount < ClearCountMin || ClearCountMax < scoreData.ClearCount) return false;
			
			myscore = scoreData;
		}

		if(!SerWorld && music.SeriesTitle == SeriesTitle.World) return false;
		if(!SerA3 && music.SeriesTitle == SeriesTitle.A3) return false;
		if(!SerA20PLUS && music.SeriesTitle == SeriesTitle.A20PLUS) return false;
		if(!SerA20 && music.SeriesTitle == SeriesTitle.A20) return false;
		if(!SerA && music.SeriesTitle == SeriesTitle.A) return false;
		if(!Ser2014 && music.SeriesTitle == SeriesTitle._2014) return false;
		if(!Ser2013 && music.SeriesTitle == SeriesTitle._2013) return false;
		if(!SerX3 && music.SeriesTitle == SeriesTitle.X3) return false;
		if(!SerX3vs2ndMIX && music.SeriesTitle == SeriesTitle.X3_2ndMix) return false;
		if(!SerX2 && music.SeriesTitle == SeriesTitle.X2) return false;
		if(!SerX && music.SeriesTitle == SeriesTitle.X) return false;
		if(!SerSuperNova2 && music.SeriesTitle == SeriesTitle.SuperNOVA2) return false;
		if(!SerSuperNova && music.SeriesTitle == SeriesTitle.SuperNOVA) return false;
		if(!SerEXTREME && music.SeriesTitle == SeriesTitle.EXTREME) return false;
		if(!SerMAX2 && music.SeriesTitle == SeriesTitle.MAX2) return false;
		if(!SerMAX && music.SeriesTitle == SeriesTitle.MAX) return false;
		if(!Ser5th && music.SeriesTitle == SeriesTitle._5th) return false;
		if(!Ser4th && music.SeriesTitle == SeriesTitle._4th) return false;
		if(!Ser3rd && music.SeriesTitle == SeriesTitle._3rd) return false;
		if(!Ser2nd && music.SeriesTitle == SeriesTitle._2nd) return false;
		if(!Ser1st && music.SeriesTitle == SeriesTitle._1st) return false;

		if(!RankAAArival || !RankAAprival || !RankAArival || !RankAAmrival || 
		   !RankAprival || !RankArival || !RankAmrival || 
		   !RankBprival || !RankBrival || !RankBmrival || 
		   !RankCprival || !RankCrival || !RankCmrival || 
		   !RankDprival || !RankDrival || !RankErival || !RankNoPlayrival ||
		   !FcMFCrival || !FcPFCrival || !FcFCrival || !FcGFCrival || !FcNoFCrival ||
		   !RivalWin || !RivalLose || !RivalDraw || ScoreMinRival > 0 || ScoreMaxRival < 1000000 ||
		   ScoreDifferenceMinusMax > -1000000 || ScoreDifferenceMinusMin < 0 || ScoreDifferencePlusMin > 0 || ScoreDifferencePlusMax < 1000000 ||
		   MaxComboDifferenceMinusMax != Integer.MIN_VALUE || MaxComboDifferenceMinusMin <= 0 || MaxComboDifferencePlusMin >= 0 || MaxComboDifferencePlusMax != Integer.MAX_VALUE ||
		   PlayCountDifferenceMinusMax != Integer.MIN_VALUE || PlayCountDifferenceMinusMin <= 0 || PlayCountDifferencePlusMin >= 0 || PlayCountDifferencePlusMax != Integer.MAX_VALUE ||
		   ClearCountDifferenceMinusMax != Integer.MIN_VALUE || ClearCountDifferenceMinusMin <= 0 || ClearCountDifferencePlusMin >= 0 || ClearCountDifferencePlusMax != Integer.MAX_VALUE )
		{
			ScoreData scoreData;
			if(rivalScore == null)
			{
				scoreData = new ScoreData();
			}
			else
			{
				switch(pattern)
				{
				case bSP:
					scoreData = rivalScore.bSP;
					break;
				case BSP:
					scoreData = rivalScore.BSP;
					break;
				case DSP:
					scoreData = rivalScore.DSP;
					break;
				case ESP:
					scoreData = rivalScore.ESP;
					break;
				case CSP:
					scoreData = rivalScore.CSP;
					break;
				case BDP:
					scoreData = rivalScore.BDP;
					break;
				case DDP:
					scoreData = rivalScore.DDP;
					break;
				case EDP:
					scoreData = rivalScore.EDP;
					break;
				case CDP:
					scoreData = rivalScore.CDP;
					break;
				default:
					scoreData = new ScoreData();
					break;
				}
			}
				
			if(!RankAAArival && scoreData.Rank == MusicRank.AAA) return false;
			if(!RankAAprival && scoreData.Rank == MusicRank.AAp) return false;
			if(!RankAArival && scoreData.Rank == MusicRank.AA) return false;
			if(!RankAAmrival && scoreData.Rank == MusicRank.AAm) return false;
			if(!RankAprival && scoreData.Rank == MusicRank.Ap) return false;
			if(!RankArival && scoreData.Rank == MusicRank.A) return false;
			if(!RankAmrival && scoreData.Rank == MusicRank.Am) return false;
			if(!RankBprival && scoreData.Rank == MusicRank.Bp) return false;
			if(!RankBrival && scoreData.Rank == MusicRank.B) return false;
			if(!RankBmrival && scoreData.Rank == MusicRank.Bm) return false;
			if(!RankCprival && scoreData.Rank == MusicRank.Cp) return false;
			if(!RankCrival && scoreData.Rank == MusicRank.C) return false;
			if(!RankCmrival && scoreData.Rank == MusicRank.Cm) return false;
			if(!RankDprival && scoreData.Rank == MusicRank.Dp) return false;
			if(!RankDrival && scoreData.Rank == MusicRank.D) return false;
			if(!RankErival && scoreData.Rank == MusicRank.E) return false;
			if(!RankNoPlayrival && scoreData.Rank == MusicRank.Noplay) return false;
			
			if(!FcMFCrival && scoreData.FullComboType == FullComboType.MerverousFullCombo) return false;
			if(!FcPFCrival && scoreData.FullComboType == FullComboType.PerfectFullCombo) return false;
			if(!FcFCrival && scoreData.FullComboType == FullComboType.FullCombo) return false;
			if(!FcGFCrival && scoreData.FullComboType == FullComboType.GoodFullCombo) return false;
			if(!FcLife4rival && scoreData.FullComboType == FullComboType.Life4) return false;
			if(!FcNoFCrival && scoreData.FullComboType == FullComboType.None) return false;
			
			//Log.e(String.valueOf(ScoreMinRival), String.valueOf(scoreData.Score));
			if(scoreData.Score < ScoreMinRival || ScoreMaxRival < scoreData.Score) return false;
			if(scoreData.MaxCombo < MaxComboMinRival || MaxComboMaxRival < scoreData.MaxCombo) return false;
			if(scoreData.PlayCount < PlayCountMinRival || PlayCountMaxRival < scoreData.PlayCount) return false;
			if(scoreData.ClearCount < ClearCountMinRival || ClearCountMaxRival < scoreData.ClearCount) return false;
			
			int dif = myscore.Score-scoreData.Score;
			if(!RivalWin && dif > 0) return false;
			if(!RivalLose && dif < 0) return false;
			if(!RivalDraw && dif == 0) return false;
			if(dif < ScoreDifferenceMinusMax || (ScoreDifferenceMinusMin < dif && dif < ScoreDifferencePlusMin) || ScoreDifferencePlusMax < dif) return false;
			dif = myscore.MaxCombo - scoreData.MaxCombo;
			if(dif < MaxComboDifferenceMinusMax || (MaxComboDifferenceMinusMin < dif && dif < MaxComboDifferencePlusMin) || MaxComboDifferencePlusMax < dif) return false;
			dif = myscore.PlayCount - scoreData.PlayCount;
			if(dif < PlayCountDifferenceMinusMax || (PlayCountDifferenceMinusMin < dif && dif < PlayCountDifferencePlusMin) || PlayCountDifferencePlusMax < dif) return false;
			dif = myscore.ClearCount - scoreData.ClearCount;
			if(dif < ClearCountDifferenceMinusMax || (ClearCountDifferenceMinusMin < dif && dif < ClearCountDifferencePlusMin) || ClearCountDifferencePlusMax < dif) return false;
		}

		if(MusicIdList != null && MusicPatternList != null)
		{
			int count = MusicIdList.size();
			boolean exists = false;
			for(int i = 0; i < count; i++)
			{
				if(music.Id == MusicIdList.get(i) && pattern.equals(MusicPatternList.get(i)))
				{
					exists = true;
					break;
				}
			}
			if(!exists)
			{
				return false;
			}
		}
		
		return true;
	}
}

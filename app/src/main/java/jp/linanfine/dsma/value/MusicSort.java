package jp.linanfine.dsma.value;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.MusicSortType;
import jp.linanfine.dsma.value._enum.PatternType;
import jp.linanfine.dsma.value._enum.SeriesTitle;
import jp.linanfine.dsma.value._enum.SortOrder;

public class MusicSort {
	public MusicSortType _1stType;
	public SortOrder _1stOrder;
	public MusicSortType _2ndType;
	public SortOrder _2ndOrder;
	public MusicSortType _3rdType;
	public SortOrder _3rdOrder;
	public MusicSortType _4thType;
	public SortOrder _4thOrder;
	public MusicSortType _5thType;
	public SortOrder _5thOrder;
	
	public int compare(UniquePattern p, UniquePattern m)
	{
		if(p.MusicId < 0)
		{
			if(m.MusicId < 0)
			{
				return 0;
			}
			else
			{
				return 1;
			}
		}
		else if(m.MusicId < 0)
		{
			return -1;
		}
		return compare(p, m, 1);
	}
	
	protected int compare(UniquePattern p, UniquePattern m, int section)
	{
		int ret = 0;
		MusicSortType type;
		SortOrder order;
		if(section == 1)
		{
			type = _1stType;
			order = _1stOrder;
		}
		else if(section == 2)
		{
			type = _2ndType;
			order = _2ndOrder;
		}
		else if(section == 3){
			type = _3rdType;
			order = _3rdOrder;
		}
		else if(section == 4){
			type = _4thType;
			order = _4thOrder;
		}
		else if(section == 5){
			type = _5thType;
			order = _5thOrder;
		}
		else
		{
			return 0;
		}
		if(type == MusicSortType.None)
		{
			return 0;
		}
		ScoreData scoreP;
		if(p.scores.containsKey(p.MusicId))
		{
			switch(p.Pattern)
			{
				case bSP:
					scoreP = p.scores.get(p.MusicId).bSP;
					break;
				case BSP:
					scoreP = p.scores.get(p.MusicId).BSP;
					break;
				case DSP:
					scoreP = p.scores.get(p.MusicId).DSP;
					break;
				case ESP:
					scoreP = p.scores.get(p.MusicId).ESP;
					break;
				case CSP:
					scoreP = p.scores.get(p.MusicId).CSP;
					break;
				case BDP:
					scoreP = p.scores.get(p.MusicId).BDP;
					break;
				case DDP:
					scoreP = p.scores.get(p.MusicId).DDP;
					break;
				case EDP:
					scoreP = p.scores.get(p.MusicId).EDP;
					break;
				case CDP:
					scoreP = p.scores.get(p.MusicId).CDP;
					break;
				default:
					scoreP = new ScoreData();
					break;
			}
		}
		else
		{
			scoreP = new ScoreData();
		}
		ScoreData scoreM;
		if(m.scores.containsKey(m.MusicId))
		{
			switch(m.Pattern)
			{
				case bSP:
					scoreM = m.scores.get(m.MusicId).bSP;
					break;
				case BSP:
					scoreM = m.scores.get(m.MusicId).BSP;
					break;
				case DSP:
					scoreM = m.scores.get(m.MusicId).DSP;
					break;
				case ESP:
					scoreM = m.scores.get(m.MusicId).ESP;
					break;
				case CSP:
					scoreM = m.scores.get(m.MusicId).CSP;
					break;
				case BDP:
					scoreM = m.scores.get(m.MusicId).BDP;
					break;
				case DDP:
					scoreM = m.scores.get(m.MusicId).DDP;
					break;
				case EDP:
					scoreM = m.scores.get(m.MusicId).EDP;
					break;
				case CDP:
					scoreM = m.scores.get(m.MusicId).CDP;
					break;
				default:
					scoreM = new ScoreData();
					break;
			}
		}
		else
		{
			scoreM = new ScoreData();
		}
		ScoreData rivalP;
		ScoreData rivalM;
		switch(type)
		{
			case RivalScore:
			case RivalRank:
			case RivalFullComboType:
			case RivalComboCount:
			case RivalPlayCount:
			case RivalClearCount:
			case RivalScoreDifference:
			case RivalScoreDifferenceAbs:
			{
				if(p.rivalScores != null && p.rivalScores.containsKey(p.MusicId))
				{
					switch(p.Pattern)
					{
						case bSP:
							rivalP = p.rivalScores.get(p.MusicId).bSP;
							break;
						case BSP:
							rivalP = p.rivalScores.get(p.MusicId).BSP;
							break;
						case DSP:
							rivalP = p.rivalScores.get(p.MusicId).DSP;
							break;
						case ESP:
							rivalP = p.rivalScores.get(p.MusicId).ESP;
							break;
						case CSP:
							rivalP = p.rivalScores.get(p.MusicId).CSP;
							break;
						case BDP:
							rivalP = p.rivalScores.get(p.MusicId).BDP;
							break;
						case DDP:
							rivalP = p.rivalScores.get(p.MusicId).DDP;
							break;
						case EDP:
							rivalP = p.rivalScores.get(p.MusicId).EDP;
							break;
						case CDP:
							rivalP = p.rivalScores.get(p.MusicId).CDP;
							break;
						default:
							rivalP = new ScoreData();
							break;
					}
				}
				else
				{
					rivalP = new ScoreData();
				}
				if(m.rivalScores != null && m.rivalScores.containsKey(m.MusicId))
				{
					switch(m.Pattern)
					{
						case bSP:
							rivalM = m.rivalScores.get(m.MusicId).bSP;
							break;
						case BSP:
							rivalM = m.rivalScores.get(m.MusicId).BSP;
							break;
						case DSP:
							rivalM = m.rivalScores.get(m.MusicId).DSP;
							break;
						case ESP:
							rivalM = m.rivalScores.get(m.MusicId).ESP;
							break;
						case CSP:
							rivalM = m.rivalScores.get(m.MusicId).CSP;
							break;
						case BDP:
							rivalM = m.rivalScores.get(m.MusicId).BDP;
							break;
						case DDP:
							rivalM = m.rivalScores.get(m.MusicId).DDP;
							break;
						case EDP:
							rivalM = m.rivalScores.get(m.MusicId).EDP;
							break;
						case CDP:
							rivalM = m.rivalScores.get(m.MusicId).CDP;
							break;
						default:
							rivalM = new ScoreData();
							break;
					}
				}
				else
				{
					rivalM = new ScoreData();
				}
				
				break;
			}
			default:
			{
				rivalP = null;
				rivalM = null;
				break;
			}
		}
		int cp = 0;
		int cm = 0;
		switch(type)
		{
			case MusicName:
				ret = p.musics.get(p.MusicId).Ruby.compareToIgnoreCase(m.musics.get(m.MusicId).Ruby);
				break;
			case Score:
				ret = scoreP.Score-scoreM.Score;
				break;
			case RivalScore:
				ret = rivalP.Score-rivalM.Score;
				break;
			case Rank:
				cp = 
					scoreP.Rank == MusicRank.AAA ? 16 :
						scoreP.Rank == MusicRank.AAp ? 15:
							scoreP.Rank == MusicRank.AA ? 14 :
								scoreP.Rank == MusicRank.AAm ? 13 :
									scoreP.Rank == MusicRank.Ap ? 12 :
										scoreP.Rank == MusicRank.A ? 11 :
											scoreP.Rank == MusicRank.Am ? 10 :
												scoreP.Rank == MusicRank.Bp ? 9 :
													scoreP.Rank == MusicRank.B ? 8 :
														scoreP.Rank == MusicRank.Bm ? 7 :
															scoreP.Rank == MusicRank.Cp ? 6 :
																scoreP.Rank == MusicRank.C ? 5 :
																	scoreP.Rank == MusicRank.Cm ? 4 :
																		scoreP.Rank == MusicRank.Dp ? 3 :
																			scoreP.Rank == MusicRank.D ? 2 :
					scoreP.Rank == MusicRank.E ? 1 :
					0;
				cm = 
						scoreM.Rank == MusicRank.AAA ? 16 :
							scoreM.Rank == MusicRank.AAp ? 15:
								scoreM.Rank == MusicRank.AA ? 14 :
									scoreM.Rank == MusicRank.AAm ? 13 :
										scoreM.Rank == MusicRank.Ap ? 12 :
											scoreM.Rank == MusicRank.A ? 11 :
												scoreM.Rank == MusicRank.Am ? 10 :
													scoreM.Rank == MusicRank.Bp ? 9 :
														scoreM.Rank == MusicRank.B ? 8 :
															scoreM.Rank == MusicRank.Bm ? 7 :
																scoreM.Rank == MusicRank.Cp ? 6 :
																	scoreM.Rank == MusicRank.C ? 5 :
																		scoreM.Rank == MusicRank.Cm ? 4 :
																			scoreM.Rank == MusicRank.Dp ? 3 :
																				scoreM.Rank == MusicRank.D ? 2 :
					scoreM.Rank == MusicRank.E ? 1 :
					0;
				ret = cp-cm;
				break;
			case RivalRank:
				cp = 
						rivalP.Rank == MusicRank.AAA ? 16 :
							rivalP.Rank == MusicRank.AAp ? 15:
								rivalP.Rank == MusicRank.AA ? 14 :
									rivalP.Rank == MusicRank.AAm ? 13 :
										rivalP.Rank == MusicRank.Ap ? 12 :
											rivalP.Rank == MusicRank.A ? 11 :
												rivalP.Rank == MusicRank.Am ? 10 :
													rivalP.Rank == MusicRank.Bp ? 9 :
														rivalP.Rank == MusicRank.B ? 8 :
															rivalP.Rank == MusicRank.Bm ? 7 :
																rivalP.Rank == MusicRank.Cp ? 6 :
																	rivalP.Rank == MusicRank.C ? 5 :
																		rivalP.Rank == MusicRank.Cm ? 4 :
																			rivalP.Rank == MusicRank.Dp ? 3 :
																				rivalP.Rank == MusicRank.D ? 2 :
					rivalP.Rank == MusicRank.E ? 1 :
					0;
				cm = 
						rivalM.Rank == MusicRank.AAA ? 16 :
							rivalM.Rank == MusicRank.AAp ? 15:
								rivalM.Rank == MusicRank.AA ? 14 :
									rivalM.Rank == MusicRank.AAm ? 13 :
										rivalM.Rank == MusicRank.Ap ? 12 :
											rivalM.Rank == MusicRank.A ? 11 :
												rivalM.Rank == MusicRank.Am ? 10 :
													rivalM.Rank == MusicRank.Bp ? 9 :
														rivalM.Rank == MusicRank.B ? 8 :
															rivalM.Rank == MusicRank.Bm ? 7 :
																rivalM.Rank == MusicRank.Cp ? 6 :
																	rivalM.Rank == MusicRank.C ? 5 :
																		rivalM.Rank == MusicRank.Cm ? 4 :
																			rivalM.Rank == MusicRank.Dp ? 3 :
																				rivalM.Rank == MusicRank.D ? 2 :
					rivalM.Rank == MusicRank.E ? 1 :
					0;
				ret = cp-cm;
				break;
			case FullComboType:
				cp = 
					scoreP.FullComboType == FullComboType.MerverousFullCombo ? 5 :
					scoreP.FullComboType == FullComboType.PerfectFullCombo ? 4 :
					scoreP.FullComboType == FullComboType.FullCombo ? 3 :
					scoreP.FullComboType == FullComboType.GoodFullCombo ? 2 :
					scoreP.FullComboType == FullComboType.Life4 ? 1 :
					0;
				cm = 
					scoreM.FullComboType == FullComboType.MerverousFullCombo ? 5 :
					scoreM.FullComboType == FullComboType.PerfectFullCombo ? 4 :
					scoreM.FullComboType == FullComboType.FullCombo ? 3 :
					scoreM.FullComboType == FullComboType.GoodFullCombo ? 2 :
					scoreM.FullComboType == FullComboType.Life4 ? 1 :
					0;
				ret = cp-cm;
				break;
			case RivalFullComboType:
				cp = 
					rivalP.FullComboType == FullComboType.MerverousFullCombo ? 5 :
					rivalP.FullComboType == FullComboType.PerfectFullCombo ? 4 :
					rivalP.FullComboType == FullComboType.FullCombo ? 3 :
					rivalP.FullComboType == FullComboType.GoodFullCombo ? 2 :
					rivalP.FullComboType == FullComboType.Life4 ? 1 :
					0;
				cm = 
					rivalM.FullComboType == FullComboType.MerverousFullCombo ? 5 :
					rivalM.FullComboType == FullComboType.PerfectFullCombo ? 4 :
					rivalM.FullComboType == FullComboType.FullCombo ? 3 :
					rivalM.FullComboType == FullComboType.GoodFullCombo ? 2 :
					rivalM.FullComboType == FullComboType.Life4 ? 1 :
					0;
				ret = cp-cm;
				break;
			case ComboCount:
				ret = scoreP.MaxCombo-scoreM.MaxCombo;
				break;
			case RivalComboCount:
				ret = rivalP.MaxCombo-rivalM.MaxCombo;
				break;
			case PlayCount:
				ret = scoreP.PlayCount-scoreM.PlayCount;
				break;
			case RivalPlayCount:
				ret = rivalP.PlayCount-rivalM.PlayCount;
				break;
			case ClearCount:
				ret = scoreP.ClearCount-scoreM.ClearCount;
				break;
			case RivalClearCount:
				ret = rivalP.ClearCount-rivalM.ClearCount;
				break;
			case RivalScoreDifference:
				ret = (scoreP.Score-rivalP.Score)-(scoreM.Score-rivalM.Score);
				break;
			case RivalScoreDifferenceAbs:
				ret = Math.abs(scoreP.Score-rivalP.Score)-Math.abs(scoreM.Score-rivalM.Score);
				break;
			case Difficulty:
				switch(p.Pattern)
				{
					case bSP:
						cp = p.musics.get(p.MusicId).Difficulty_bSP;
						break;
					case BSP:
						cp = p.musics.get(p.MusicId).Difficulty_BSP;
						break;
					case DSP:
						cp = p.musics.get(p.MusicId).Difficulty_DSP;
						break;
					case ESP:
						cp = p.musics.get(p.MusicId).Difficulty_ESP;
						break;
					case CSP:
						cp = p.musics.get(p.MusicId).Difficulty_CSP;
						break;
					case BDP:
						cp = p.musics.get(p.MusicId).Difficulty_BDP;
						break;
					case DDP:
						cp = p.musics.get(p.MusicId).Difficulty_DDP;
						break;
					case EDP:
						cp = p.musics.get(p.MusicId).Difficulty_EDP;
						break;
					case CDP:
						cp = p.musics.get(p.MusicId).Difficulty_CDP;
						break;
					default:
						cp = 0;
						break;
				}
				switch(m.Pattern)
				{
					case bSP:
						cm = m.musics.get(m.MusicId).Difficulty_bSP;
						break;
					case BSP:
						cm = m.musics.get(m.MusicId).Difficulty_BSP;
						break;
					case DSP:
						cm = m.musics.get(m.MusicId).Difficulty_DSP;
						break;
					case ESP:
						cm = m.musics.get(m.MusicId).Difficulty_ESP;
						break;
					case CSP:
						cm = m.musics.get(m.MusicId).Difficulty_CSP;
						break;
					case BDP:
						cm = m.musics.get(m.MusicId).Difficulty_BDP;
						break;
					case DDP:
						cm = m.musics.get(m.MusicId).Difficulty_DDP;
						break;
					case EDP:
						cm = m.musics.get(m.MusicId).Difficulty_EDP;
						break;
					case CDP:
						cm = m.musics.get(m.MusicId).Difficulty_CDP;
						break;
					default:
						cm = 0;
						break;
				}
				ret = cp-cm;
				break;
			case Pattern:
				cp = 
					p.Pattern == PatternType.bSP ? 0 :
					p.Pattern == PatternType.BSP ? 1 :
					p.Pattern == PatternType.DSP ? 2 :
					p.Pattern == PatternType.ESP ? 3 :
					p.Pattern == PatternType.CSP ? 4 :
					p.Pattern == PatternType.BDP ? 1 :
					p.Pattern == PatternType.DDP ? 2 :
					p.Pattern == PatternType.EDP ? 3 :
					p.Pattern == PatternType.CDP ? 4 :
					0;
				cm = 
					m.Pattern == PatternType.bSP ? 0 :
					m.Pattern == PatternType.BSP ? 1 :
					m.Pattern == PatternType.DSP ? 2 :
					m.Pattern == PatternType.ESP ? 3 :
					m.Pattern == PatternType.CSP ? 4 :
					m.Pattern == PatternType.BDP ? 1 :
					m.Pattern == PatternType.DDP ? 2 :
					m.Pattern == PatternType.EDP ? 3 :
					m.Pattern == PatternType.CDP ? 4 :
					0;
				ret = cp-cm;
				break;
			case SPDP:
				cp = 
					p.Pattern == PatternType.bSP ? 0 :
					p.Pattern == PatternType.BSP ? 0 :
					p.Pattern == PatternType.DSP ? 0 :
					p.Pattern == PatternType.ESP ? 0 :
					p.Pattern == PatternType.CSP ? 0 :
					p.Pattern == PatternType.BDP ? 1 :
					p.Pattern == PatternType.DDP ? 1 :
					p.Pattern == PatternType.EDP ? 1 :
					p.Pattern == PatternType.CDP ? 1 :
					0;
				cm = 
					m.Pattern == PatternType.bSP ? 0 :
					m.Pattern == PatternType.BSP ? 0 :
					m.Pattern == PatternType.DSP ? 0 :
					m.Pattern == PatternType.ESP ? 0 :
					m.Pattern == PatternType.CSP ? 0 :
					m.Pattern == PatternType.BDP ? 1 :
					m.Pattern == PatternType.DDP ? 1 :
					m.Pattern == PatternType.EDP ? 1 :
					m.Pattern == PatternType.CDP ? 1 :
					0;
				ret = cp-cm;
				break;
			case ID:
				//ret = p.MusicId - m.MusicId;
				boolean pe = p.WebMusicIds.containsKey(p.MusicId);
				boolean me = m.WebMusicIds.containsKey(m.MusicId);
				ret = pe?me?
						p.WebMusicIds.get(p.MusicId).idOnWebPage.compareToIgnoreCase(m.WebMusicIds.get(m.MusicId).idOnWebPage):
						1:-1;
				break;
			case BpmMax:
				ret = p.musics.get(p.MusicId).MaxBPM-m.musics.get(m.MusicId).MaxBPM;
				break;
			case BpmMin:
				ret = p.musics.get(p.MusicId).MinBPM-m.musics.get(m.MusicId).MinBPM;
				break;
			case BpmAve:
				ret = (p.musics.get(p.MusicId).MaxBPM+p.musics.get(p.MusicId).MinBPM)/2-(m.musics.get(m.MusicId).MaxBPM+m.musics.get(m.MusicId).MinBPM)/2;
				break;
			case SeriesTitle:
				MusicData pmd = p.musics.get(p.MusicId);
				MusicData mmd = m.musics.get(m.MusicId);
				cp = 
					pmd.SeriesTitle == SeriesTitle._1st ? 1 :
					pmd.SeriesTitle == SeriesTitle._2nd ? 2 :
					pmd.SeriesTitle == SeriesTitle._3rd ? 3 :
					pmd.SeriesTitle == SeriesTitle._4th ? 4 :
					pmd.SeriesTitle == SeriesTitle._5th ? 5 :
					pmd.SeriesTitle == SeriesTitle.MAX ? 6 :
					pmd.SeriesTitle == SeriesTitle.MAX2 ? 7 :
					pmd.SeriesTitle == SeriesTitle.EXTREME ? 8 :
					pmd.SeriesTitle == SeriesTitle.SuperNOVA ? 9 :
					pmd.SeriesTitle == SeriesTitle.SuperNOVA2 ? 10 :
					pmd.SeriesTitle == SeriesTitle.X ? 11 :
					pmd.SeriesTitle == SeriesTitle.X2 ? 12 :
					pmd.SeriesTitle == SeriesTitle.X3 ? 13 :
					pmd.SeriesTitle == SeriesTitle._2013 ? 14 :
						pmd.SeriesTitle == SeriesTitle._2014 ? 15 :
							pmd.SeriesTitle == SeriesTitle.A ? 16 :
					0;
				cm = 
					mmd.SeriesTitle == SeriesTitle._1st ? 1 :
					mmd.SeriesTitle == SeriesTitle._2nd ? 2 :
					mmd.SeriesTitle == SeriesTitle._3rd ? 3 :
					mmd.SeriesTitle == SeriesTitle._4th ? 4 :
					mmd.SeriesTitle == SeriesTitle._5th ? 5 :
					mmd.SeriesTitle == SeriesTitle.MAX ? 6 :
					mmd.SeriesTitle == SeriesTitle.MAX2 ? 7 :
					mmd.SeriesTitle == SeriesTitle.EXTREME ? 8 :
					mmd.SeriesTitle == SeriesTitle.SuperNOVA ? 9 :
					mmd.SeriesTitle == SeriesTitle.SuperNOVA2 ? 10 :
					mmd.SeriesTitle == SeriesTitle.X ? 11 :
					mmd.SeriesTitle == SeriesTitle.X2 ? 12 :
					mmd.SeriesTitle == SeriesTitle.X3 ? 13 :
					mmd.SeriesTitle == SeriesTitle._2013 ? 14 :
						mmd.SeriesTitle == SeriesTitle._2014 ? 15 :
							mmd.SeriesTitle == SeriesTitle.A ? 16 :
					0;
				ret = cp-cm;
				break;
			default:
				ret = 0;
				break;
		}
		ret = (order==SortOrder.Ascending?1:-1)*ret;
		if(ret == 0)
		{
			ret = compare(p, m, section+1);
		}
		return ret;
	}
}

package jp.linanfine.dsma.value;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class ScoreData {
	public MusicRank Rank = MusicRank.Noplay;
	public int Score;
	public int MaxCombo;
	public FullComboType FullComboType = jp.linanfine.dsma.value._enum.FullComboType.None;
	public int PlayCount;
	public int ClearCount;
}

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

	public int FlareRank = -1;
	public int FlareSkill = 0;

	public void updateFlareSkill(int songDifficulty) {
		if (FlareRank == -1) {  // ランクなし
			FlareSkill = 0;
			return;
		}

		if (songDifficulty < 1 || songDifficulty > 19) {
			FlareSkill = 0;
			return;
		}

		int[][] skillValues = {
				{145, 153, 162, 171, 179, 188, 197, 205, 214, 223, 232},
				{155, 164, 182, 192, 201, 210, 220, 220, 229, 238, 248},
				{170, 180, 190, 200, 210, 221, 231, 241, 251, 261, 272},
				{185, 196, 207, 218, 229, 240, 251, 262, 273, 284, 296},
				{205, 217, 229, 241, 254, 266, 278, 291, 303, 315, 328},
				{230, 243, 257, 271, 285, 299, 312, 326, 340, 354, 368},
				{255, 270, 285, 300, 316, 331, 346, 362, 377, 392, 408},
				{290, 307, 324, 342, 359, 377, 394, 411, 429, 446, 464},
				{335, 355, 375, 395, 415, 435, 455, 475, 495, 515, 536},
				{400, 424, 448, 472, 496, 520, 544, 568, 592, 616, 640},
				{465, 492, 520, 548, 576, 604, 632, 660, 688, 716, 744},
				{510, 540, 571, 601, 632, 663, 693, 724, 754, 785, 816},
				{545, 577, 610, 643, 675, 708, 741, 773, 806, 839, 872},
				{575, 609, 644, 678, 713, 747, 782, 816, 851, 885, 920},
				{600, 636, 672, 708, 744, 780, 816, 852, 888, 924, 960},
				{620, 657, 694, 731, 768, 806, 843, 880, 917, 954, 992},
				{635, 673, 711, 749, 787, 825, 863, 901, 939, 977, 1016},
				{650, 689, 728, 767, 806, 845, 884, 923, 962, 1001, 1040},
				{665, 704, 744, 784, 824, 864, 904, 944, 984, 1024, 1064}
		};

		FlareSkill = skillValues[songDifficulty - 1][FlareRank];
	}
}
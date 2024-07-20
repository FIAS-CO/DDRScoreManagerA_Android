package jp.linanfine.dsma.util.flare;

import java.util.Map;
import java.util.TreeMap;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;

public class FlareSkillUpdater {
    public static void updateAllFlareSkills(TreeMap<Integer, MusicData> musicList, TreeMap<Integer, MusicScore> scoreList) {
        for (Map.Entry<Integer, MusicData> entry : musicList.entrySet()) {
            int musicId = entry.getKey();
            MusicData musicData = entry.getValue();
            MusicScore musicScore = scoreList.get(musicId);

            if (musicScore != null) {
                // SPパターン
                updateFlareSkillForPattern(musicScore.bSP, musicData.Difficulty_bSP);
                updateFlareSkillForPattern(musicScore.BSP, musicData.Difficulty_BSP);
                updateFlareSkillForPattern(musicScore.DSP, musicData.Difficulty_DSP);
                updateFlareSkillForPattern(musicScore.ESP, musicData.Difficulty_ESP);
                updateFlareSkillForPattern(musicScore.CSP, musicData.Difficulty_CSP);

                // DPパターン
                updateFlareSkillForPattern(musicScore.BDP, musicData.Difficulty_BDP);
                updateFlareSkillForPattern(musicScore.DDP, musicData.Difficulty_DDP);
                updateFlareSkillForPattern(musicScore.EDP, musicData.Difficulty_EDP);
                updateFlareSkillForPattern(musicScore.CDP, musicData.Difficulty_CDP);

                // 更新されたMusicScoreを保存
                scoreList.put(musicId, musicScore);
            }
        }
    }

    private static void updateFlareSkillForPattern(ScoreData scoreData, int difficulty) {
        scoreData.updateFlareSkill(difficulty);
    }

    public static String getFlareRankText(int flareRank) {
        if (flareRank == 10) {
            return "EX";
        } else if (flareRank >= 1 && flareRank <= 9) {
            String[] romanNumerals = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
            return romanNumerals[flareRank - 1];
        } else if (flareRank == 0) {
            return "0";
        } else {
            return "ー";
        }
    }
}
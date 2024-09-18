package jp.linanfine.dsma.util.skillnote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value._enum.SeriesTitle;

public class TopFlareSkillProcessor {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String processTopFlareSkills(String playerId, TreeMap<Integer, MusicScore> mScoreList, TreeMap<Integer, MusicData> musicDataMap) {
        Map<String, Map<String, List<PlayerScore>>> categorizedScores = new HashMap<>();
        for (String category : Arrays.asList("CLASSIC", "WHITE", "GOLD")) {
            categorizedScores.put(category, new HashMap<>());
            categorizedScores.get(category).put("SP", new ArrayList<>());
            categorizedScores.get(category).put("DP", new ArrayList<>());
        }

        for (Map.Entry<Integer, MusicScore> entry : mScoreList.entrySet()) {
            Integer songId = entry.getKey();
            MusicScore musicScore = entry.getValue();
            MusicData musicData = musicDataMap.get(songId);
            String category = getCategoryForSeries(musicData.SeriesTitle);

            processChartTypes(categorizedScores, songId, musicScore, musicData, category,
                    Arrays.asList("bSP", "BSP", "DSP", "ESP", "CSP"), "SP");
            processChartTypes(categorizedScores, songId, musicScore, musicData, category,
                    Arrays.asList("BDP", "DDP", "EDP", "CDP"), "DP");
        }

        int totalFlareSkillSp = 0;
        int totalFlareSkillDp = 0;

        List<PlayerScore> allTopScores = new ArrayList<>();
        for (Map<String, List<PlayerScore>> categoryScores : categorizedScores.values()) {
            for (Map.Entry<String, List<PlayerScore>> cs : categoryScores.entrySet()) {
                List<PlayerScore> top30Scores = getTop30Scores(cs.getValue());
                int sum = top30Scores.stream()
                        .sorted(Comparator.comparingInt((PlayerScore sc) -> sc.flareSkill).reversed())
                        .limit(30)
                        .mapToInt(sc -> sc.flareSkill)
                        .sum();
                if (cs.getKey().equals("SP")) totalFlareSkillSp += sum;
                else totalFlareSkillDp += sum;
                allTopScores.addAll(top30Scores);
            }
        }

        TopFlareSkillResponse response =
                new TopFlareSkillResponse(playerId, totalFlareSkillSp, totalFlareSkillDp, allTopScores);

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}"; // エラー時は空のオブジェクトを返す
        }
    }

    private void processChartTypes(Map<String, Map<String, List<PlayerScore>>> categorizedScores,
                                   Integer songId, MusicScore musicScore, MusicData musicData,
                                   String category, List<String> chartTypes, String playStyle) {
        for (String chartType : chartTypes) {
            ScoreData scoreData = getScoreDataForChartType(musicScore, chartType);
            if (scoreData.FlareSkill > 0) {
                categorizedScores.get(category).get(playStyle).add(new PlayerScore(
                        songId,
                        chartType,
                        scoreData.Score,
                        String.valueOf(scoreData.FlareRank),
                        scoreData.FlareSkill,
                        musicData.Name,
                        category,
                        playStyle
                ));
            }
        }
    }

    private List<PlayerScore> getTop30Scores(List<PlayerScore> scores) {
        scores.sort(Comparator.comparingInt(PlayerScore::getFlareSkill).reversed());
        if (scores.size() <= 30) {
            return scores;
        }
        int thresholdFlareSkill = scores.get(29).getFlareSkill();
        return scores.stream()
                .filter(score -> score.flareSkill >= thresholdFlareSkill)
                .collect(Collectors.toList());
    }

    private String getCategoryForSeries(SeriesTitle seriesTitle) {
        if (seriesTitle.ordinal() <= SeriesTitle.X3_2ndMix.ordinal()) {
            return "CLASSIC";
        } else if (seriesTitle.ordinal() <= SeriesTitle.A.ordinal()) {
            return "WHITE";
        } else {
            return "GOLD";
        }
    }

    private ScoreData getScoreDataForChartType(MusicScore musicScore, String chartType) {
        switch (chartType) {
            case "bSP":
                return musicScore.bSP;
            case "BSP":
                return musicScore.BSP;
            case "DSP":
                return musicScore.DSP;
            case "ESP":
                return musicScore.ESP;
            case "CSP":
                return musicScore.CSP;
            case "BDP":
                return musicScore.BDP;
            case "DDP":
                return musicScore.DDP;
            case "EDP":
                return musicScore.EDP;
            case "CDP":
                return musicScore.CDP;
            default:
                throw new IllegalArgumentException("Invalid chart type: " + chartType);
        }
    }
}

class TopFlareSkillResponse {
    public final String playerId;
    public final List<PlayerScore> scores;
    public final int totalFlareSkillSp;
    public final int totalFlareSkillDp;

    public TopFlareSkillResponse(
            @JsonProperty("playerId") String playerId,
            @JsonProperty("totalFlareSkillSp") int totalFlareSkillSp,
            @JsonProperty("totalFlareSkillDp") int totalFlareSkillDp,
            @JsonProperty("scores") List<PlayerScore> scores) {
        this.playerId = playerId;
        this.totalFlareSkillSp = totalFlareSkillSp;
        this.totalFlareSkillDp = totalFlareSkillDp;
        this.scores = scores;
    }
}

class PlayerScore {
    public final int songId;
    public final String chartType;
    public final int score;
    public final String flareRank;
    public final int flareSkill;
    public final String songName;
    public final String category;
    public final String playStyle;

    @JsonCreator
    public PlayerScore(
            @JsonProperty("songId") int songId,
            @JsonProperty("chartType") String chartType,
            @JsonProperty("score") int score,
            @JsonProperty("flareRank") String flareRank,
            @JsonProperty("flareSkill") int flareSkill,
            @JsonProperty("songName") String songName,
            @JsonProperty("category") String category,
            @JsonProperty("playStyle") String playStyle) {
        this.songId = songId;
        this.chartType = chartType;
        this.score = score;
        this.flareRank = flareRank;
        this.flareSkill = flareSkill;
        this.songName = songName;
        this.category = category;
        this.playStyle = playStyle;
    }

    public int getFlareSkill() {
        return flareSkill;
    }
}

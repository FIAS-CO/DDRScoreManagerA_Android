package jp.linanfine.dsma.util.skillnote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value._enum.SeriesTitle;

class TopFlareSkillProcessorTest {

    private TopFlareSkillProcessor processor;
    private TreeMap<Integer, MusicScore> mScoreList;
    private TreeMap<Integer, MusicData> musicDataMap;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        processor = new TopFlareSkillProcessor();
        mScoreList = new TreeMap<>();
        musicDataMap = new TreeMap<>();
        objectMapper = new ObjectMapper();

        // テストデータのセットアップ
        setupTestData();
    }

    private void setupTestData() {
        // MusicScore と MusicData のテストデータを作成
        for (int i = 1; i <= 100; i++) {
            MusicScore score = new MusicScore();
            score.ESP = createScoreData(i);
            score.CDP = createScoreData(i + 1);

            MusicData data = new MusicData();
            data.Name = "Song " + i;
            data.SeriesTitle = i <= 30 ? SeriesTitle.X3_2ndMix :
                    (i <= 60 ? SeriesTitle.A : SeriesTitle.A20);

            mScoreList.put(i, score);
            musicDataMap.put(i, data);
        }
    }

    private ScoreData createScoreData(int flareSkill) {
        ScoreData data = new ScoreData();
        data.FlareSkill = flareSkill;
        data.Score = flareSkill * 100;
        data.FlareRank = 5;
        return data;
    }

    @Test
    void testProcessTopFlareSkills() throws Exception {
        String result = processor.processTopFlareSkills("test", mScoreList, musicDataMap);

        assertTrue(result.startsWith("{\"playerId"));
        assertTrue(result.endsWith("]}"));

        TopFlareSkillResponse response = objectMapper.readValue(result, new TypeReference<TopFlareSkillResponse>() {
        });
        List<PlayerScore> scores = response.scores;

        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "GOLD") && ps.playStyle.equals("SP")).count());
        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "WHITE") && ps.playStyle.equals("SP")).count());
        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "CLASSIC") && ps.playStyle.equals("SP")).count());

        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "GOLD") && ps.playStyle.equals("DP")).count());
        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "WHITE") && ps.playStyle.equals("DP")).count());
        assertEquals(30, scores.stream().filter(ps -> Objects.equals(ps.category, "CLASSIC") && ps.playStyle.equals("DP")).count());

        // カテゴリとプレイスタイルが正しく設定されていることを確認
        for (PlayerScore score : scores) {
            assertNotNull(score.category);
            assertTrue(Arrays.asList("CLASSIC", "WHITE", "GOLD").contains(score.category));
            assertTrue(Arrays.asList("SP", "DP").contains(score.playStyle));
        }

        assertEquals("test", response.playerId);
        assertEquals(4395, response.totalFlareSkillSp);
        assertEquals(4485, response.totalFlareSkillDp);
    }

    @Test
    void testEmptyInput() throws Exception {
        String result = processor.processTopFlareSkills("test", new TreeMap<>(), new TreeMap<>());
        TopFlareSkillResponse response = objectMapper.readValue(result, new TypeReference<TopFlareSkillResponse>() {
        });
        List<PlayerScore> scores = response.scores;
        assertTrue(scores.isEmpty());
        assertEquals("test", response.playerId);
        assertEquals(0, response.totalFlareSkillSp);
        assertEquals(0, response.totalFlareSkillDp);
    }

    @Test
    void testLessThan30Scores() throws JsonProcessingException {
        // 20曲のみのデータを準備
        TreeMap<Integer, MusicScore> smallScoreList = new TreeMap<>();
        TreeMap<Integer, MusicData> smallMusicDataMap = new TreeMap<>();

        for (int i = 1; i <= 20; i++) {
            MusicScore score = new MusicScore();
            score.ESP = createScoreData(i);

            MusicData data = new MusicData();
            data.Name = "Song " + i;
            data.SeriesTitle = SeriesTitle.X3_2ndMix;

            smallScoreList.put(i, score);
            smallMusicDataMap.put(i, data);
        }

        String result = processor.processTopFlareSkills("test", smallScoreList, smallMusicDataMap);
        TopFlareSkillResponse response = objectMapper.readValue(result, new TypeReference<TopFlareSkillResponse>() {
        });
        List<PlayerScore> scores = response.scores;

        assertEquals(20, scores.size());
        assertEquals("test", response.playerId);
        assertEquals(210, response.totalFlareSkillSp);
        assertEquals(0, response.totalFlareSkillDp);
    }

    @Test
    void testScoresWithTie() throws JsonProcessingException {
        // 30位と同じFlareSkillを持つ曲がある場合のテスト
        TreeMap<Integer, MusicScore> tieScoreList = new TreeMap<>();
        TreeMap<Integer, MusicData> tieMusicDataMap = new TreeMap<>();

        for (int i = 1; i <= 35; i++) {
            MusicScore score = new MusicScore();
            score.ESP = createScoreData(Math.max(i, 30)); // 31-35番目の曲は30位と同じFlareSkill

            MusicData data = new MusicData();
            data.Name = "Song " + i;
            data.SeriesTitle = SeriesTitle.World;

            tieScoreList.put(i, score);
            tieMusicDataMap.put(i, data);
        }

        String result = processor.processTopFlareSkills("test", tieScoreList, tieMusicDataMap);
        TopFlareSkillResponse response = objectMapper.readValue(result, new TypeReference<TopFlareSkillResponse>() {
        });
        List<PlayerScore> scores = response.scores;

        assertEquals(35, scores.size());
        assertEquals("test", response.playerId);
        assertEquals(915, response.totalFlareSkillSp);
        assertEquals(0, response.totalFlareSkillDp);
    }
}
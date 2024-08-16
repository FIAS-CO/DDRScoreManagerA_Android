package jp.linanfine.dsma.util.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

/** @noinspection NonAsciiCharacters*/
public class HtmlParseUtilTest {

    private String singleHtml;
    private String doubleHtml;

    @BeforeEach
    void setUp() throws IOException {
        singleHtml = new String(Files.readAllBytes(Paths.get("src/test/resources/music_data_single.html")));
        doubleHtml = new String(Files.readAllBytes(Paths.get("src/test/resources/music_data_double.html")));
    }

    @Test
    public void testParseMusicListSingle_forWorld() throws IOException {
        String htmlContent = loadHtmlContent("WorldSiteDataSingle.html");
        List<MusicEntry> result = HtmlParseUtil.parseMusicListForWorld(htmlContent);

        assertFalse(result.isEmpty(), "Result should not be empty");

        testSpecificSongs(result);
        checkFlareRanks(result);
        checkFullComboTypes(result);
    }

    @Test
    public void testParseMusicListDouble_forWorld() throws IOException {
        String htmlContent = loadHtmlContent("WorldSiteDataDouble.html");
        List<MusicEntry> result = HtmlParseUtil.parseMusicListForWorld(htmlContent);

        assertFalse(result.isEmpty(), "Result should not be empty");

        testSpecificSongsDouble(result);
    }

    @Test
    public void testParseMusicListSingle_forWorld_楽曲データ取得不可検証() throws IOException {
        String htmlContent = loadHtmlContent("WorldSiteDataSingle_VerWorld.html");
        List<MusicEntry> result = HtmlParseUtil.parseMusicListForWorld(htmlContent);

        assertFalse(result.isEmpty(), "Result should not be empty");

        testSpecificSongs_検証(result);
    }

    @Test
    void testParseMusicListSingle() {
        List<MusicEntry> result = HtmlParseUtil.parseMusicList(singleHtml);

        assertFalse(result.isEmpty());
        assertEquals(50, result.size());

        // Test the first entry (全難易度Noplay)
        MusicEntry firstEntry = result.get(0);
        assertEquals("蒼い衝動 ～for EXTREME～", firstEntry.getMusicName());
        assertEquals(5, firstEntry.getScores().size());

        for (DifficultyScore score : firstEntry.getScores()) {
            assertEquals(0, score.getScore());
            assertEquals(MusicRank.Noplay, score.getRank());
            assertEquals(FullComboType.None, score.getFullComboType());
        }

        // Test an entry with scores (蒼が消えるとき)
        MusicEntry scoredEntry = result.stream()
                .filter(entry -> entry.getMusicName().equals("蒼が消えるとき"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Entry '蒼が消えるとき' not found"));

        DifficultyScore difficultScore = scoredEntry.getScores().stream()
                .filter(score -> score.getDifficultyId().equals("difficult"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Difficult score not found"));

        assertEquals(999640, difficultScore.getScore());
        assertEquals(MusicRank.AAA, difficultScore.getRank());
        assertEquals(FullComboType.PerfectFullCombo, difficultScore.getFullComboType());

        DifficultyScore expertScore = scoredEntry.getScores().stream()
                .filter(score -> score.getDifficultyId().equals("expert"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expert score not found"));

        assertEquals(998570, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.FullCombo, expertScore.getFullComboType());
    }

    @Test
    void testParseMusicListDouble() {
        List<MusicEntry> result = HtmlParseUtil.parseMusicList(doubleHtml);

        assertFalse(result.isEmpty());
        assertEquals(50, result.size());

        // Test the first entry (全難易度Noplay)
        MusicEntry firstEntry = result.get(0);
        assertEquals("蒼い衝動 ～for EXTREME～", firstEntry.getMusicName());
        assertEquals(4, firstEntry.getScores().size());

        for (DifficultyScore score : firstEntry.getScores()) {
            assertEquals(0, score.getScore());
            assertEquals(MusicRank.Noplay, score.getRank());
            assertEquals(FullComboType.None, score.getFullComboType());
        }

        // Test an entry with scores (蒼が消えるとき)
        MusicEntry scoredEntry = result.stream()
                .filter(entry -> entry.getMusicName().equals("蒼が消えるとき"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Entry '蒼が消えるとき' not found"));

        DifficultyScore difficultScore = scoredEntry.getScores().stream()
                .filter(score -> score.getDifficultyId().equals("difficult"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Difficult score not found"));

        assertEquals(971780, difficultScore.getScore());
        assertEquals(MusicRank.AAp, difficultScore.getRank());
        assertEquals(FullComboType.GoodFullCombo, difficultScore.getFullComboType());

        DifficultyScore expertScore = scoredEntry.getScores().stream()
                .filter(score -> score.getDifficultyId().equals("expert"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expert score not found"));

        assertEquals(896870, expertScore.getScore());
        assertEquals(MusicRank.AAm, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
    }

    @Test
    void testParseEdgeCases() {
        String edgeCaseHtml = "<table><tr class=\"data\"><td><a href=\"#\">Edge Case Song</a></td>" +
                "<td class=\"rank\" id=\"expert\"><div class=\"data_score\">-</div></td></tr></table>";

        List<MusicEntry> result = HtmlParseUtil.parseMusicList(edgeCaseHtml);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());

        MusicEntry edgeCaseEntry = result.get(0);
        assertEquals("Edge Case Song", edgeCaseEntry.getMusicName());
        assertEquals(1, edgeCaseEntry.getScores().size());

        DifficultyScore edgeCaseScore = edgeCaseEntry.getScores().get(0);
        assertEquals("expert", edgeCaseScore.getDifficultyId());
        assertEquals(0, edgeCaseScore.getScore());
        assertEquals(MusicRank.Noplay, edgeCaseScore.getRank());
        assertEquals(FullComboType.None, edgeCaseScore.getFullComboType());
    }

    private void testSpecificSongs(List<MusicEntry> result) {
        MusicEntry songEntry = findSongByName(result, "とこにゃつ☆トロピカル");
        assertNotNull(songEntry, "Song 'とこにゃつ☆トロピカル' not found");
        assertEquals(5, songEntry.getScores().size(), "Should have 5 difficulty scores");

        DifficultyScore difficultScore = findScoreByDifficulty(songEntry, "basic");
        assertNotNull(difficultScore, "BASIC score for 'とこにゃつ☆トロピカル' not found");
        assertEquals(0, difficultScore.getScore());
        assertEquals(MusicRank.Noplay, difficultScore.getRank());
        assertEquals(FullComboType.None, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank()); // フレアランクなし

        // "晴天Bon Voyage"のテスト
        songEntry = findSongByName(result, "晴天Bon Voyage");
        assertNotNull(songEntry, "Song '晴天Bon Voyage' not found");
        assertEquals(5, songEntry.getScores().size(), "Should have 5 difficulty scores");

        difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for '晴天Bon Voyage' not found");
        assertEquals(999580, difficultScore.getScore());
        assertEquals(MusicRank.AAA, difficultScore.getRank());
        assertEquals(FullComboType.PerfectFullCombo, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank()); // フレアランクなし

        DifficultyScore expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '晴天Bon Voyage' not found");
        assertEquals(991320, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.FullCombo, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        // "零 - ZERO -"のテスト（EXフレアランクの確認）
        songEntry = findSongByName(result, "零 - ZERO -");
        assertNotNull(songEntry, "Song '零 - ZERO -' not found");

        DifficultyScore beginnerScore = findScoreByDifficulty(songEntry, "beginner");
        assertNotNull(beginnerScore, "BEGINNER score for '零 - ZERO -' not found");
        assertEquals(1000000, beginnerScore.getScore());
        assertEquals(MusicRank.AAA, beginnerScore.getRank());
        assertEquals(FullComboType.MerverousFullCombo, beginnerScore.getFullComboType());
        assertEquals(2, beginnerScore.getFlareRank());

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '零 - ZERO -' not found");
        assertEquals(999550, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.PerfectFullCombo, expertScore.getFullComboType());
        assertEquals(10, expertScore.getFlareRank()); // EXランク

        // "打打打打打打打打打打"のテスト（フレアランク9の確認）
        songEntry = findSongByName(result, "打打打打打打打打打打");
        assertNotNull(songEntry, "Song '打打打打打打打打打打' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '打打打打打打打打打打' not found");
        assertEquals(997570, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.FullCombo, expertScore.getFullComboType());
        assertEquals(10, expertScore.getFlareRank());

        DifficultyScore challengeScore = findScoreByDifficulty(songEntry, "challenge");
        assertNotNull(challengeScore, "CHALLENGE score for '打打打打打打打打打打' not found");
        assertEquals(985140, challengeScore.getScore());
        assertEquals(MusicRank.AAp, challengeScore.getRank());
        assertEquals(FullComboType.FullCombo, challengeScore.getFullComboType());
        assertEquals(9, challengeScore.getFlareRank()); // フレアランク9

        // "嘆きの樹"のテスト（フレアランク1の確認とクリアランクApの確認）
        songEntry = findSongByName(result, "嘆きの樹");
        assertNotNull(songEntry, "Song '嘆きの樹' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '嘆きの樹' not found");
        assertEquals(940000, expertScore.getScore());
        assertEquals(MusicRank.AA, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(1, expertScore.getFlareRank()); // フレアランク1

        challengeScore = findScoreByDifficulty(songEntry, "challenge");
        assertNotNull(challengeScore, "CHALLENGE score for '嘆きの樹' not found");
        assertEquals(860550, challengeScore.getScore());
        assertEquals(MusicRank.Ap, challengeScore.getRank());
        assertEquals(FullComboType.None, challengeScore.getFullComboType());
        assertEquals(-1, challengeScore.getFlareRank()); // フレアランクなし

        // "ちくわパフェだよ☆ＣＫＰ"のテスト（GoodFullComboの確認）
        songEntry = findSongByName(result, "ちくわパフェだよ☆ＣＫＰ");
        assertNotNull(songEntry, "Song 'ちくわパフェだよ☆ＣＫＰ' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for 'ちくわパフェだよ☆ＣＫＰ' not found");
        assertEquals(996090, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.FullCombo, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        challengeScore = findScoreByDifficulty(songEntry, "challenge");
        assertNotNull(challengeScore, "CHALLENGE score for 'ちくわパフェだよ☆ＣＫＰ' not found");
        assertEquals(991030, challengeScore.getScore());
        assertEquals(MusicRank.AAA, challengeScore.getRank());
        assertEquals(FullComboType.FullCombo, challengeScore.getFullComboType());
        assertEquals(9, challengeScore.getFlareRank()); // フレアランク9

        // "伐折羅-vajra-"のテスト
        songEntry = findSongByName(result, "伐折羅-vajra-");
        assertNotNull(songEntry, "Song '伐折羅-vajra-' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '伐折羅-vajra-' not found");
        assertEquals(925780, expertScore.getScore());
        assertEquals(MusicRank.AA, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        challengeScore = findScoreByDifficulty(songEntry, "challenge");
        assertNotNull(challengeScore, "CHALLENGE score for '伐折羅-vajra-' not found");
        assertEquals(741330, challengeScore.getScore());
        assertEquals(MusicRank.B, challengeScore.getRank());
        assertEquals(FullComboType.None, challengeScore.getFullComboType());
        assertEquals(-1, challengeScore.getFlareRank()); // フレアランクなし

        // "パーフェクトイーター"のテスト
        songEntry = findSongByName(result, "パーフェクトイーター");
        assertNotNull(songEntry, "Song 'パーフェクトイーター' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for 'パーフェクトイーター' not found");
        assertEquals(969480, expertScore.getScore());
        assertEquals(MusicRank.AAp, expertScore.getRank());
        assertEquals(FullComboType.Life4, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        // "春を告げる"のテスト
        songEntry = findSongByName(result, "春を告げる");
        assertNotNull(songEntry, "Song '春を告げる' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '春を告げる' not found");
        assertEquals(735190, expertScore.getScore());
        assertEquals(MusicRank.E, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし
    }

    private void testSpecificSongsDouble(List<MusicEntry> result) {
        MusicEntry songEntry = findSongByName(result, "蒼い衝動 ～for EXTREME～");
        assertNotNull(songEntry, "Song '蒼い衝動 ～for EXTREME～' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        DifficultyScore expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '蒼い衝動 ～for EXTREME～' not found");
        assertEquals(0, expertScore.getScore());
        assertEquals(MusicRank.Noplay, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        // "蒼が消えるとき"のテスト
        songEntry = findSongByName(result, "蒼が消えるとき");
        assertNotNull(songEntry, "Song '蒼が消えるとき' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        DifficultyScore difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for '蒼が消えるとき' not found");
        assertEquals(971780, difficultScore.getScore());
        assertEquals(MusicRank.AAp, difficultScore.getRank());
        assertEquals(FullComboType.GoodFullCombo, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank()); // フレアランクなし

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '蒼が消えるとき' not found");
        assertEquals(896870, expertScore.getScore());
        assertEquals(MusicRank.AAm, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank()); // フレアランクなし

        // "天ノ弱"のテスト
        songEntry = findSongByName(result, "天ノ弱");
        assertNotNull(songEntry, "Song '天ノ弱' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for '天ノ弱' not found");
        assertEquals(867390, difficultScore.getScore());
        assertEquals(MusicRank.Ap, difficultScore.getRank());
        assertEquals(FullComboType.None, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank()); // フレアランクなし

        // "鋳鉄の檻"のテスト
        songEntry = findSongByName(result, "鋳鉄の檻");
        assertNotNull(songEntry, "Song '鋳鉄の檻' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        DifficultyScore basicScore = findScoreByDifficulty(songEntry, "basic");
        assertNotNull(basicScore, "BASIC score for '鋳鉄の檻' not found");
        assertEquals(986070, basicScore.getScore());
        assertEquals(MusicRank.AAp, basicScore.getRank());
        assertEquals(FullComboType.FullCombo, basicScore.getFullComboType());
        assertEquals(-1, basicScore.getFlareRank());

        // "イノセントバイブル"のテスト
        songEntry = findSongByName(result, "イノセントバイブル");
        assertNotNull(songEntry, "Song 'イノセントバイブル' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for 'イノセントバイブル' not found");
        assertEquals(937000, difficultScore.getScore());
        assertEquals(MusicRank.AA, difficultScore.getRank());
        assertEquals(FullComboType.Life4, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank());

        // "梅雪夜"のテスト
        songEntry = findSongByName(result, "梅雪夜");
        assertNotNull(songEntry, "Song '梅雪夜' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for '梅雪夜' not found");
        assertEquals(741400, expertScore.getScore());
        assertEquals(MusicRank.B, expertScore.getRank());
        assertEquals(FullComboType.None, expertScore.getFullComboType());
        assertEquals(-1, expertScore.getFlareRank());

        // "カゲロウ"のテスト
        songEntry = findSongByName(result, "カゲロウ");
        assertNotNull(songEntry, "Song 'カゲロウ' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for 'カゲロウ' not found");
        assertEquals(827740, difficultScore.getScore());
        assertEquals(MusicRank.A, difficultScore.getRank());
        assertEquals(FullComboType.None, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank());

        // "狂水一華"のテスト
        songEntry = findSongByName(result, "狂水一華");
        assertNotNull(songEntry, "Song '狂水一華' not found");
        assertEquals(4, songEntry.getScores().size(), "Should have 4 difficulty scores");

        difficultScore = findScoreByDifficulty(songEntry, "difficult");
        assertNotNull(difficultScore, "DIFFICULT score for '狂水一華' not found");
        assertEquals(705890, difficultScore.getScore());
        assertEquals(MusicRank.E, difficultScore.getRank());
        assertEquals(FullComboType.None, difficultScore.getFullComboType());
        assertEquals(-1, difficultScore.getFlareRank());
    }

    private void testSpecificSongs_検証(List<MusicEntry> result) {
        MusicEntry songEntry = findSongByName(result, "きらきら☆ユニバース");
        assertNotNull(songEntry, "Song 'きらきら☆ユニバース' not found");
        assertEquals(5, songEntry.getScores().size(), "Should have 5 difficulty scores");

        DifficultyScore basicScore = findScoreByDifficulty(songEntry, "basic");
        assertNotNull(basicScore, "BASIC score for 'きらきら☆ユニバース' not found");
        assertEquals(997230, basicScore.getScore());
        assertEquals(MusicRank.AAA, basicScore.getRank());
        assertEquals(FullComboType.FullCombo, basicScore.getFullComboType());
        assertEquals(10, basicScore.getFlareRank()); // フレアランクなし

        // "零 - ZERO -"のテスト（EXフレアランクの確認）
        songEntry = findSongByName(result, "Don't Stop The HYPERCORE");
        assertNotNull(songEntry, "Song 'Don't Stop The HYPERCORE' not found");

        DifficultyScore expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "Expert score for '994080' not found");
        assertEquals(994080, expertScore.getScore());
        assertEquals(MusicRank.AAA, expertScore.getRank());
        assertEquals(FullComboType.GoodFullCombo, expertScore.getFullComboType());
        assertEquals(9, expertScore.getFlareRank());

        // "打打打打打打打打打打"のテスト（フレアランク9の確認）
        songEntry = findSongByName(result, "Happy Dance Day 2 U");
        assertNotNull(songEntry, "Song 'Happy Dance Day 2 U' not found");

        expertScore = findScoreByDifficulty(songEntry, "expert");
        assertNotNull(expertScore, "EXPERT score for 'Happy Dance Day 2 U' not found");
        assertEquals(971390, expertScore.getScore());
        assertEquals(MusicRank.AAp, expertScore.getRank());
        assertEquals(FullComboType.GoodFullCombo, expertScore.getFullComboType());
        assertEquals(1, expertScore.getFlareRank());
    }

    private MusicEntry findSongByName(List<MusicEntry> entries, String name) {
        return entries.stream()
                .filter(entry -> entry.getMusicName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private DifficultyScore findScoreByDifficulty(MusicEntry entry, String difficulty) {
        return entry.getScores().stream()
                .filter(score -> score.getDifficultyId().equals(difficulty))
                .findFirst()
                .orElse(null);
    }

    private void checkFlareRanks(List<MusicEntry> entries) {
        int[] expectedFlareRanks = {-1, 1, 9, 10};
        for (int rank : expectedFlareRanks) {
            Assertions.assertTrue(entries.stream().anyMatch(song -> song.getScores().stream().anyMatch(score -> score.getFlareRank() == rank)), "Should find a song with flare rank " + rank);
        }
    }

    private void checkFullComboTypes(List<MusicEntry> entries) {
        FullComboType[] fullComboTypes = {FullComboType.None, FullComboType.FullCombo, FullComboType.PerfectFullCombo, FullComboType.Life4};
        for (FullComboType type : fullComboTypes) {
            Assertions.assertTrue(entries.stream().anyMatch(song -> song.getScores().stream().anyMatch(score -> score.getFullComboType() == type)), "Should find a song with " + type + " full combo type");
        }
    }

    private String loadHtmlContent(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get("src/test/resources/" + fileName)));
    }
}
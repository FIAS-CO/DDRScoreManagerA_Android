package jp.linanfine.dsma.util.html;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class HtmlParseUtilTest {

    private String singleHtml;
    private String doubleHtml;

    @BeforeEach
    void setUp() throws IOException {
        singleHtml = new String(Files.readAllBytes(Paths.get("src/test/resources/music_data_single.html")));
        doubleHtml = new String(Files.readAllBytes(Paths.get("src/test/resources/music_data_double.html")));
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
}
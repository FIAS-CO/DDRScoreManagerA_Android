package jp.linanfine.dsma.util.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class HtmlParseUtil {

    public static List<MusicEntry> parseMusicList(String src) {
        List<MusicEntry> musicEntries = new ArrayList<>();
        Document doc = Jsoup.parse(src);
        Elements musicRows = doc.select("tr.data");

        for (Element row : musicRows) {
            MusicEntry entry = parseMusicEntry(row);
            if (entry != null) {
                musicEntries.add(entry);
            }
        }

        return musicEntries;
    }

    private static MusicEntry parseMusicEntry(Element row) {
        Element titleLink = row.selectFirst("td a");
        if (titleLink == null) return null;

        String musicName = titleLink.text().trim();
        musicName = TextUtil.escapeWebTitle(musicName);

        List<DifficultyScore> scores = new ArrayList<>();
        Elements difficultyColumns = row.select("td.rank");
        for (Element column : difficultyColumns) {
            DifficultyScore score = parseDifficultyScore(column);
            if (score != null) {
                scores.add(score);
            }
        }

        return new MusicEntry(musicName, scores);
    }

    private static DifficultyScore parseDifficultyScore(Element column) {
        String diffId = column.id();
        Element scoreElement = column.selectFirst("div.data_score");
        if (scoreElement == null) return null;

        String scoreText = scoreElement.text();
        int score = scoreText.equals("-") ? 0 : Integer.parseInt(scoreText);

        boolean isRankE = column.html().contains("rank_s_e");
        MusicRank rank = getRank(score, isRankE);
        FullComboType fullComboType = getFullComboType(column);

        return new DifficultyScore(diffId, score, rank, fullComboType);
    }

    private static MusicRank getRank(int score, boolean isRankE) {
        if (isRankE) return MusicRank.E;
        if (score == 0) return MusicRank.Noplay;
        if (score < 550000) return MusicRank.D;
        if (score < 590000) return MusicRank.Dp;
        if (score < 600000) return MusicRank.Cm;
        if (score < 650000) return MusicRank.C;
        if (score < 690000) return MusicRank.Cp;
        if (score < 700000) return MusicRank.Bm;
        if (score < 750000) return MusicRank.B;
        if (score < 790000) return MusicRank.Bp;
        if (score < 800000) return MusicRank.Am;
        if (score < 850000) return MusicRank.A;
        if (score < 890000) return MusicRank.Ap;
        if (score < 900000) return MusicRank.AAm;
        if (score < 950000) return MusicRank.AA;
        if (score < 990000) return MusicRank.AAp;
        return MusicRank.AAA;
    }

    private static FullComboType getFullComboType(Element column) {
        if (column.html().contains("full_none")) return FullComboType.None;
        if (column.html().contains("full_good")) return FullComboType.GoodFullCombo;
        if (column.html().contains("full_great")) return FullComboType.FullCombo;
        if (column.html().contains("full_perfect")) return FullComboType.PerfectFullCombo;
        if (column.html().contains("full_mar")) return FullComboType.MerverousFullCombo;
        return FullComboType.None;
    }
}
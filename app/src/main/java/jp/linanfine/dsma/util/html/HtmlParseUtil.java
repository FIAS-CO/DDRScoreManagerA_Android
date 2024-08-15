package jp.linanfine.dsma.util.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.linanfine.dsma.util.common.TextUtil;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class HtmlParseUtil {

    public static class ParseResult {
        public GameMode gameMode;
        public List<MusicEntry> musicEntries;

        public ParseResult(GameMode gameMode, List<MusicEntry> musicEntries) {
            this.gameMode = gameMode;
            this.musicEntries = musicEntries;
        }
    }

    public static ParseResult parseMusicListForWorld(String src) throws IOException {
        Document doc = Jsoup.parse(src);
        GameMode gameMode = determineGameMode(doc);
        List<MusicEntry> musicEntries = new ArrayList<>();
        Elements musicRows = doc.select("tr.data");

        for (Element row : musicRows) {
            MusicEntry entry = parseMusicEntry(row, gameMode);
            if (entry != null) {
                musicEntries.add(entry);
            }
        }

        return new ParseResult(gameMode, musicEntries);
    }

    private static GameMode determineGameMode(Document doc) {
        // URLからモードを判定
        Element urlElement = doc.selectFirst("meta[property='og:url']");
        if (urlElement != null) {
            String url = urlElement.attr("content");
            if (url.contains("music_data_single")) {
                return GameMode.SINGLE;
            } else if (url.contains("music_data_double")) {
                return GameMode.DOUBLE;
            }
        }

        // スタイルタブからモードを判定
        if (doc.selectFirst("#t_single a.select") != null) {
            return GameMode.SINGLE;
        } else if (doc.selectFirst("#t_double a.select") != null) {
            return GameMode.DOUBLE;
        }

        throw new IllegalStateException("Unable to determine game mode");
    }

    private static MusicEntry parseMusicEntry(Element row, GameMode gameMode) {
        Element titleLink = row.selectFirst("td a");
        if (titleLink == null) return null;

        String musicName = titleLink.text().trim();
        musicName = TextUtil.escapeWebTitle(musicName);

        List<DifficultyScore> scores = new ArrayList<>();
        Elements difficultyColumns = row.select("td.rank");
        for (int i = 0; i < difficultyColumns.size(); i++) {
            Element column = difficultyColumns.get(i);
            DifficultyScore score = parseDifficultyScore(column, i, gameMode);
            if (score != null) {
                scores.add(score);
            }
        }

        return new MusicEntry(musicName, scores, gameMode);
    }

    private static DifficultyScore parseDifficultyScore(Element column, int index, GameMode gameMode) {
        String diffId = getDifficultyId(index, gameMode);
        Element scoreElement = column.selectFirst("div.data_score");
        if (scoreElement == null) return new DifficultyScore(diffId, 0, MusicRank.Noplay, FullComboType.None, -1);
        String scoreText = scoreElement.text();
        int score = 0;
        try {
            score = Integer.parseInt(scoreText);
        } catch (NumberFormatException e) {
            // パースに失敗した場合（"---"を含む）、スコアは0のまま
        }

        Element rankElement = column.selectFirst("div.data_rank img");
        MusicRank rank = getRank(rankElement, score);
        FullComboType fullComboType = getFullComboType(column);
        int flareRank = parseFlareRank(column);

        return new DifficultyScore(diffId, score, rank, fullComboType, flareRank);
    }

    private static String getDifficultyId(int index, GameMode gameMode) {
        String[] singleDifficulties = {"beginner", "basic", "difficult", "expert", "challenge"};
        String[] doubleDifficulties = {"basic", "difficult", "expert", "challenge"};
        String[] difficulties = gameMode == GameMode.SINGLE ? singleDifficulties : doubleDifficulties;
        return index < difficulties.length ? difficulties[index] : "unknown";
    }

    private static int parseFlareRank(Element column) {
        Element flareRankElement = column.selectFirst("div.data_flarerank img");
        if (flareRankElement == null) return -1;

        String flareRankSrc = flareRankElement.attr("src");
        if (flareRankSrc.contains("flare_nodisp")) return -1;
        if (flareRankSrc.contains("flare_none")) return -1;
        if (flareRankSrc.contains("flare_1")) return 1;
        if (flareRankSrc.contains("flare_2")) return 2;
        if (flareRankSrc.contains("flare_3")) return 3;
        if (flareRankSrc.contains("flare_4")) return 4;
        if (flareRankSrc.contains("flare_5")) return 5;
        if (flareRankSrc.contains("flare_6")) return 6;
        if (flareRankSrc.contains("flare_7")) return 7;
        if (flareRankSrc.contains("flare_8")) return 8;
        if (flareRankSrc.contains("flare_9")) return 9;
        if (flareRankSrc.contains("flare_ex")) return 10;
        return 0;
    }

    private static MusicRank getRank(Element rankElement, int score) {
        if (rankElement != null && rankElement.attr("src").contains("rank_s_e")) {
            return MusicRank.E;
        }
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

        Element fcElement = column.selectFirst("div.data_clearkind img");
        if (fcElement == null) return FullComboType.None;

        String fcSrc = fcElement.attr("src");
        if (fcSrc.contains("cl_marv")) return FullComboType.MerverousFullCombo;
        if (fcSrc.contains("cl_perf")) return FullComboType.PerfectFullCombo;
        if (fcSrc.contains("cl_great")) return FullComboType.FullCombo;
        if (fcSrc.contains("cl_good")) return FullComboType.GoodFullCombo;
        if (fcSrc.contains("cl_li4clear")) return FullComboType.Life4;
        return FullComboType.None;
    }
}

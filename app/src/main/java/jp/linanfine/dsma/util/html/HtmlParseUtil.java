package jp.linanfine.dsma.util.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.WebMusicId;
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

    public static List<MusicEntry> parseMusicListForWorld(String src) {
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

        return musicEntries;
    }

    public static ScoreData parseMusicDetailForWorld(String src, WebMusicId webMusicId) throws ParseException {
        Document doc = Jsoup.parse(src);
        ScoreData sd = new ScoreData();

        // タイトルの確認
        Element titleElement = doc.select("table#music_info td").last();
        if (titleElement == null) {
            throw new ParseException("Title element not found");
        }
        String fullTitle = titleElement.html().trim();
        String[] titleComponents = fullTitle.split("<br>");
        String title = titleComponents[0].trim();

        if (!title.equals(webMusicId.titleOnWebPage)) {
            throw new ParseException("Music ID mismatch: " + webMusicId.titleOnWebPage + " vs " + title);
        }

        // "NO PLAY..." の確認
        if (doc.text().contains("NO PLAY...")) {
            return new ScoreData();
        }

        int score = parseScoreForDetail(doc);
        sd.Score = score;
        sd.Rank = parseRankForDetail(doc, score);
        sd.MaxCombo = parseMaxComboForDetail(doc);
        sd.FullComboType = parseFullComboTypeForDetail(doc);
        sd.PlayCount = parsePlayCountForDetail(doc);
        sd.ClearCount = parseClearCountForDetail(doc);
        sd.FlareRank = parseFlareRankForDetail(doc);

        return sd;
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

        List<DifficultyScore> scores = new ArrayList<>();
        Elements difficultyColumns = row.select("td.rank");
        for (int i = 0; i < difficultyColumns.size(); i++) {
            Element column = difficultyColumns.get(i);
            DifficultyScore score = parseDifficultyScore(column, i, gameMode);
            scores.add(score);
        }

        return new MusicEntry(musicName, scores);
    }

    private static DifficultyScore parseDifficultyScore(Element column, int index, GameMode gameMode) {
        String diffId = getDifficultyId(index, gameMode);
        Element scoreElement = column.selectFirst("div.data_score");
        if (scoreElement == null)
            return new DifficultyScore(diffId, 0, MusicRank.Noplay, FullComboType.None, -1);
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
        if (flareRankSrc.contains("flare_nodisp")) return determineFlareRankFromSkill(column);
        if (flareRankSrc.contains("flare_none")) return determineFlareRankFromSkill(column);
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

        return -1;
    }

    private static int determineFlareRankFromSkill(Element column) {
        Element flareSkillElement = column.selectFirst("div.data_flareskill");
        if (flareSkillElement != null) {
            String flareSkillText = flareSkillElement.text().trim();
            if (!flareSkillText.equals("---")) {
                try {
                    int flareSkill = Integer.parseInt(flareSkillText);
                    if (flareSkill > 0) {
                        return 0; // フレアスキルが0より大きい場合、フレアランクは0
                    }
                } catch (NumberFormatException e) {
                    // フレアスキルの解析に失敗した場合は無視
                }
            }
        }
        return -1; // フレアスキルがない、または0以下の場合、フレアランクは-1
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

    private static MusicEntry parseMusicEntry(Element row) {
        Element titleLink = row.selectFirst("td a");
        if (titleLink == null) return null;

        String musicName = titleLink.text().trim();

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

    private static MusicRank parseRankForDetail(Document doc, int score) throws ParseException {
        Element rankElement = doc.select("th:contains(ハイスコア時のランク) + td").first();
        if (rankElement == null) {
            throw new ParseException("Rank element not found");
        }
        String rankText = rankElement.text();

        return getRank(score, rankText.equals("E"));
    }

    private static int parseScoreForDetail(Document doc) throws ParseException {
        Elements thElements = doc.select("th");
        for (Element th : thElements) {
            if (th.text().trim().equals("ハイスコア")) {
                Element scoreElement = th.nextElementSibling();
                if (scoreElement != null) {
                    String scoreText = scoreElement.text();
                    try {
                        return Integer.parseInt(scoreText);
                    } catch (NumberFormatException e) {
                        throw new ParseException("Invalid score format: " + scoreText);
                    }
                }
            }
        }
        throw new ParseException("Score element not found");
    }

    private static int parseMaxComboForDetail(Document doc) throws ParseException {
        Element comboElement = doc.select("th:contains(最大コンボ数) + td").first();
        if (comboElement == null) {
            throw new ParseException("Max combo element not found");
        }
        String comboText = comboElement.text();
        try {
            return Integer.parseInt(comboText);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid max combo format: " + comboText);
        }
    }

    private static FullComboType parseFullComboTypeForDetail(Document doc) {
        Elements fcElements = doc.select("#clear_detail_table tr[id^='fc_']");
        for (Element element : fcElements) {
            String fcTypeText = element.select("th").text().trim();
            String fcCountText = element.select("td").text().trim();
            int fcCount = 0;

            try {
                fcCount = Integer.parseInt(fcCountText);
            } catch (NumberFormatException ignored) {
            }

            if (fcCount > 0) {
                switch (fcTypeText) {
                    case "マーベラスフルコンボ":
                        return FullComboType.MerverousFullCombo;
                    case "パーフェクトフルコンボ":
                        return FullComboType.PerfectFullCombo;
                    case "グレートフルコンボ":
                        return FullComboType.FullCombo;
                    case "グッドフルコンボ":
                        return FullComboType.GoodFullCombo;
                }
            }
        }

        Element life4Element = doc.select("#clear_detail_table tr#clear_life4 td").first();
        if (life4Element != null) {
            int life4Count = 0;

            try {
                life4Count = Integer.parseInt(life4Element.text().trim());
            } catch (NumberFormatException ignored) {
            }

            if (life4Count > 0) {
                return FullComboType.Life4;
            }
        }

        return FullComboType.None;
    }

    private static int parsePlayCountForDetail(Document doc) throws ParseException {
        Element playCountElement = doc.select("th:contains(プレー回数) + td").first();
        if (playCountElement == null) {
            throw new ParseException("Play count element not found");
        }
        String playCountText = playCountElement.text();
        try {
            return Integer.parseInt(playCountText);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid play count format: " + playCountText);
        }
    }

    private static int parseClearCountForDetail(Document doc) throws ParseException {
        Element clearCountElement = doc.select("th:contains(クリア回数) + td").first();
        if (clearCountElement == null) {
            throw new ParseException("Clear count element not found");
        }
        String clearCountText = clearCountElement.text();
        try {
            return Integer.parseInt(clearCountText);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid clear count format: " + clearCountText);
        }
    }

    private static int parseFlareRankForDetail(Document doc) throws ParseException {
        Element flareRankElement = doc.select("th:contains(フレアランク) + td").first();
        if (flareRankElement == null) {
            throw new ParseException("Flare rank element not found");
        }
        String flareRankText = flareRankElement.text();
        switch (flareRankText) {
            case "EX":
                return 10;
            case "IX":
                return 9;
            case "VIII":
                return 8;
            case "VII":
                return 7;
            case "VI":
                return 6;
            case "V":
                return 5;
            case "IV":
                return 4;
            case "III":
                return 3;
            case "II":
                return 2;
            case "I":
                return 1;
            default:
                int flareSkill = parseFlareSkillForDetail(doc);
                if (flareSkill > 0) {
                    return 0;
                }
                return -1;
        }
    }

    private static int parseFlareSkillForDetail(Document doc) throws ParseException {
        Element flareSkillElement = doc.select("th:contains(フレアスキル) + td").first();
        if (flareSkillElement == null) {
            throw new ParseException("Flare skill element not found");
        }
        String flareSkillText = flareSkillElement.text().trim();
        if (flareSkillText.equals("---")) {
            return -1;
        }
        try {
            return Integer.parseInt(flareSkillText);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid flare skill format: " + flareSkillText);
        }
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}

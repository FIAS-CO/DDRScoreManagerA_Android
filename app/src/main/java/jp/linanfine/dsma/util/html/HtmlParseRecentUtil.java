package jp.linanfine.dsma.util.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.linanfine.dsma.value.RecentData;
import jp.linanfine.dsma.value._enum.PatternType;

public class HtmlParseRecentUtil {
    public static List<RecentData> parseRecentHTML(String src, Map<String, Integer> localIds) throws ParseException {
        List<RecentData> recent = new ArrayList<>();

        try {
            Document doc = Jsoup.parse(src);
            Elements rows = doc.select("table#data_tbl tr");

            for (int i = 1; i < rows.size(); i++) { // Skip header row
                Element row = rows.get(i);
                RecentData recentData = parseRow(row, localIds);
                if (recentData != null) {
                    recent.add(recentData);
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing HTML: " + e.getMessage());
            throw new ParseException("");
        }

        return recent;
    }

    private static RecentData parseRow(Element row, Map<String, Integer> localIds) {
        try {
            Elements cells = row.select("td");
            if (cells.size() >= 2) {
                Element linkElement = cells.get(0).select("a").first();
                if (linkElement != null) {
                    String href = linkElement.attr("href");
                    String[] components = href.split("[?&]");
                    String webId = null;
                    String style = null;
                    String difficulty = null;

                    for (String component : components) {
                        String[] parts = component.split("=");
                        if (parts.length == 2) {
                            switch (parts[0]) {
                                case "index":
                                    webId = parts[1];
                                    break;
                                case "style":
                                    style = parts[1];
                                    break;
                                case "difficulty":
                                    difficulty = parts[1];
                                    break;
                            }
                        }
                    }

                    if (webId != null && style != null && difficulty != null) {
                        Integer id = localIds.get(webId);
                        PatternType patternType = getPatternType(style, difficulty);
                        if (id != null && patternType != null) {
                            RecentData recentData = new RecentData();
                            recentData.Id = id;
                            recentData.PatternType_ = patternType;
                            return recentData;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing row: " + e.getMessage());
        }
        return null;
    }

    private static PatternType getPatternType(String style, String difficulty) {
        if ("0".equals(style)) {
            switch (difficulty) {
                case "0":
                    return PatternType.bSP;
                case "1":
                    return PatternType.BSP;
                case "2":
                    return PatternType.DSP;
                case "3":
                    return PatternType.ESP;
                case "4":
                    return PatternType.CSP;
            }
        } else if ("1".equals(style)) {
            switch (difficulty) {
                case "1":
                    return PatternType.BDP;
                case "2":
                    return PatternType.DDP;
                case "3":
                    return PatternType.EDP;
                case "4":
                    return PatternType.CDP;
            }
        }
        return null;
    }

    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}
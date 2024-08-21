package jp.linanfine.dsma.util.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.linanfine.dsma.util.html.HtmlParseRecentUtil;
import jp.linanfine.dsma.value.RecentData;
import jp.linanfine.dsma.value._enum.PatternType;

public class HtmlParseRecentUtilTest {
    private Map<String, Integer> localIds;

    @BeforeEach
    public void setUp() {
        localIds = new HashMap<>();
        localIds.put("DdIo9DQ0ddDld99DQdiiqbPP06OI91I0", 1);
        localIds.put("9dD6dlq01qd80loD6OOQb60ql6P6068O", 2);
        localIds.put("l6d0ibbliQPQDdb1lP18D9qbPDi1698b", 3);
    }

    @Test
    public void testParseRecentHTML() throws HtmlParseRecentUtil.ParseException {
        String htmlContent = "<table id=\"data_tbl\" cellspacing=\"0\">\n" +
                "<tr><th>楽曲名</th><th>スタイル<br>難易度</th><th>ランク</th><th style=\"min-width:50px;\">スコア</th><th class=\"date\">プレー日時</th></tr>\n" +
                "<tr>\n" +
                "<td><img src=\"/game/ddr/ddrworld/images/binary_jk.html?img=DdIo9DQ0ddDld99DQdiiqbPP06OI91I0&ddrcode=11078354&kind=2\" class=\"jk\" width=\"30\">\n" +
                "<a href=\"/game/ddr/ddrworld/playdata/music_detail.html?index=DdIo9DQ0ddDld99DQdiiqbPP06OI91I0&style=0&difficulty=3\" class=\"music_info cboxelement\" style=\"\">New Era</a>\n" +
                "</td>\n" +
                "<td class=\"diff expert\"><div class=\"style\">SINGLE</div><div class=\"difficulty\">EXPERT</div></td>\n" +
                "<td class=\"rank\"><img src=\"/game/ddr/ddrworld/images/playdata/rank_s_aa_p.png\"></td>\n" +
                "<td class=\"score\">983240</td>\n" +
                "<td class=\"date\">2024-08-18 20:50:09</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td><img src=\"/game/ddr/ddrworld/images/binary_jk.html?img=9dD6dlq01qd80loD6OOQb60ql6P6068O&ddrcode=11078354&kind=2\" class=\"jk\" width=\"30\">\n" +
                "<a href=\"/game/ddr/ddrworld/playdata/music_detail.html?index=9dD6dlq01qd80loD6OOQb60ql6P6068O&style=0&difficulty=3\" class=\"music_info cboxelement\" style=\"\">Destination</a>\n" +
                "</td>\n" +
                "<td class=\"diff expert\"><div class=\"style\">SINGLE</div><div class=\"difficulty\">EXPERT</div></td>\n" +
                "<td class=\"rank\"><img src=\"/game/ddr/ddrworld/images/playdata/rank_s_aa_p.png\"></td>\n" +
                "<td class=\"score\">951540</td>\n" +
                "<td class=\"date\">2024-08-18 20:42:18</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td><img src=\"/game/ddr/ddrworld/images/binary_jk.html?img=l6d0ibbliQPQDdb1lP18D9qbPDi1698b&ddrcode=11078354&kind=2\" class=\"jk\" width=\"30\">\n" +
                "<a href=\"/game/ddr/ddrworld/playdata/music_detail.html?index=l6d0ibbliQPQDdb1lP18D9qbPDi1698b&style=1&difficulty=4\" class=\"music_info cboxelement\" style=\"\">ÆTHER</a>\n" +
                "</td>\n" +
                "<td class=\"diff challeange\"><div class=\"style\">DOUBLE</div><div class=\"difficulty\">CHALLENGE</div></td>\n" +
                "<td class=\"rank\"><img src=\"/game/ddr/ddrworld/images/playdata/rank_s_e.png\"></td>\n" +
                "<td class=\"score\">940980</td>\n" +
                "<td class=\"date\">2024-08-18 20:38:50</td>\n" +
                "</tr>\n" +
                "</table>";

        List<RecentData> result = HtmlParseRecentUtil.parseRecentHTML(htmlContent, localIds);

        assertEquals(3, result.size(), "3つのRecentDataオブジェクトが作成されるはずです");

        assertEquals(1, result.get(0).Id);
        assertEquals(PatternType.ESP, result.get(0).PatternType_);

        assertEquals(2, result.get(1).Id);
        assertEquals(PatternType.ESP, result.get(1).PatternType_);

        assertEquals(3, result.get(2).Id);
        assertEquals(PatternType.CDP, result.get(2).PatternType_);
    }

    @Test
    public void testParseRecentHTMLWithEmptyData() throws HtmlParseRecentUtil.ParseException {
        String emptyHtml = "<table id='data_tbl'><tr><th>Header</th></tr></table>";

        List<RecentData> result = HtmlParseRecentUtil.parseRecentHTML(emptyHtml, localIds);

        assertTrue(result.isEmpty(), "データが空の場合、空のリストを返すはずです");
    }

    @Test
    public void testParseRecentHTMLWithInvalidData() throws HtmlParseRecentUtil.ParseException {
        String invalidHtml = "<table id='data_tbl'><tr><td>Invalid Data</td></tr></table>";

        List<RecentData> result = HtmlParseRecentUtil.parseRecentHTML(invalidHtml, localIds);

        assertTrue(result.isEmpty(), "データが空の場合、空のリストを返すはずです");
    }
}

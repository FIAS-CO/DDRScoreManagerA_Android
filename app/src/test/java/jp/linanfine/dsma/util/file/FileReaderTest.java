package jp.linanfine.dsma.util.file;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.stream.Collectors;

import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class FileReaderTest {
    private String oldTSVContent;
    private String newTSVContent;
    private String newTSVFlareContent;

    @BeforeEach
    public void setUp() {
        // テストデータファイルを読み込む
        oldTSVContent = readTestFile("OldScoreData.txt");
        newTSVContent = readTestFile("NewScoreData.txt");
        newTSVFlareContent = readTestFile("NewScoreDataFlare.txt");
    }

    private String readTestFile(String fileName) {
        try (InputStream is = getClass().getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            fail("テストデータファイルの読み込みに失敗しました: " + fileName);
            return null;
        }
    }

    @Test
    public void testParseScoreData() {
        TreeMap<Integer, MusicScore> scores = FileReader.loadMusicScoresToTreeMap(newTSVFlareContent);

        assertEquals(1155, scores.size(), "スコアの項目数が1155ではありません");

        MusicScore score233 = scores.get(233);
        assertNotNull(score233, "ID 233 のスコアが存在しません");
        assertEquals(MusicRank.AAA, score233.ESP.Rank);
        assertEquals(999660, score233.ESP.Score);
        assertEquals(FullComboType.PerfectFullCombo, score233.ESP.FullComboType);
        assertEquals(MusicRank.AA, score233.EDP.Rank);
        assertEquals(944400, score233.EDP.Score);
        assertEquals(FullComboType.None, score233.EDP.FullComboType);
        assertEquals(10, score233.bSP.FlareRank);
        assertEquals(7, score233.BSP.FlareRank);
        assertEquals(6, score233.DSP.FlareRank);
        assertEquals(5, score233.ESP.FlareRank);
        assertEquals(4, score233.CSP.FlareRank);
        assertEquals(3, score233.BDP.FlareRank);
        assertEquals(2, score233.DDP.FlareRank);
        assertEquals(1, score233.EDP.FlareRank);
        assertEquals(0, score233.CDP.FlareRank);

        MusicScore score453 = scores.get(453);
        assertNotNull(score453, "ID 453 のスコアが存在しません");
        assertEquals(MusicRank.AAA, score453.ESP.Rank);
        assertEquals(996710, score453.ESP.Score);
        assertEquals(FullComboType.FullCombo, score453.ESP.FullComboType);
        assertEquals(MusicRank.AAA, score453.CSP.Rank);
        assertEquals(999860, score453.CSP.Score);
        assertEquals(FullComboType.PerfectFullCombo, score453.CSP.FullComboType);
        assertEquals(MusicRank.A, score453.DDP.Rank);
        assertEquals(811160, score453.DDP.Score);
        assertEquals(FullComboType.None, score453.DDP.FullComboType);
        assertEquals(MusicRank.AAm, score453.CDP.Rank);
        assertEquals(898950, score453.CDP.Score);
        assertEquals(FullComboType.None, score453.CDP.FullComboType);
        assertEquals(8, score453.EDP.FlareRank);
        assertEquals(9, score453.CDP.FlareRank);
    }

    @Test
    public void testCompareScoreListMethods() {
        TreeMap<Integer, MusicScore> oldScoresFromOldMethod = oldParseScoreData(oldTSVContent);
        TreeMap<Integer, MusicScore> oldScoresFromNewMethod = FileReader.loadMusicScoresToTreeMap(oldTSVContent);
        TreeMap<Integer, MusicScore> newScoresFromNewMethod = FileReader.loadMusicScoresToTreeMap(newTSVContent);

        assertEquals(oldScoresFromOldMethod.size(), oldScoresFromNewMethod.size(), "旧TSVの結果数が一致しません");
        assertEquals(oldScoresFromOldMethod.size(), newScoresFromNewMethod.size(), "新旧TSVの結果数が一致しません");

        for (Integer id : oldScoresFromOldMethod.keySet()) {
            MusicScore oldScore = oldScoresFromOldMethod.get(id);
            MusicScore newScoreOldData = oldScoresFromNewMethod.get(id);
            MusicScore newScoreNewData = newScoresFromNewMethod.get(id);


            assertNotNull(newScoreOldData, "ID " + id + " が新メソッド（旧TSV）の結果に存在しません");
            assertNotNull(newScoreNewData, "ID " + id + " が新メソッド（新TSV）の結果に存在しません");

            assert oldScore != null;
            assertTrue(equalsMusicScore(oldScore, newScoreOldData), "ID " + id + " のスコアが一致しません（旧TSV）");

            // 既存のフィールドの比較
            assertTrue(equalsScoreData(oldScore.bSP, newScoreNewData.bSP), "ID " + id + " の bSP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.BSP, newScoreNewData.BSP), "ID " + id + " の BSP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.ESP, newScoreNewData.ESP), "ID " + id + " の ESP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.CSP, newScoreNewData.CSP), "ID " + id + " の CSP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.BDP, newScoreNewData.BDP), "ID " + id + " の BDP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.DDP, newScoreNewData.DDP), "ID " + id + " の DDP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.EDP, newScoreNewData.EDP), "ID " + id + " の EDP スコアが一致しません");
            assertTrue(equalsScoreData(oldScore.CDP, newScoreNewData.CDP), "ID " + id + " の CDP スコアが一致しません");
        }
    }


    private TreeMap<Integer, MusicScore> oldParseScoreData(String content) {
        TreeMap<Integer, MusicScore> ret = new TreeMap<Integer, MusicScore>();
        String[] lines = content.split("\n");
        for (String str : lines) {
            String[] sp = str.split("\t");
            int id = Integer.parseInt(sp[0]);
            MusicScore ms = new MusicScore();
            int spi = 0;
            ++spi;
            ms.bSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.bSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.bSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.bSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.BSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.BSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.DSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.DSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.ESP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.ESP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.ESP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.ESP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CSP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.CSP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CSP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.CSP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.BDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.BDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.BDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.DDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.DDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.DDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.EDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.EDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.EDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.EDP.MaxCombo = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CDP.Rank =
                    sp[spi].equals("AAA") ? MusicRank.AAA :
                            sp[spi].equals("AA+") ? MusicRank.AAp :
                                    sp[spi].equals("AA") ? MusicRank.AA :
                                            sp[spi].equals("AA-") ? MusicRank.AAm :
                                                    sp[spi].equals("A+") ? MusicRank.Ap :
                                                            sp[spi].equals("A") ? MusicRank.A :
                                                                    sp[spi].equals("A-") ? MusicRank.Am :
                                                                            sp[spi].equals("B+") ? MusicRank.Bp :
                                                                                    sp[spi].equals("B") ? MusicRank.B :
                                                                                            sp[spi].equals("B-") ? MusicRank.Bm :
                                                                                                    sp[spi].equals("C+") ? MusicRank.Cp :
                                                                                                            sp[spi].equals("C") ? MusicRank.C :
                                                                                                                    sp[spi].equals("C-") ? MusicRank.Cm :
                                                                                                                            sp[spi].equals("D+") ? MusicRank.Dp :
                                                                                                                                    sp[spi].equals("D") ? MusicRank.D :
                                                                                                                                            sp[spi].equals("E") ? MusicRank.E :
                                                                                                                                                    MusicRank.Noplay;
            ++spi;
            ms.CDP.Score = Integer.valueOf(sp[spi]);
            ++spi;
            ms.CDP.FullComboType =
                    sp[spi].equals("MerverousFullCombo") ? FullComboType.MerverousFullCombo :
                            sp[spi].equals("PerfectFullCombo") ? FullComboType.PerfectFullCombo :
                                    sp[spi].equals("FullCombo") ? FullComboType.FullCombo :
                                            sp[spi].equals("GoodFullCombo") ? FullComboType.GoodFullCombo :
                                                    sp[spi].equals("Life4") ? FullComboType.Life4 :
                                                            FullComboType.None;
            ++spi;
            ms.CDP.MaxCombo = Integer.valueOf(sp[spi]);
            if (sp.length > spi + 1) {
                ms.bSP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.bSP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.BSP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.BSP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.DSP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.DSP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.ESP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.ESP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.CSP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.CSP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.BDP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.BDP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.DDP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.DDP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.EDP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.EDP.ClearCount = Integer.valueOf(sp[++spi]);
                ms.CDP.PlayCount = Integer.valueOf(sp[++spi]);
                ms.CDP.ClearCount = Integer.valueOf(sp[++spi]);
            }
            ret.put(id, ms);
        }
        return ret;
    }

    private boolean equalsMusicScore(MusicScore lhs, MusicScore rhs) {
        return equalsScoreData(lhs.bSP, rhs.bSP) &&
                equalsScoreData(lhs.BSP, rhs.BSP) &&
                equalsScoreData(lhs.DSP, rhs.DSP) &&
                equalsScoreData(lhs.ESP, rhs.ESP) &&
                equalsScoreData(lhs.CSP, rhs.CSP) &&
                equalsScoreData(lhs.BDP, rhs.BDP) &&
                equalsScoreData(lhs.DDP, rhs.DDP) &&
                equalsScoreData(lhs.EDP, rhs.EDP) &&
                equalsScoreData(lhs.CDP, rhs.CDP);
    }

    private boolean equalsScoreData(ScoreData lhs, ScoreData rhs) {
        return lhs.Rank == rhs.Rank &&
                lhs.Score == rhs.Score &&
                lhs.MaxCombo == rhs.MaxCombo &&
                lhs.FullComboType == rhs.FullComboType &&
                lhs.PlayCount == rhs.PlayCount &&
                lhs.ClearCount == rhs.ClearCount &&
                lhs.FlareRank == rhs.FlareRank;
    }
}
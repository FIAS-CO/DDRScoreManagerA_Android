package jp.linanfine.dsma.util.common;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.TreeMap;

import jp.linanfine.dsma.value.MusicData;
import jp.linanfine.dsma.value.MusicScore;
import jp.linanfine.dsma.value.ScoreData;
import jp.linanfine.dsma.value.UniquePattern;
import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;
import jp.linanfine.dsma.value._enum.PatternType;

public class TextUtil {

    public static String textFromCopyFormat(String format, UniquePattern targetPattern, MusicData targetMusicData, ScoreData targetScoreData) {
        StringBuilder ret = new StringBuilder();
        String[] sps = format.split("%", -1);
        Log.e("hoge", "----" + sps.length);
        for (int index = 0; index < sps.length; ++index) {
            Log.e("hoge", "----" + sps[index]);
            if (index % 2 == 0) {
                ret.append(sps[index]);
            } else if (index == sps.length - 1) {

            } else if (sps[index].length() == 0) {
                ret.append("%");
            } else {
                String[] fms;
                Boolean zero;
                Boolean space;
                boolean comma;
                int num;
                StringBuilder tmp = new StringBuilder();
                switch (sps[index].charAt(0)) {
                    case 't':
                        ret.append(targetMusicData.Name);
                        break;
                    case 'p':

                        fms = sps[index].split(":", -1);
                        switch (targetPattern.Pattern) {
                            case bSP:
                                ret.append(fms.length > 1 ? fms[1] : "b");
                                break;
                            case BSP:
                            case BDP:
                                ret.append(fms.length > 2 ? fms[2] : "B");
                                break;
                            case DSP:
                            case DDP:
                                ret.append(fms.length > 3 ? fms[3] : "D");
                                break;
                            case ESP:
                            case EDP:
                                ret.append(fms.length > 4 ? fms[4] : "E");
                                break;
                            case CSP:
                            case CDP:
                                ret.append(fms.length > 5 ? fms[5] : "C");
                                break;
                        }
                        break;
                    case 'y':
                        fms = sps[index].split(":", -1);
                        switch (targetPattern.Pattern) {
                            case bSP:
                            case BSP:
                            case DSP:
                            case ESP:
                            case CSP:
                                ret.append(fms.length > 1 ? fms[1] : "SP");
                                break;
                            case BDP:
                            case DDP:
                            case EDP:
                            case CDP:
                                ret.append(fms.length > 2 ? fms[2] : "DP");
                                break;
                        }
                        break;
                    case 's':
                        zero = sps[index].contains("0");
                        space = sps[index].contains(" ");
                        comma = sps[index].contains(",");
                        num = targetScoreData.Score;
                        tmp = new StringBuilder();
                        int keta = 0;
                        do {
                            if (comma && (keta == 3 || keta == 6)) {
                                tmp.insert(0, ",");
                            }
                            keta = keta + 1;
                            if (num == 0) {
                                tmp.insert(0, (space ? " " : zero ? "0" : ""));
                            } else {
                                tmp.insert(0, num % 10);
                                num = num / 10;
                            }
                        } while (zero || space ? keta < 7 : num > 0);
                        ret.append(tmp);
                        break;
                    case 'd':
                        zero = sps[index].contains("0");
                        space = sps[index].contains(" ");
                        num = targetMusicData.getDifficulty(targetPattern.Pattern);
                        tmp = new StringBuilder();
                        keta = 0;
                        do {
                            keta = keta + 1;
                            if (num == 0) {
                                tmp.insert(0, (space ? " " : zero ? "0" : ""));
                            } else {
                                tmp.insert(0, num % 10);
                                num = num / 10;
                            }
                        } while (zero || space ? keta < 2 : num > 0);
                        ret.append(tmp);
                        break;
                    case 'c':
                        zero = sps[index].contains("0");
                        space = sps[index].contains(" ");
                        num = targetScoreData.MaxCombo;
                        tmp = new StringBuilder();
                        keta = 0;
                        do {
                            keta = keta + 1;
                            if (num == 0) {
                                tmp.insert(0, (space ? " " : zero ? "0" : ""));
                            } else {
                                tmp.insert(0, num % 10);
                                num = num / 10;
                            }
                        } while (zero || space ? keta < 3 : num > 0);
                        ret.append(tmp);
                        break;
                    case 'e':
                        zero = sps[index].contains("0");
                        space = sps[index].contains(" ");
                        num = targetScoreData.ClearCount;
                        tmp = new StringBuilder();
                        keta = 0;
                        do {
                            keta = keta + 1;
                            if (num == 0) {
                                tmp.insert(0, (space ? " " : zero ? "0" : ""));
                            } else {
                                tmp.insert(0, num % 10);
                                num = num / 10;
                            }
                        } while (zero || space ? keta < 4 : num > 0);
                        ret.append(tmp);
                        break;
                    case 'a':
                        zero = sps[index].contains("0");
                        space = sps[index].contains(" ");
                        num = targetScoreData.PlayCount;
                        tmp = new StringBuilder();
                        keta = 0;
                        do {
                            keta = keta + 1;
                            if (num == 0) {
                                tmp.insert(0, (space ? " " : zero ? "0" : ""));
                            } else {
                                tmp.insert(0, num % 10);
                                num = num / 10;
                            }
                        } while (zero || space ? keta < 4 : num > 0);
                        ret.append(tmp);
                        break;
                    case 'l':
                        fms = sps[index].split(":", -1);
                        switch (targetScoreData.Rank) {
                            case AAA:
                                ret.append(fms.length > 1 ? fms[1] : "AAA");
                                break;
                            case AAp:
                                ret.append(fms.length > 2 ? fms[2] : "AA+");
                                break;
                            case AA:
                                ret.append(fms.length > 3 ? fms[3] : "AA");
                                break;
                            case AAm:
                                ret.append(fms.length > 4 ? fms[4] : "AA-");
                                break;
                            case Ap:
                                ret.append(fms.length > 5 ? fms[5] : "A+");
                                break;
                            case A:
                                ret.append(fms.length > 6 ? fms[6] : "A");
                                break;
                            case Am:
                                ret.append(fms.length > 7 ? fms[7] : "A-");
                                break;
                            case Bp:
                                ret.append(fms.length > 8 ? fms[8] : "B+");
                                break;
                            case B:
                                ret.append(fms.length > 9 ? fms[9] : "B");
                                break;
                            case Bm:
                                ret.append(fms.length > 10 ? fms[10] : "B-");
                                break;
                            case Cp:
                                ret.append(fms.length > 11 ? fms[11] : "C+");
                                break;
                            case C:
                                ret.append(fms.length > 12 ? fms[12] : "C");
                                break;
                            case Cm:
                                ret.append(fms.length > 13 ? fms[13] : "C-");
                                break;
                            case Dp:
                                ret.append(fms.length > 14 ? fms[14] : "D+");
                                break;
                            case D:
                                ret.append(fms.length > 15 ? fms[15] : "D");
                                break;
                            case E:
                                ret.append(fms.length > 16 ? fms[16] : "E");
                                break;
                            case Noplay:
                                ret.append(fms.length > 17 ? fms[17] : "-");
                                break;
                        }
                        break;
                    case 'f':
                        fms = sps[index].split(":", -1);
                        switch (targetScoreData.FullComboType) {
                            case MerverousFullCombo:
                                ret.append(fms.length > 1 ? fms[1] : "MFC");
                                break;
                            case PerfectFullCombo:
                                ret.append(fms.length > 2 ? fms[2] : "PFC");
                                break;
                            case FullCombo:
                                ret.append(fms.length > 3 ? fms[3] : "FC");
                                break;
                            case GoodFullCombo:
                                ret.append(fms.length > 4 ? fms[4] : "GFC");
                                break;
                            case Life4:
                                ret.append(fms.length > 5 ? fms[5] : "Life4");
                                break;
                            case None:
                                ret.append(fms.length > 6 ? fms[6] : "NoFC");
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return ret.toString();
    }

    // TODO checkLoginStatus に置き換える
    // ログインしていない： 1
    // ログイン済みでエラーなし： 0
    // 不明： -1
    public static int checkLoggedIn(String src) {
        //Log.e("DATA", src);
        final String cmpNoLoginCheck = "<div class=\"name_str\">---</div>";
        return src.contains(cmpNoLoginCheck) ? 1 : isLoginForm(src) ? 1 : 0;
    }

    public static LoginStatus checkLoginStatus(String src) {
        int status = checkLoggedIn(src);

        return LoginStatus.fromInt(status);
    }

    public enum LoginStatus {
        LOGGED_IN(0),
        NOT_LOGGED_IN(1),
        UNKNOWN(-1);

        private final int status;

        LoginStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return this.status;
        }

        public static LoginStatus fromInt(int status) {
            for (LoginStatus ls : LoginStatus.values()) {
                if (ls.getStatus() == status) {
                    return ls;
                }
            }
            return UNKNOWN; // 既定の値として UNKNOWN を返す
        }
    }

    public static boolean isLoginForm(String src) {
        String cmpLoginForm = "otp.act.10";
        return src.contains(cmpLoginForm);
    }

    public static String get10646Reference(String src) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < src.length(); ++i) {
            int code = src.charAt(i);
            sb.append("&#");
            sb.append(code);
            sb.append(";");
        }
        return sb.toString();
    }

    public static String getScoreBackupText(int id, TreeMap<Integer, MusicScore> scores) {
        StringBuilder sb = new StringBuilder();

        sb.append(id);
        sb.append("\t");
        MusicScore score = scores.get(id);
        sb.append(score.bSP.Rank.toString());
        sb.append("\t");
        sb.append(score.bSP.Score);
        sb.append("\t");
        sb.append(score.bSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.bSP.MaxCombo);
        sb.append("\t");
        sb.append(score.BSP.Rank.toString());
        sb.append("\t");
        sb.append(score.BSP.Score);
        sb.append("\t");
        sb.append(score.BSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.BSP.MaxCombo);
        sb.append("\t");
        sb.append(score.DSP.Rank.toString());
        sb.append("\t");
        sb.append(score.DSP.Score);
        sb.append("\t");
        sb.append(score.DSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.DSP.MaxCombo);
        sb.append("\t");
        sb.append(score.ESP.Rank.toString());
        sb.append("\t");
        sb.append(score.ESP.Score);
        sb.append("\t");
        sb.append(score.ESP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.ESP.MaxCombo);
        sb.append("\t");
        sb.append(score.CSP.Rank.toString());
        sb.append("\t");
        sb.append(score.CSP.Score);
        sb.append("\t");
        sb.append(score.CSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.CSP.MaxCombo);
        sb.append("\t");
        sb.append(score.BDP.Rank.toString());
        sb.append("\t");
        sb.append(score.BDP.Score);
        sb.append("\t");
        sb.append(score.BDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.BDP.MaxCombo);
        sb.append("\t");
        sb.append(score.DDP.Rank.toString());
        sb.append("\t");
        sb.append(score.DDP.Score);
        sb.append("\t");
        sb.append(score.DDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.DDP.MaxCombo);
        sb.append("\t");
        sb.append(score.EDP.Rank.toString());
        sb.append("\t");
        sb.append(score.EDP.Score);
        sb.append("\t");
        sb.append(score.EDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.EDP.MaxCombo);
        sb.append("\t");
        sb.append(score.CDP.Rank.toString());
        sb.append("\t");
        sb.append(score.CDP.Score);
        sb.append("\t");
        sb.append(score.CDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.CDP.MaxCombo);
        sb.append("\t");
        sb.append(score.bSP.PlayCount);
        sb.append("\t");
        sb.append(score.bSP.ClearCount);
        sb.append("\t");
        sb.append(score.BSP.PlayCount);
        sb.append("\t");
        sb.append(score.BSP.ClearCount);
        sb.append("\t");
        sb.append(score.DSP.PlayCount);
        sb.append("\t");
        sb.append(score.DSP.ClearCount);
        sb.append("\t");
        sb.append(score.ESP.PlayCount);
        sb.append("\t");
        sb.append(score.ESP.ClearCount);
        sb.append("\t");
        sb.append(score.CSP.PlayCount);
        sb.append("\t");
        sb.append(score.CSP.ClearCount);
        sb.append("\t");
        sb.append(score.BDP.PlayCount);
        sb.append("\t");
        sb.append(score.BDP.ClearCount);
        sb.append("\t");
        sb.append(score.DDP.PlayCount);
        sb.append("\t");
        sb.append(score.DDP.ClearCount);
        sb.append("\t");
        sb.append(score.EDP.PlayCount);
        sb.append("\t");
        sb.append(score.EDP.ClearCount);
        sb.append("\t");
        sb.append(score.CDP.PlayCount);
        sb.append("\t");
        sb.append(score.CDP.ClearCount);

        // フレアランクの追加
        sb.append("\t");
        sb.append(score.bSP.FlareRank);
        sb.append("\t");
        sb.append(score.BSP.FlareRank);
        sb.append("\t");
        sb.append(score.DSP.FlareRank);
        sb.append("\t");
        sb.append(score.ESP.FlareRank);
        sb.append("\t");
        sb.append(score.CSP.FlareRank);
        sb.append("\t");
        sb.append(score.BDP.FlareRank);
        sb.append("\t");
        sb.append(score.DDP.FlareRank);
        sb.append("\t");
        sb.append(score.EDP.FlareRank);
        sb.append("\t");
        sb.append(score.CDP.FlareRank);

        return sb.toString();
    }

    public static String getSaExportText(int id, TreeMap<Integer, MusicScore> scores) {
        StringBuilder sb = new StringBuilder();

        sb.append(id);
        sb.append("\t");
        MusicScore score = scores.get(id);
        sb.append(score.bSP.Rank.toString());
        sb.append("\t");
        sb.append(score.bSP.Score);
        sb.append("\t");
        sb.append(score.bSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.bSP.MaxCombo);
        sb.append("\t");
        sb.append(score.BSP.Rank.toString());
        sb.append("\t");
        sb.append(score.BSP.Score);
        sb.append("\t");
        sb.append(score.BSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.BSP.MaxCombo);
        sb.append("\t");
        sb.append(score.DSP.Rank.toString());
        sb.append("\t");
        sb.append(score.DSP.Score);
        sb.append("\t");
        sb.append(score.DSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.DSP.MaxCombo);
        sb.append("\t");
        sb.append(score.ESP.Rank.toString());
        sb.append("\t");
        sb.append(score.ESP.Score);
        sb.append("\t");
        sb.append(score.ESP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.ESP.MaxCombo);
        sb.append("\t");
        sb.append(score.CSP.Rank.toString());
        sb.append("\t");
        sb.append(score.CSP.Score);
        sb.append("\t");
        sb.append(score.CSP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.CSP.MaxCombo);
        sb.append("\t");
        sb.append(score.BDP.Rank.toString());
        sb.append("\t");
        sb.append(score.BDP.Score);
        sb.append("\t");
        sb.append(score.BDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.BDP.MaxCombo);
        sb.append("\t");
        sb.append(score.DDP.Rank.toString());
        sb.append("\t");
        sb.append(score.DDP.Score);
        sb.append("\t");
        sb.append(score.DDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.DDP.MaxCombo);
        sb.append("\t");
        sb.append(score.EDP.Rank.toString());
        sb.append("\t");
        sb.append(score.EDP.Score);
        sb.append("\t");
        sb.append(score.EDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.EDP.MaxCombo);
        sb.append("\t");
        sb.append(score.CDP.Rank.toString());
        sb.append("\t");
        sb.append(score.CDP.Score);
        sb.append("\t");
        sb.append(score.CDP.FullComboType.toString());
        sb.append("\t");
        sb.append(score.CDP.MaxCombo);

        return sb.toString();
    }

    public static Spanned getScoreText(int score) {
        String scoreText = new DecimalFormat("0,000,000").format(score);
        StringBuilder html = new StringBuilder();
        if (score == 0) {
            html.append("0,000,00");
            html.append("<font color=\"#ffffff\">");
            html.append("0");
            html.append("</font>");
        } else if (score < 100) {
            html.append(scoreText.charAt(0));
            html.append(scoreText.charAt(1));
            html.append(scoreText.charAt(2));
            html.append(scoreText.charAt(3));
            html.append(scoreText.charAt(4));
            html.append(scoreText.charAt(5));
            html.append(scoreText.charAt(6));
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText.charAt(7));
            html.append(scoreText.charAt(8));
            html.append("</font>");
        } else if (score < 1000) {
            html.append(scoreText.charAt(0));
            html.append(scoreText.charAt(1));
            html.append(scoreText.charAt(2));
            html.append(scoreText.charAt(3));
            html.append(scoreText.charAt(4));
            html.append(scoreText.charAt(5));
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText.charAt(6));
            html.append(scoreText.charAt(7));
            html.append(scoreText.charAt(8));
            html.append("</font>");
        } else if (score < 10000) {
            html.append(scoreText.charAt(0));
            html.append(scoreText.charAt(1));
            html.append(scoreText.charAt(2));
            html.append(scoreText.charAt(3));
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText.charAt(4));
            html.append(scoreText.charAt(5));
            html.append(scoreText.charAt(6));
            html.append(scoreText.charAt(7));
            html.append(scoreText.charAt(8));
            html.append("</font>");
        } else if (score < 100000) {
            html.append(scoreText.charAt(0));
            html.append(scoreText.charAt(1));
            html.append(scoreText.charAt(2));
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText.charAt(3));
            html.append(scoreText.charAt(4));
            html.append(scoreText.charAt(5));
            html.append(scoreText.charAt(6));
            html.append(scoreText.charAt(7));
            html.append(scoreText.charAt(8));
            html.append("</font>");
        } else if (score < 1000000) {
            html.append(scoreText.charAt(0));
            html.append(scoreText.charAt(1));
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText.charAt(2));
            html.append(scoreText.charAt(3));
            html.append(scoreText.charAt(4));
            html.append(scoreText.charAt(5));
            html.append(scoreText.charAt(6));
            html.append(scoreText.charAt(7));
            html.append(scoreText.charAt(8));
            html.append("</font>");
        } else {
            html.append("<font color=\"#ffffff\">");
            html.append(scoreText);
            html.append("</font>");
        }
        return Html.fromHtml(html.toString());
    }

    public MusicRank toMusicRank(String rank) {
        switch (rank) {
            case "AAA":
                return MusicRank.AAA;
            case "AA+":
                return MusicRank.AAp;
            case "AA":
                return MusicRank.AA;
            case "AA-":
                return MusicRank.AAm;
            case "A+":
                return MusicRank.Ap;
            case "A":
                return MusicRank.A;
            case "A-":
                return MusicRank.Am;
            case "B+":
                return MusicRank.Bp;
            case "B":
                return MusicRank.B;
            case "B-":
                return MusicRank.Bm;
            case "C+":
                return MusicRank.Cp;
            case "C":
                return MusicRank.C;
            case "C-":
                return MusicRank.Cm;
            case "D+":
                return MusicRank.Dp;
            case "D":
                return MusicRank.D;
            case "E":
                return MusicRank.E;
            default:
                return MusicRank.Noplay;
        }
    }

    public FullComboType toFullComboType(String type) {
        switch (type) {
            case "グッドフルコンボ":
                return FullComboType.GoodFullCombo;
            case "グレートフルコンボ":
                return FullComboType.FullCombo;
            case "パーフェクトフルコンボ":
                return FullComboType.PerfectFullCombo;
            case "マーベラスフルコンボ":
                return FullComboType.MerverousFullCombo;
            default:
                return FullComboType.None;
        }
    }

    public static int patternTypeToInt(PatternType pattern) {

        switch (pattern) {
            case BSP:
                return 1;
            case DSP:
                return 2;
            case ESP:
                return 3;
            case CSP:
                return 4;
            case BDP:
                return 5;
            case DDP:
                return 6;
            case EDP:
                return 7;
            case CDP:
                return 8;
            case bSP:
            default:
                return 0;
        }
        // TODO 以下と同じ動作になっていることを一応テストする
//        return
//                pattern == PatternType.bSP ? 0 :
//                        pattern == PatternType.BSP ? 1 :
//                                pattern == PatternType.DSP ? 2 :
//                                        pattern == PatternType.ESP ? 3 :
//                                                pattern == PatternType.CSP ? 4 :
//                                                        pattern == PatternType.BDP ? 5 :
//                                                                pattern == PatternType.DDP ? 6 :
//                                                                        pattern == PatternType.EDP ? 7 :
//                                                                                pattern == PatternType.CDP ? 8 :
//                                                                                        0;

    }
}

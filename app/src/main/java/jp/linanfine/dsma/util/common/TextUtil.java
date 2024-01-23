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
import jp.linanfine.dsma.value._enum.MusicRank;

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

    public static String escapeWebTitle(String src) {
        return src
                .replaceAll("&amp;", "&")
                .replaceAll("&AElig;", "Æ")
                .replaceAll("&quot;", "\"")
                .replaceAll("&dagger;", "†")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&apos;", "\'")
                .replaceAll("&iexcl;", "¡")
                .replaceAll("&cent;", "¢")
                .replaceAll("&pound;", "£")
                .replaceAll("&curren;", "¤")
                .replaceAll("&yen;", "¥")
                .replaceAll("&brvbar;", "¦")
                .replaceAll("&sect;", "§")
                .replaceAll("&uml;", "¨")
                .replaceAll("&copy;", "©")
                .replaceAll("&ordf;", "ª")
                .replaceAll("&laquo;", "«")
                .replaceAll("&not;", "¬")
                .replaceAll("&reg;", "®")
                .replaceAll("&macr;", "¯")
                .replaceAll("&deg;", "°")
                .replaceAll("&plusmn;", "±")
                .replaceAll("&sup2;", "²")
                .replaceAll("&sup3;", "³")
                .replaceAll("&acute;", "´")
                .replaceAll("&micro;", "µ")
                .replaceAll("&para;", "¶")
                .replaceAll("&middot;", "·")
                .replaceAll("&cedil;", "¸")
                .replaceAll("&sup1;", "¹")
                .replaceAll("&ordm;", "º")
                .replaceAll("&raquo;", "»")
                .replaceAll("&frac14;", "¼")
                .replaceAll("&frac12;", "½")
                .replaceAll("&frac34;", "¾")
                .replaceAll("&iquest;", "¿")
                .replaceAll("&Agrave;", "À")
                .replaceAll("&Aacute;", "Á")
                .replaceAll("&Acirc;", "Â")
                .replaceAll("&Atilde;", "Ã")
                .replaceAll("&Auml;", "Ä")
                .replaceAll("&Aring;", "Å")
                .replaceAll("&AElig;", "Æ")
                .replaceAll("&Ccedil;", "Ç")
                .replaceAll("&Egrave;", "È")
                .replaceAll("&Eacute;", "É")
                .replaceAll("&Ecirc;", "Ê")
                .replaceAll("&Euml;", "Ë")
                .replaceAll("&Igrave;", "Ì")
                .replaceAll("&Iacute;", "Í")
                .replaceAll("&Icirc;", "Î")
                .replaceAll("&Iuml;", "Ï")
                .replaceAll("&ETH;", "Ð")
                .replaceAll("&Ntilde;", "Ñ")
                .replaceAll("&Ograve;", "Ò")
                .replaceAll("&Oacute;", "Ó")
                .replaceAll("&Ocirc;", "Ô")
                .replaceAll("&Otilde;", "Õ")
                .replaceAll("&Ouml;", "Ö")
                .replaceAll("&times;", "×")
                .replaceAll("&Oslash;", "Ø")
                .replaceAll("&Ugrave;", "Ù")
                .replaceAll("&Uacute;", "Ú")
                .replaceAll("&Ucirc;", "Û")
                .replaceAll("&Uuml;", "Ü")
                .replaceAll("&Yacute;", "Ý")
                .replaceAll("&THORN;", "Þ")
                .replaceAll("&szlig;", "ß")
                .replaceAll("&agrave;", "à")
                .replaceAll("&aacute;", "á")
                .replaceAll("&acirc;", "â")
                .replaceAll("&atilde;", "ã")
                .replaceAll("&auml;", "ä")
                .replaceAll("&aring;", "å")
                .replaceAll("&aelig;", "æ")
                .replaceAll("&ccedil;", "ç")
                .replaceAll("&egrave;", "è")
                .replaceAll("&eacute;", "é")
                .replaceAll("&ecirc;", "ê")
                .replaceAll("&euml;", "ë")
                .replaceAll("&igrave;", "ì")
                .replaceAll("&iacute;", "í")
                .replaceAll("&icirc;", "î")
                .replaceAll("&iuml;", "ï")
                .replaceAll("&eth;", "ð")
                .replaceAll("&ntilde;", "ñ")
                .replaceAll("&ograve;", "ò")
                .replaceAll("&oacute;", "ó")
                .replaceAll("&ocirc;", "ô")
                .replaceAll("&otilde;", "õ")
                .replaceAll("&ouml;", "ö")
                .replaceAll("&divide;", "÷")
                .replaceAll("&oslash;", "ø")
                .replaceAll("&ugrave;", "ù")
                .replaceAll("&uacute;", "ú")
                .replaceAll("&ucirc;", "û")
                .replaceAll("&uuml;", "ü")
                .replaceAll("&yacute;", "ý")
                .replaceAll("&thorn;", "þ")
                .replaceAll("&yuml;", "ÿ")
                .replaceAll("&OElig;", "Œ")
                .replaceAll("&oelig;", "œ")
                .replaceAll("&Scaron;", "Š")
                .replaceAll("&scaron;", "š")
                .replaceAll("&Yuml;", "Ÿ")
                .replaceAll("&fnof;", "ƒ")
                .replaceAll("&circ;", "ˆ")
                .replaceAll("&tilde;", "˜")
                .replaceAll("&Alpha;", "Α")
                .replaceAll("&Beta;", "Β")
                .replaceAll("&Gamma;", "Γ")
                .replaceAll("&Delta;", "Δ")
                .replaceAll("&Epsilon;", "Ε")
                .replaceAll("&Zeta;", "Ζ")
                .replaceAll("&Eta;", "Η")
                .replaceAll("&Theta;", "Θ")
                .replaceAll("&Iota;", "Ι")
                .replaceAll("&Kappa;", "Κ")
                .replaceAll("&Lambda;", "Λ")
                .replaceAll("&Mu;", "Μ")
                .replaceAll("&Nu;", "Ν")
                .replaceAll("&Xi;", "Ξ")
                .replaceAll("&Omicron;", "Ο")
                .replaceAll("&Pi;", "Π")
                .replaceAll("&Rho;", "Ρ")
                .replaceAll("&Sigma;", "Σ")
                .replaceAll("&Tau;", "Τ")
                .replaceAll("&Upsilon;", "Υ")
                .replaceAll("&Phi;", "Φ")
                .replaceAll("&Chi;", "Χ")
                .replaceAll("&Psi;", "Ψ")
                .replaceAll("&Omega;", "Ω")
                .replaceAll("&alpha;", "α")
                .replaceAll("&beta;", "β")
                .replaceAll("&gamma;", "γ")
                .replaceAll("&delta;", "δ")
                .replaceAll("&epsilon;", "ε")
                .replaceAll("&zeta;", "ζ")
                .replaceAll("&eta;", "η")
                .replaceAll("&theta;", "θ")
                .replaceAll("&iota;", "ι")
                .replaceAll("&kappa;", "κ")
                .replaceAll("&lambda;", "λ")
                .replaceAll("&mu;", "μ")
                .replaceAll("&nu;", "ν")
                .replaceAll("&xi;", "ξ")
                .replaceAll("&omicron;", "ο")
                .replaceAll("&pi;", "π")
                .replaceAll("&rho;", "ρ")
                .replaceAll("&sigmaf;", "ς")
                .replaceAll("&sigma;", "σ")
                .replaceAll("&tau;", "τ")
                .replaceAll("&upsilon;", "υ")
                .replaceAll("&phi;", "φ")
                .replaceAll("&chi;", "χ")
                .replaceAll("&psi;", "ψ")
                .replaceAll("&omega;", "ω")
                .replaceAll("&thetasym;", "ϑ")
                .replaceAll("&upsih;", "ϒ")
                .replaceAll("&piv;", "ϖ")
                .replaceAll("&ndash;", "–")
                .replaceAll("&mdash;", "—")
                .replaceAll("&lsquo;", "‘")
                .replaceAll("&rsquo;", "’")
                .replaceAll("&sbquo;", "‚")
                .replaceAll("&ldquo;", "“")
                .replaceAll("&rdquo;", "”")
                .replaceAll("&bdquo;", "„")
                .replaceAll("&dagger;", "†")
                .replaceAll("&Dagger;", "‡")
                .replaceAll("&bull;", "•")
                .replaceAll("&hellip;", "…")
                .replaceAll("&permil;", "‰")
                .replaceAll("&prime;", "′")
                .replaceAll("&Prime;", "″")
                .replaceAll("&lsaquo;", "‹")
                .replaceAll("&rsaquo;", "›")
                .replaceAll("&oline;", "‾")
                .replaceAll("&frasl;", "⁄")
                .replaceAll("&euro;", "€")
                .replaceAll("&image;", "ℑ")
                .replaceAll("&weierp;", "℘")
                .replaceAll("&real;", "ℜ")
                .replaceAll("&trade;", "™")
                .replaceAll("&alefsym;", "ℵ")
                .replaceAll("&larr;", "←")
                .replaceAll("&uarr;", "↑")
                .replaceAll("&rarr;", "→")
                .replaceAll("&darr;", "↓")
                .replaceAll("&harr;", "↔")
                .replaceAll("&crarr;", "↵")
                .replaceAll("&lArr;", "⇐")
                .replaceAll("&uArr;", "⇑")
                .replaceAll("&rArr;", "⇒")
                .replaceAll("&dArr;", "⇓")
                .replaceAll("&hArr;", "⇔")
                .replaceAll("&forall;", "∀")
                .replaceAll("&part;", "∂")
                .replaceAll("&exist;", "∃")
                .replaceAll("&empty;", "∅")
                .replaceAll("&nabla;", "∇")
                .replaceAll("&isin;", "∈")
                .replaceAll("&notin;", "∉")
                .replaceAll("&ni;", "∋")
                .replaceAll("&prod;", "∏")
                .replaceAll("&sum;", "∑")
                .replaceAll("&minus;", "−")
                .replaceAll("&lowast;", "∗")
                .replaceAll("&radic;", "√")
                .replaceAll("&prop;", "∝")
                .replaceAll("&infin;", "∞")
                .replaceAll("&ang;", "∠")
                .replaceAll("&and;", "∧")
                .replaceAll("&or;", "∨")
                .replaceAll("&cap;", "∩")
                .replaceAll("&cup;", "∪")
                .replaceAll("&int;", "∫")
                .replaceAll("&there4;", "∴")
                .replaceAll("&sim;", "∼")
                .replaceAll("&cong;", "≅")
                .replaceAll("&asymp;", "≈")
                .replaceAll("&ne;", "≠")
                .replaceAll("&equiv;", "≡")
                .replaceAll("&le;", "≤")
                .replaceAll("&ge;", "≥")
                .replaceAll("&sub;", "⊂")
                .replaceAll("&sup;", "⊃")
                .replaceAll("&nsub;", "⊄")
                .replaceAll("&sube;", "⊆")
                .replaceAll("&supe;", "⊇")
                .replaceAll("&oplus;", "⊕")
                .replaceAll("&otimes;", "⊗")
                .replaceAll("&perp;", "⊥")
                .replaceAll("&sdot;", "⋅")
                .replaceAll("&lceil;", "⌈")
                .replaceAll("&rceil;", "⌉")
                .replaceAll("&lfloor;", "⌊")
                .replaceAll("&rfloor;", "⌋")
                .replaceAll("&lang;", "⟨")
                .replaceAll("&rang;", "⟩")
                .replaceAll("&loz;", "◊")
                .replaceAll("&spades;", "♠")
                .replaceAll("&clubs;", "♣")
                .replaceAll("&hearts;", "♥")
                .replaceAll("&diams;", "♦")
                .replaceAll("&#9834;", "♪")
                .replaceAll("&", "&amp;")
                .replaceAll("Æ", "&AElig;")
                .replaceAll("\"", "&quot;")
                .replaceAll("†", "&dagger;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\'", "&apos;")
                .replaceAll("¡", "&iexcl;")
                .replaceAll("¢", "&cent;")
                .replaceAll("£", "&pound;")
                .replaceAll("¤", "&curren;")
                .replaceAll("¥", "&yen;")
                .replaceAll("¦", "&brvbar;")
                .replaceAll("§", "&sect;")
                .replaceAll("¨", "&uml;")
                .replaceAll("©", "&copy;")
                .replaceAll("ª", "&ordf;")
                .replaceAll("«", "&laquo;")
                .replaceAll("¬", "&not;")
                .replaceAll("®", "&reg;")
                .replaceAll("¯", "&macr;")
                .replaceAll("°", "&deg;")
                .replaceAll("±", "&plusmn;")
                .replaceAll("²", "&sup2;")
                .replaceAll("³", "&sup3;")
                .replaceAll("´", "&acute;")
                .replaceAll("µ", "&micro;")
                .replaceAll("¶", "&para;")
                .replaceAll("·", "&middot;")
                .replaceAll("¸", "&cedil;")
                .replaceAll("¹", "&sup1;")
                .replaceAll("º", "&ordm;")
                .replaceAll("»", "&raquo;")
                .replaceAll("¼", "&frac14;")
                .replaceAll("½", "&frac12;")
                .replaceAll("¾", "&frac34;")
                .replaceAll("¿", "&iquest;")
                .replaceAll("À", "&Agrave;")
                .replaceAll("Á", "&Aacute;")
                .replaceAll("Â", "&Acirc;")
                .replaceAll("Ã", "&Atilde;")
                .replaceAll("Ä", "&Auml;")
                .replaceAll("Å", "&Aring;")
                .replaceAll("Æ", "&AElig;")
                .replaceAll("Ç", "&Ccedil;")
                .replaceAll("È", "&Egrave;")
                .replaceAll("É", "&Eacute;")
                .replaceAll("Ê", "&Ecirc;")
                .replaceAll("Ë", "&Euml;")
                .replaceAll("Ì", "&Igrave;")
                .replaceAll("Í", "&Iacute;")
                .replaceAll("Î", "&Icirc;")
                .replaceAll("Ï", "&Iuml;")
                .replaceAll("Ð", "&ETH;")
                .replaceAll("Ñ", "&Ntilde;")
                .replaceAll("Ò", "&Ograve;")
                .replaceAll("Ó", "&Oacute;")
                .replaceAll("Ô", "&Ocirc;")
                .replaceAll("Õ", "&Otilde;")
                .replaceAll("Ö", "&Ouml;")
                .replaceAll("×", "&times;")
                .replaceAll("Ø", "&Oslash;")
                .replaceAll("Ù", "&Ugrave;")
                .replaceAll("Ú", "&Uacute;")
                .replaceAll("Û", "&Ucirc;")
                .replaceAll("Ü", "&Uuml;")
                .replaceAll("Ý", "&Yacute;")
                .replaceAll("Þ", "&THORN;")
                .replaceAll("ß", "&szlig;")
                .replaceAll("à", "&agrave;")
                .replaceAll("á", "&aacute;")
                .replaceAll("â", "&acirc;")
                .replaceAll("ã", "&atilde;")
                .replaceAll("ä", "&auml;")
                .replaceAll("å", "&aring;")
                .replaceAll("æ", "&aelig;")
                .replaceAll("ç", "&ccedil;")
                .replaceAll("è", "&egrave;")
                .replaceAll("é", "&eacute;")
                .replaceAll("ê", "&ecirc;")
                .replaceAll("ë", "&euml;")
                .replaceAll("ì", "&igrave;")
                .replaceAll("í", "&iacute;")
                .replaceAll("î", "&icirc;")
                .replaceAll("ï", "&iuml;")
                .replaceAll("ð", "&eth;")
                .replaceAll("ñ", "&ntilde;")
                .replaceAll("ò", "&ograve;")
                .replaceAll("ó", "&oacute;")
                .replaceAll("ô", "&ocirc;")
                .replaceAll("õ", "&otilde;")
                .replaceAll("ö", "&ouml;")
                .replaceAll("÷", "&divide;")
                .replaceAll("ø", "&oslash;")
                .replaceAll("ù", "&ugrave;")
                .replaceAll("ú", "&uacute;")
                .replaceAll("û", "&ucirc;")
                .replaceAll("ü", "&uuml;")
                .replaceAll("ý", "&yacute;")
                .replaceAll("þ", "&thorn;")
                .replaceAll("ÿ", "&yuml;")
                .replaceAll("Œ", "&OElig;")
                .replaceAll("œ", "&oelig;")
                .replaceAll("Š", "&Scaron;")
                .replaceAll("š", "&scaron;")
                .replaceAll("Ÿ", "&Yuml;")
                .replaceAll("ƒ", "&fnof;")
                .replaceAll("ˆ", "&circ;")
                .replaceAll("˜", "&tilde;")
                .replaceAll("Α", "&Alpha;")
                .replaceAll("Β", "&Beta;")
                .replaceAll("Γ", "&Gamma;")
                .replaceAll("Δ", "&Delta;")
                .replaceAll("Ε", "&Epsilon;")
                .replaceAll("Ζ", "&Zeta;")
                .replaceAll("Η", "&Eta;")
                .replaceAll("Θ", "&Theta;")
                .replaceAll("Ι", "&Iota;")
                .replaceAll("Κ", "&Kappa;")
                .replaceAll("Λ", "&Lambda;")
                .replaceAll("Μ", "&Mu;")
                .replaceAll("Ν", "&Nu;")
                .replaceAll("Ξ", "&Xi;")
                .replaceAll("Ο", "&Omicron;")
                .replaceAll("Π", "&Pi;")
                .replaceAll("Ρ", "&Rho;")
                .replaceAll("Σ", "&Sigma;")
                .replaceAll("Τ", "&Tau;")
                .replaceAll("Υ", "&Upsilon;")
                .replaceAll("Φ", "&Phi;")
                .replaceAll("Χ", "&Chi;")
                .replaceAll("Ψ", "&Psi;")
                .replaceAll("Ω", "&Omega;")
                .replaceAll("α", "&alpha;")
                .replaceAll("β", "&beta;")
                .replaceAll("γ", "&gamma;")
                .replaceAll("δ", "&delta;")
                .replaceAll("ε", "&epsilon;")
                .replaceAll("ζ", "&zeta;")
                .replaceAll("η", "&eta;")
                .replaceAll("θ", "&theta;")
                .replaceAll("ι", "&iota;")
                .replaceAll("κ", "&kappa;")
                .replaceAll("λ", "&lambda;")
                .replaceAll("μ", "&mu;")
                .replaceAll("ν", "&nu;")
                .replaceAll("ξ", "&xi;")
                .replaceAll("ο", "&omicron;")
                .replaceAll("π", "&pi;")
                .replaceAll("ρ", "&rho;")
                .replaceAll("ς", "&sigmaf;")
                .replaceAll("σ", "&sigma;")
                .replaceAll("τ", "&tau;")
                .replaceAll("υ", "&upsilon;")
                .replaceAll("φ", "&phi;")
                .replaceAll("χ", "&chi;")
                .replaceAll("ψ", "&psi;")
                .replaceAll("ω", "&omega;")
                .replaceAll("ϑ", "&thetasym;")
                .replaceAll("ϒ", "&upsih;")
                .replaceAll("ϖ", "&piv;")
                .replaceAll("–", "&ndash;")
                .replaceAll("—", "&mdash;")
                .replaceAll("‘", "&lsquo;")
                .replaceAll("’", "&rsquo;")
                .replaceAll("‚", "&sbquo;")
                .replaceAll("“", "&ldquo;")
                .replaceAll("”", "&rdquo;")
                .replaceAll("„", "&bdquo;")
                .replaceAll("†", "&dagger;")
                .replaceAll("‡", "&Dagger;")
                .replaceAll("•", "&bull;")
                .replaceAll("…", "&hellip;")
                .replaceAll("‰", "&permil;")
                .replaceAll("′", "&prime;")
                .replaceAll("″", "&Prime;")
                .replaceAll("‹", "&lsaquo;")
                .replaceAll("›", "&rsaquo;")
                .replaceAll("‾", "&oline;")
                .replaceAll("⁄", "&frasl;")
                .replaceAll("€", "&euro;")
                .replaceAll("ℑ", "&image;")
                .replaceAll("℘", "&weierp;")
                .replaceAll("ℜ", "&real;")
                .replaceAll("™", "&trade;")
                .replaceAll("ℵ", "&alefsym;")
                .replaceAll("←", "&larr;")
                .replaceAll("↑", "&uarr;")
                .replaceAll("→", "&rarr;")
                .replaceAll("↓", "&darr;")
                .replaceAll("↔", "&harr;")
                .replaceAll("↵", "&crarr;")
                .replaceAll("⇐", "&lArr;")
                .replaceAll("⇑", "&uArr;")
                .replaceAll("⇒", "&rArr;")
                .replaceAll("⇓", "&dArr;")
                .replaceAll("⇔", "&hArr;")
                .replaceAll("∀", "&forall;")
                .replaceAll("∂", "&part;")
                .replaceAll("∃", "&exist;")
                .replaceAll("∅", "&empty;")
                .replaceAll("∇", "&nabla;")
                .replaceAll("∈", "&isin;")
                .replaceAll("∉", "&notin;")
                .replaceAll("∋", "&ni;")
                .replaceAll("∏", "&prod;")
                .replaceAll("∑", "&sum;")
                .replaceAll("−", "&minus;")
                .replaceAll("∗", "&lowast;")
                .replaceAll("√", "&radic;")
                .replaceAll("∝", "&prop;")
                .replaceAll("∞", "&infin;")
                .replaceAll("∠", "&ang;")
                .replaceAll("∧", "&and;")
                .replaceAll("∨", "&or;")
                .replaceAll("∩", "&cap;")
                .replaceAll("∪", "&cup;")
                .replaceAll("∫", "&int;")
                .replaceAll("∴", "&there4;")
                .replaceAll("∼", "&sim;")
                .replaceAll("≅", "&cong;")
                .replaceAll("≈", "&asymp;")
                .replaceAll("≠", "&ne;")
                .replaceAll("≡", "&equiv;")
                .replaceAll("≤", "&le;")
                .replaceAll("≥", "&ge;")
                .replaceAll("⊂", "&sub;")
                .replaceAll("⊃", "&sup;")
                .replaceAll("⊄", "&nsub;")
                .replaceAll("⊆", "&sube;")
                .replaceAll("⊇", "&supe;")
                .replaceAll("⊕", "&oplus;")
                .replaceAll("⊗", "&otimes;")
                .replaceAll("⊥", "&perp;")
                .replaceAll("⋅", "&sdot;")
                .replaceAll("⌈", "&lceil;")
                .replaceAll("⌉", "&rceil;")
                .replaceAll("⌊", "&lfloor;")
                .replaceAll("⌋", "&rfloor;")
                .replaceAll("⟨", "&lang;")
                .replaceAll("⟩", "&rang;")
                .replaceAll("◊", "&loz;")
                .replaceAll("♠", "&spades;")
                .replaceAll("♣", "&clubs;")
                .replaceAll("♥", "&hearts;")
                .replaceAll("♦", "&diams;")
                .replaceAll("♪", "&#9834;")
                ;
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
        // sb.append("\t");
        // sb.append("\n");

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
}

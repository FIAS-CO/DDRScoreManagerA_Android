package jp.linanfine.dsma.util.html;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import java.text.DecimalFormat;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class CharSequenceFormatter {
    public static CharSequence formatScore(int score, MusicRank rankData) {
        String scoreText = new DecimalFormat("0,000,000").format(score);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (score == 0) {
            if (rankData != MusicRank.Noplay) {
                builder.append("0,000,00");
                int start = builder.length();
                builder.append("0");
                builder.setSpan(new ForegroundColorSpan(0xFFFFFFFF), start, builder.length(), 0);
            } else {
                builder.append(scoreText);
            }
        } else {
            int whiteStart = scoreText.length();
            if (score < 100) whiteStart = 7;
            else if (score < 1000) whiteStart = 6;
            else if (score < 10000) whiteStart = 4;
            else if (score < 100000) whiteStart = 3;
            else if (score < 1000000) whiteStart = 2;
            else whiteStart = 0;

            builder.append(scoreText);
            if (whiteStart < scoreText.length()) {
                builder.setSpan(new ForegroundColorSpan(0xFFFFFFFF), whiteStart, scoreText.length(), 0);
            }
        }

        return builder;
    }

    public static class FormattedRankAndFC {
        public CharSequence text;
        public int textSize;
    }

    public static FormattedRankAndFC formatRankAndFC(MusicRank rankData, FullComboType fc, float originalFontSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int rankColor;
        float sizeFactor = 1.0f;

        // ランクの処理
        switch (rankData) {
            case Noplay:
                rankColor = 0xFF666666;
                sizeFactor = 3f / 7f;
                break;
            case E:
                rankColor = 0xFF999999;
                break;
            case Dp:
            case D:
                rankColor = 0xFFFF0000;
                break;
            case Cp:
            case C:
            case Cm:
                rankColor = 0xFFFF00FF;
                break;
            case Bp:
            case B:
            case Bm:
                rankColor = 0xFF6666FF;
                break;
            case Ap:
            case A:
            case Am:
                rankColor = 0xFFFFFF00;
                break;
            case AAp:
            case AA:
            case AAm:
                rankColor = 0xFFFFFF66;
                break;
            case AAA:
                rankColor = 0xFFFFFFCC;
                break;
            default:
                rankColor = 0xFFFFFFFF; // デフォルト色
        }

        if (!rankData.toString().isEmpty()) {
            SpannableString rankSpan = new SpannableString(rankData.toString());
            rankSpan.setSpan(new ForegroundColorSpan(rankColor), 0, rankSpan.length(), 0);
            builder.append(rankSpan);
        }

        // FCの処理
        String fcSymbol = "゜";
        int fcColor;
        switch (fc) {
//            fcColor = 0xFFFFFFFF;
//                fcSymbol = "/";  // 特別なシンボルを使用
//            break;
            case PerfectFullCombo:
                fcColor = 0xFFFFFF00;
                break;
            case FullCombo:
                fcColor = 0xFF33FF33;
                break;
            case GoodFullCombo:
                fcColor = 0xFF6699FF;
                break;
            case Life4:
                fcColor = 0xFFFF6633;
                break;
            case MerverousFullCombo:
            default:
                fcColor = 0xFFFFFFFF; // デフォルト色
        }

        SpannableString fcSpan = new SpannableString(fcSymbol);
        fcSpan.setSpan(new ForegroundColorSpan(fcColor), 0, fcSpan.length(), 0);
        builder.append(fcSpan);

        if (sizeFactor != 1.0f) {
            builder.setSpan(new RelativeSizeSpan(sizeFactor), 0, builder.length(), 0);
        }

        FormattedRankAndFC result = new FormattedRankAndFC();
        result.text = builder;
        result.textSize = (int) (originalFontSize * sizeFactor);

        return result;
    }

    public static CharSequence formatLevel(int level) {
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (level < 0) {
            builder.append("?");
            builder.setSpan(new ForegroundColorSpan(0xFF000000), 0, 1, 0);
            builder.append("?");
        } else {
            if (level < 10) {
                builder.append("0");
                builder.setSpan(new ForegroundColorSpan(0xFF000000), 0, 1, 0);
            }
            builder.append(String.valueOf(level));
        }

        return builder;
    }

    public static CharSequence formatRivalScore(int score, MusicRank rankData) {
        String scoreText = new DecimalFormat("0,000,000").format(score);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        if (score == 0) {
            if (rankData != MusicRank.Noplay) {
                builder.append("0,000,00");
                int start = builder.length();
                builder.append("0");
                builder.setSpan(new ForegroundColorSpan(0xFFFFFFFF), start, builder.length(), 0);
            } else {
                builder.append(scoreText);
            }
        } else {
            int whiteStart = scoreText.length();
            if (score < 100) whiteStart = 7;
            else if (score < 1000) whiteStart = 6;
            else if (score < 10000) whiteStart = 4;
            else if (score < 100000) whiteStart = 3;
            else if (score < 1000000) whiteStart = 2;
            else whiteStart = 0;

            builder.append(scoreText);
            if (whiteStart < scoreText.length()) {
                builder.setSpan(new ForegroundColorSpan(0xFFFFFFFF), whiteStart, scoreText.length(), 0);
            }
        }

        return builder;
    }

    public static FormattedRankAndFC formatRivalRankAndFC(MusicRank rankData, FullComboType fc, float originalFontSize) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        int rankColor;
        float sizeFactor = 1.0f;

        // ランクの処理
        switch (rankData) {
            case Noplay:
                rankColor = 0xFF666666;
                sizeFactor = 3f / 7f;
                break;
            case E:
                rankColor = 0xFF999999;
                break;
            case Dp:
            case D:
                rankColor = 0xFFFF0000;
                break;
            case Cp:
            case C:
            case Cm:
                rankColor = 0xFFFF00FF;
                break;
            case Bp:
            case B:
            case Bm:
                rankColor = 0xFF6666FF;
                break;
            case Ap:
            case A:
            case Am:
                rankColor = 0xFFFFFF00;
                break;
            case AAp:
            case AA:
            case AAm:
                rankColor = 0xFFFFFF66;
                break;
            case AAA:
                rankColor = 0xFFFFFFCC;
                break;
            default:
                rankColor = 0xFFFFFFFF;
        }

        builder.append(rankData.toString());
        builder.setSpan(new ForegroundColorSpan(rankColor), 0, builder.length(), 0);

        // FCの処理
        String fcSymbol = "゜";
        int fcColor;
        switch (fc) {
            case PerfectFullCombo:
                fcColor = 0xFFFFFF00;
                break;
            case FullCombo:
                fcColor = 0xFF33FF33;
                break;
            case GoodFullCombo:
                fcColor = 0xFF6699FF;
                break;
            case Life4:
                fcColor = 0xFFFF6633;
                break;
            case MerverousFullCombo:
            default:
                fcColor = 0xFFFFFFFF;
        }

        builder.append(fcSymbol);
        builder.setSpan(new ForegroundColorSpan(fcColor), builder.length() - 1, builder.length(), 0);

        FormattedRankAndFC result = new FormattedRankAndFC();
        result.text = builder;
        result.textSize = (int)(originalFontSize * sizeFactor);

        return result;
    }


    public static CharSequence formatScoreDifference(int myScore, int rScore) {
        int dfscore = myScore - rScore;
        int score = Math.abs(dfscore);
        String scoreText = new DecimalFormat("0,000,000").format(score);
        SpannableStringBuilder builder = new SpannableStringBuilder();

        int color;
        String prefix;
        if (dfscore > 0) {
            color = 0xFF9999FF;
            prefix = "+";
        } else if (dfscore < 0) {
            color = 0xFFFF6666;
            prefix = "-";
        } else {
            color = 0xFFFFFFFF;
            prefix = "+";
        }

        builder.append(prefix);

        if (score == 0) {
            builder.append("0");
        } else if (score < 100) {
            builder.append(scoreText.substring(7));
        } else if (score < 1000) {
            builder.append(scoreText.substring(6));
        } else if (score < 10000) {
            builder.append(scoreText.substring(4));
        } else if (score < 100000) {
            builder.append(scoreText.substring(3));
        } else if (score < 1000000) {
            builder.append(scoreText.substring(2));
        } else {
            builder.append(scoreText);
        }

        builder.setSpan(new ForegroundColorSpan(color), 0, builder.length(), 0);

        return builder;
    }
}

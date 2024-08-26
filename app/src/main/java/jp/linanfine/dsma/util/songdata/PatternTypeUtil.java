package jp.linanfine.dsma.util.songdata;

import jp.linanfine.dsma.value._enum.PatternType;

public class PatternTypeUtil {

    public static int getPatternIntForA3(PatternType pattern) {
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
    }

    public static int getPatternIntForWorld(PatternType patternType) {
        int patternValue;

        switch (patternType) {
            case bSP:
                patternValue = 0;
                break;
            case BSP:
            case BDP:
                patternValue = 1;
                break;
            case DSP:
            case DDP:
                patternValue = 2;
                break;
            case ESP:
            case EDP:
                patternValue = 3;
                break;
            case CSP:
            case CDP:
                patternValue = 4;
                break;
            default:
                patternValue = -1; // デフォルト値としてエラー処理のための値を設定
                break;
        }

        return patternValue;
    }

    public static int getStyleInt(PatternType patternType) {
        int styleValue;

        switch (patternType) {
            case bSP:
            case BSP:
            case DSP:
            case ESP:
            case CSP:
                styleValue = 0;
                break;
            case BDP:
            case DDP:
            case EDP:
            case CDP:
                styleValue = 1;
                break;
            default:
                styleValue = -1; // エラー処理のためにデフォルト値を設定
                break;
        }

        return styleValue;
    }
}

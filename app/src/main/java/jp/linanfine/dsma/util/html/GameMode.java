package jp.linanfine.dsma.util.html;

public enum GameMode {
    SINGLE,
    DOUBLE;

    public static GameMode fromString(String text) {
        for (GameMode mode : GameMode.values()) {
            if (mode.name().equalsIgnoreCase(text)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
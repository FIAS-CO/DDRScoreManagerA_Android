package jp.linanfine.dsma.util.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicEntry {
    private final String musicName;
    private final List<DifficultyScore> scores;

    public MusicEntry(String musicName, List<DifficultyScore> scores) {
        this.musicName = musicName;
        this.scores = new ArrayList<>(scores);
    }

    public String getMusicName() {
        return musicName;
    }

    public List<DifficultyScore> getScores() {
        return new ArrayList<>(scores);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicEntry that = (MusicEntry) o;
        return Objects.equals(musicName, that.musicName) &&
                Objects.equals(scores, that.scores);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicName, scores);
    }
}
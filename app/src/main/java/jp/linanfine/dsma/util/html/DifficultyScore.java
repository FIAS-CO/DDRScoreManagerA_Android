package jp.linanfine.dsma.util.html;

import java.util.Objects;

import jp.linanfine.dsma.value._enum.FullComboType;
import jp.linanfine.dsma.value._enum.MusicRank;

public class DifficultyScore {
    private final String difficultyId;
    private final int score;
    private final MusicRank rank;
    private final FullComboType fullComboType;
    private int flareRank = -1;

    public DifficultyScore(String difficultyId, int score, MusicRank rank, FullComboType fullComboType, int flareRank) {
        this(difficultyId, score,rank,fullComboType);
        this.flareRank = flareRank;
    }
    public DifficultyScore(String difficultyId, int score, MusicRank rank, FullComboType fullComboType) {
        this.difficultyId = difficultyId;
        this.score = score;
        this.rank = rank;
        this.fullComboType = fullComboType;
    }

    public String getDifficultyId() {
        return difficultyId;
    }

    public int getScore() {
        return score;
    }

    public MusicRank getRank() {
        return rank;
    }

    public FullComboType getFullComboType() {
        return fullComboType;
    }

    public int getFlareRank() {
        return flareRank;
    }

    @Override
    public String toString() {
        return "DifficultyScore{" +
                "difficultyId='" + difficultyId + '\'' +
                ", score=" + score +
                ", rank=" + rank +
                ", fullComboType=" + fullComboType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DifficultyScore that = (DifficultyScore) o;
        return score == that.score &&
                Objects.equals(difficultyId, that.difficultyId) &&
                rank == that.rank &&
                fullComboType == that.fullComboType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(difficultyId, score, rank, fullComboType);
    }
}
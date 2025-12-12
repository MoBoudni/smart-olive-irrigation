package de.smartolive.irrigation.domain.valueobject;

import java.util.Objects;

/**
 * Value Object: Repräsentiert den Soll-Bodenfeuchtebereich einer Parzelle.
 * Beispiel: unterer Grenzwert 30%, oberer Grenzwert 60%.
 * Immutable und mit fachlicher Validierung.
 */
public class MoistureRange {

    private final double lower;  // in Prozent (0–100)
    private final double upper;  // in Prozent (0–100)

    public MoistureRange(double lower, double upper) {
        if (lower < 0 || lower > 100) {
            throw new IllegalArgumentException("Untergrenze muss zwischen 0 und 100 liegen.");
        }
        if (upper < 0 || upper > 100) {
            throw new IllegalArgumentException("Obergrenze muss zwischen 0 und 100 liegen.");
        }
        if (lower >= upper) {
            throw new IllegalArgumentException("Untergrenze muss kleiner als Obergrenze sein.");
        }
        this.lower = lower;
        this.upper = upper;
    }

    /** Fachliche Methode: Prüft, ob der Bereich gültig ist (lower < upper und im 0–100-Bereich) */
    public boolean isValid() {
        return lower < upper && lower >= 0 && upper <= 100;
    }

    public boolean contains(double currentMoisture) {
        return currentMoisture >= lower && currentMoisture <= upper;
    }

    public boolean isBelow(double currentMoisture) {
        return currentMoisture < lower;
    }

    public boolean isAbove(double currentMoisture) {
        return currentMoisture > upper;
    }

    // Getter
    public double getLower() {
        return lower;
    }

    public double getUpper() {
        return upper;
    }

    // equals & hashCode für Value Object Semantik
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoistureRange that)) return false;
        return Double.compare(that.lower, lower) == 0 && Double.compare(that.upper, upper) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lower, upper);
    }

    @Override
    public String toString() {
        return String.format("%.0f%% – %.0f%%", lower, upper);
    }
}
package de.smartolive.irrigation.domain.valueobject;

import java.util.Objects;

/**
 * Value Object: Repräsentiert einen Feuchtigkeitslevel mit Kategorisierung
 */
public class MoistureLevel {

    private final double percent;
    private final MoistureCategory category;

    public MoistureLevel(double percent) {
        if (percent < 0 || percent > 100) {
            throw new IllegalArgumentException("Feuchtigkeit muss zwischen 0-100% liegen");
        }
        this.percent = percent;
        this.category = determineCategory(percent);
    }

    private MoistureCategory determineCategory(double percent) {
        if (percent < 20) return MoistureCategory.SEHR_TROCKEN;
        if (percent < 40) return MoistureCategory.TROCKEN;
        if (percent < 60) return MoistureCategory.OPTIMAL;
        if (percent < 80) return MoistureCategory.FEUCHT;
        return MoistureCategory.SEHR_FEUCHT;
    }

    // Fachliche Methoden

    public boolean isTooDry() {
        return category == MoistureCategory.SEHR_TROCKEN || category == MoistureCategory.TROCKEN;
    }

    public boolean isTooWet() {
        return category == MoistureCategory.SEHR_FEUCHT;
    }

    public boolean isOptimal() {
        return category == MoistureCategory.OPTIMAL;
    }

    public String getRecommendation() {
        switch (category) {
            case SEHR_TROCKEN: return "Dringend bewässern!";
            case TROCKEN: return "Bewässern empfohlen";
            case OPTIMAL: return "Keine Bewässerung nötig";
            case FEUCHT: return "Bewässerung reduzieren";
            case SEHR_FEUCHT: return "Keine Bewässerung - Boden ist gesättigt";
            default: return "Unbekannt";
        }
    }

    // Getter
    public double getPercent() { return percent; }
    public MoistureCategory getCategory() { return category; }

    // equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoistureLevel)) return false;
        MoistureLevel that = (MoistureLevel) o;
        return Double.compare(that.percent, percent) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(percent);
    }

    @Override
    public String toString() {
        return String.format("%.1f%% (%s)", percent, category.getBeschreibung());
    }
}


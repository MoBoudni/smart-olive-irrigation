package de.smartolive.irrigation.domain.model;

/**
 * Art der Bewässerung
 */
public enum IrrigationType {
    AUTOMATIC("Automatisch", "🟢"),
    MANUAL("Manuell", "👤"),
    SCHEDULED("Zeitgesteuert", "⏰"),
    FALLBACK("Fallback", "⚫"),
    TEST("Test", "🧪");

    private final String beschreibung;
    private final String symbol;

    IrrigationType(String beschreibung, String symbol) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getSymbol() {
        return symbol;
    }
}
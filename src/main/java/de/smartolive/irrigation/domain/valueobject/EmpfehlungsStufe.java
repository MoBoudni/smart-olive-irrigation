package de.smartolive.irrigation.domain.valueobject;

/**
 * Enum: Stufe der Bewässerungsempfehlung
 */
public enum EmpfehlungsStufe {
    /**
     * Keine Bewässerung erforderlich/empfohlen
     */
    KEINE("Keine Bewässerung", "🟢"),

    /**
     * Normale Bewässerung empfohlen
     */
    NORMAL("Normale Bewässerung", "🟡"),

    /**
     * Erhöhter Wasserbedarf (Hitze, Trockenheit)
     */
    ERHOEHT("Erhöhter Bedarf", "🟠"),

    /**
     * Kritischer Zustand - sofortige Bewässerung
     */
    KRITISCH("Kritischer Zustand", "🔴"),

    /**
     * Fallback: Bei fehlenden Sensordaten
     */
    FALLBACK("Fallback-Modus", "⚫");

    private final String beschreibung;
    private final String symbol;

    EmpfehlungsStufe(String beschreibung, String symbol) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isKritisch() {
        return this == KRITISCH || this == FALLBACK;
    }
}
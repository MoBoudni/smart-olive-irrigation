package de.smartolive.irrigation.domain.valueobject;

/**
 * Kategorien für Feuchtigkeitslevel
 */
public enum MoistureCategory {
    SEHR_TROCKEN("Sehr trocken", "🔴", 0xE74C3C),
    TROCKEN("Trocken", "🟠", 0xE67E22),
    OPTIMAL("Optimal", "🟢", 0x2ECC71),
    FEUCHT("Feucht", "🔵", 0x3498DB),
    SEHR_FEUCHT("Sehr feucht", "🟣", 0x9B59B6);

    private final String beschreibung;
    private final String symbol;
    private final int colorHex;

    MoistureCategory(String beschreibung, String symbol, int colorHex) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
        this.colorHex = colorHex;
    }

    public String getBeschreibung() { return beschreibung; }
    public String getSymbol() { return symbol; }
    public int getColorHex() { return colorHex; }

    public String getColorCSS() {
        return String.format("#%06X", colorHex);
    }
}

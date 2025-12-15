package de.smartolive.irrigation.domain.model;

/**
 * Gesundheitszustand des Systems
 */
public enum SystemHealth {
    HEALTHY("Gesund", "🟢", "Alles funktioniert optimal"),
    WARNING("Warnung", "🟡", "Einige Komponenten benötigen Aufmerksamkeit"),
    CRITICAL("Kritisch", "🔴", "Dringende Maßnahmen erforderlich"),
    UNKNOWN("Unbekannt", "⚫", "Status kann nicht ermittelt werden");

    private final String beschreibung;
    private final String symbol;
    private final String details;

    SystemHealth(String beschreibung, String symbol, String details) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
        this.details = details;
    }

    public String getBeschreibung() { return beschreibung; }
    public String getSymbol() { return symbol; }
    public String getDetails() { return details; }
}
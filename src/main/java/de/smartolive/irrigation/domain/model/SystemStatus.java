package de.smartolive.irrigation.domain.model;

/**
 * Status des gesamten Bewässerungssystems
 */
public enum SystemStatus {
    AKTIV("Aktiv", "🟢", "System läuft normal"),
    WARTUNG("Wartung", "🟡", "System wird gewartet"),
    GESTOPPT("Gestoppt", "🔴", "System manuell gestoppt"),
    FEHLER("Fehler", "🔴", "Systemfehler erkannt"),
    UPDATE("Update", "🔵", "System-Update läuft");

    private final String beschreibung;
    private final String symbol;
    private final String details;

    SystemStatus(String beschreibung, String symbol, String details) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
        this.details = details;
    }

    public String getBeschreibung() { return beschreibung; }
    public String getSymbol() { return symbol; }
    public String getDetails() { return details; }

    public boolean isOperational() {
        return this == AKTIV || this == UPDATE;
    }
}


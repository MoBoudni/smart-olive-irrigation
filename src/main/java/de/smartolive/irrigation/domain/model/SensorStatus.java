package de.smartolive.irrigation.domain.model;

/**
 * Status eines Sensors
 */
public enum SensorStatus {
    ONLINE("Online", "🟢", true, 1),
    OFFLINE("Offline", "⚫", false, 2),
    ERROR("Fehler", "🔴", false, 3),
    CALIBRATING("Kalibrierung", "🟡", false, 4),
    MAINTENANCE("Wartung", "🟠", false, 5),
    LOW_BATTERY("Batterie schwach", "🟡", true, 6),
    CONFIGURING("Konfiguration", "🔵", false, 7),
    SLEEPING("Schlafmodus", "🌙", false, 8);

    private final String beschreibung;
    private final String symbol;
    private final boolean operational;
    private final int priority; // Für Sortierung/Priorisierung

    SensorStatus(String beschreibung, String symbol, boolean operational, int priority) {
        this.beschreibung = beschreibung;
        this.symbol = symbol;
        this.operational = operational;
        this.priority = priority;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getSymbol() {
        return symbol;
    }

    /**
     * Prüft ob der Sensor betriebsbereit ist
     */
    public boolean isOperational() {
        return operational;
    }

    /**
     * Prüft ob der Sensor Daten liefern kann
     */
    public boolean canProvideData() {
        return this == ONLINE || this == LOW_BATTERY;
    }

    /**
     * Prüft ob der Sensor in einem Fehlerzustand ist
     */
    public boolean isErrorState() {
        return this == ERROR || this == OFFLINE;
    }

    /**
     * Prüft ob der Sensor in Wartung/Kalibrierung ist
     */
    public boolean isMaintenanceState() {
        return this == CALIBRATING || this == MAINTENANCE || this == CONFIGURING;
    }

    /**
     * Gibt den Prioritätswert zurück (für Sortierung)
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Konvertiert einen String zurück zum Enum
     */
    public static SensorStatus fromString(String status) {
        if (status == null) {
            return OFFLINE;
        }
        try {
            return SensorStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return OFFLINE; // Fallback
        }
    }

    /**
     * Gibt eine detaillierte Status-Meldung zurück
     */
    public String getDetailedStatus() {
        switch (this) {
            case ONLINE:
                return "Sensor ist online und liefert Daten";
            case OFFLINE:
                return "Sensor ist offline - keine Verbindung";
            case ERROR:
                return "Sensor meldet einen Fehler";
            case CALIBRATING:
                return "Sensor wird kalibriert - temporär keine Daten";
            case MAINTENANCE:
                return "Sensor in Wartung";
            case LOW_BATTERY:
                return "Sensor hat schwache Batterie - bitte wechseln";
            case CONFIGURING:
                return "Sensor wird konfiguriert";
            case SLEEPING:
                return "Sensor im Energiesparmodus";
            default:
                return "Unbekannter Status";
        }
    }

    /**
     * Gibt die Farbe für die UI zurück (CSS-Klassen)
     */
    public String getColorClass() {
        switch (this) {
            case ONLINE:
                return "status-online";
            case OFFLINE:
                return "status-offline";
            case ERROR:
                return "status-error";
            case CALIBRATING:
            case MAINTENANCE:
                return "status-warning";
            case LOW_BATTERY:
                return "status-low-battery";
            case CONFIGURING:
                return "status-configuring";
            case SLEEPING:
                return "status-sleeping";
            default:
                return "status-unknown";
        }
    }

    /**
     * Prüft ob eine Statusänderung erlaubt ist
     */
    public boolean canTransitionTo(SensorStatus newStatus) {
        // Einige Übergänge sind nicht erlaubt
        if (this == ERROR && newStatus == ONLINE) {
            return false; // Von ERROR direkt zu ONLINE geht nicht
        }
        if (this == OFFLINE && newStatus == CALIBRATING) {
            return false; // Offline-Sensoren können nicht kalibriert werden
        }
        return true;
    }

    /**
     * Gibt die empfohlene Aktion für den Status zurück
     */
    public String getRecommendedAction() {
        switch (this) {
            case OFFLINE:
                return "Verbindung prüfen, Sensor neu starten";
            case ERROR:
                return "Sensor überprüfen, Protokolle analysieren";
            case LOW_BATTERY:
                return "Batterie innerhalb von 7 Tagen wechseln";
            case CALIBRATING:
                return "Kalibrierung nicht unterbrechen";
            case MAINTENANCE:
                return "Wartungsprozedur befolgen";
            default:
                return "Keine Aktion erforderlich";
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", symbol, beschreibung);
    }
}
package de.smartolive.irrigation.domain.model;

public enum ParzellenStatus {
    RUHE,
    BEWAESSERUNG_AKTIV,
    FEHLER_SENSOR,
    GESPERRT_WETTER,
    GESPERRT_MANUELL,
    FALLBACK_MODUS
}
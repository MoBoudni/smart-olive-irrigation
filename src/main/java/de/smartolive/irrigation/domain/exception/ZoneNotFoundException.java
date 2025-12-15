package de.smartolive.irrigation.domain.exception;

/**
 * Exception für nicht gefundene Zonen/Parzellen
 */
public class ZoneNotFoundException extends DomainException {
    public ZoneNotFoundException(Long zoneId) {
        super(String.format("Parzelle mit ID %d nicht gefunden", zoneId));
    }

    public ZoneNotFoundException(String name) {
        super(String.format("Parzelle mit Namen '%s' nicht gefunden", name));
    }
}

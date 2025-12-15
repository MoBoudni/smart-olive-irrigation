package de.smartolive.irrigation.domain.exception;

/**
 * Exception für System-Konfigurationsfehler
 */
public class SystemConfigurationException extends DomainException {
    public SystemConfigurationException(String configKey, String expected) {
        super(String.format("Konfigurationsfehler: %s muss %s sein", configKey, expected));
    }
}


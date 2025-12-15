package de.smartolive.irrigation.domain.exception;

/**
 * Exception für Verletzung von Bewässerungsregeln
 */
public class IrrigationRuleViolationException extends DomainException {
    public IrrigationRuleViolationException(String rule, String details) {
        super(String.format("Bewässerungsregel verletzt: %s - %s", rule, details));
    }
}
package de.smartolive.irrigation.domain.exception;

/**
 * Exception für ungültige Feuchtebereiche
 */
public class InvalidMoistureRangeException extends DomainException {
    public InvalidMoistureRangeException(double lower, double upper) {
        super(String.format("Ungültiger Feuchtebereich: %.1f%% - %.1f%%", lower, upper));
    }
}







package de.smartolive.irrigation.domain.exception;

/**
 * Basisklasse für alle fachlichen (domänenbezogenen) Exceptions.
 * Diese sind checked Exceptions, damit sie im Code bewusst behandelt werden müssen.
 */
public class DomainException extends Exception {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
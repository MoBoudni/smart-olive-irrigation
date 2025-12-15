package de.smartolive.irrigation.domain.exception;

/**
 * Exception für offline Sensoren
 */
public class SensorOfflineException extends DomainException {
    public SensorOfflineException(Long sensorId, Long parzelleId) {
        super(String.format("Sensor %d in Parzelle %d ist offline", sensorId, parzelleId));
    }

    public SensorOfflineException(Long parzelleId) {
        super(String.format("Sensor in Parzelle %d ist offline", parzelleId));
    }
}
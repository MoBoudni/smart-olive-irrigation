package de.smartolive.irrigation.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object: Repräsentiert eine Sammlung von Sensordaten
 */
public class Sensordaten {

    private final LocalDateTime timestamp;
    private final double moisturePercent;
    private final Double temperatureCelsius;
    private final Double ecValue; // Elektrische Leitfähigkeit (µS/cm)
    private final Double phValue; // pH-Wert
    private final Double lightIntensity; // Lux
    private final int batteryLevel; // Prozent

    public Sensordaten(double moisturePercent, Double temperatureCelsius,
                       Double ecValue, Double phValue, Double lightIntensity,
                       int batteryLevel) {

        this.timestamp = LocalDateTime.now();

        if (moisturePercent < 0 || moisturePercent > 100) {
            throw new IllegalArgumentException("Feuchte muss zwischen 0-100% liegen");
        }
        this.moisturePercent = moisturePercent;

        if (temperatureCelsius != null && (temperatureCelsius < -50 || temperatureCelsius > 80)) {
            throw new IllegalArgumentException("Temperatur muss zwischen -50°C und 80°C liegen");
        }
        this.temperatureCelsius = temperatureCelsius;

        if (ecValue != null && ecValue < 0) {
            throw new IllegalArgumentException("EC-Wert darf nicht negativ sein");
        }
        this.ecValue = ecValue;

        if (phValue != null && (phValue < 0 || phValue > 14)) {
            throw new IllegalArgumentException("pH-Wert muss zwischen 0-14 liegen");
        }
        this.phValue = phValue;

        if (lightIntensity != null && lightIntensity < 0) {
            throw new IllegalArgumentException("Lichtintensität darf nicht negativ sein");
        }
        this.lightIntensity = lightIntensity;

        if (batteryLevel < 0 || batteryLevel > 100) {
            throw new IllegalArgumentException("Batteriestand muss zwischen 0-100% liegen");
        }
        this.batteryLevel = batteryLevel;
    }

    // Fachliche Methoden

    public boolean isBatteryCritical() {
        return batteryLevel < 10;
    }

    public boolean isDataComplete() {
        return temperatureCelsius != null && ecValue != null && phValue != null;
    }

    public double calculateWaterStressIndex() {
        // Vereinfachter Wasserstress-Index
        double baseIndex = (100 - moisturePercent) / 100.0;

        if (temperatureCelsius != null && temperatureCelsius > 25) {
            baseIndex *= 1.5; // Hitze verstärkt Stress
        }

        if (ecValue != null && ecValue > 2000) {
            baseIndex *= 1.3; // Hoher Salzgehalt verstärkt Stress
        }

        return Math.min(baseIndex, 1.0);
    }

    public String getQualityStatus() {
        if (!isDataComplete()) return "UNVOLLSTÄNDIG";
        if (isBatteryCritical()) return "BATTERIE_KRITISCH";
        if (calculateWaterStressIndex() > 0.7) return "STRESS_HOCH";
        if (calculateWaterStressIndex() > 0.4) return "STRESS_MITTEL";
        return "OK";
    }

    // Getter
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getMoisturePercent() { return moisturePercent; }
    public Double getTemperatureCelsius() { return temperatureCelsius; }
    public Double getEcValue() { return ecValue; }
    public Double getPhValue() { return phValue; }
    public Double getLightIntensity() { return lightIntensity; }
    public int getBatteryLevel() { return batteryLevel; }

    // equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensordaten)) return false;
        Sensordaten that = (Sensordaten) o;
        return Double.compare(that.moisturePercent, moisturePercent) == 0 &&
                batteryLevel == that.batteryLevel &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(temperatureCelsius, that.temperatureCelsius) &&
                Objects.equals(ecValue, that.ecValue) &&
                Objects.equals(phValue, that.phValue) &&
                Objects.equals(lightIntensity, that.lightIntensity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, moisturePercent, temperatureCelsius,
                ecValue, phValue, lightIntensity, batteryLevel);
    }

    @Override
    public String toString() {
        return String.format("Sensordaten[%s]: %.1f%% Feuchte, %.1f°C",
                timestamp.toLocalTime(), moisturePercent,
                temperatureCelsius != null ? temperatureCelsius : 0.0);
    }
}
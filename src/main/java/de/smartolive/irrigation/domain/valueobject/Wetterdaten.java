package de.smartolive.irrigation.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Value Object: Repräsentiert Wetterdaten für eine bestimmte Zeit und Position.
 * Immutable - einmal erstellt, nicht änderbar.
 */
public class Wetterdaten {

    private final LocalDateTime timestamp;
    private final double temperaturCelsius;
    private final double niederschlagMm24h;
    private final double niederschlagWahrscheinlichkeit;
    private final double luftfeuchtigkeitProzent;
    private final double windGeschwindigkeitKmh;
    private final double evapotranspirationMm; // ET0-Wert

    public Wetterdaten(LocalDateTime timestamp,
                       double temperaturCelsius,
                       double niederschlagMm24h,
                       double niederschlagWahrscheinlichkeit,
                       double luftfeuchtigkeitProzent,
                       double windGeschwindigkeitKmh,
                       double evapotranspirationMm) {

        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp darf nicht null sein");

        // Temperatur: realistischer Bereich für Olivenanbau
        if (temperaturCelsius < -20 || temperaturCelsius > 50) {
            throw new IllegalArgumentException("Temperatur muss zwischen -20°C und 50°C liegen");
        }
        this.temperaturCelsius = temperaturCelsius;

        // Niederschlag: nicht negativ
        if (niederschlagMm24h < 0) {
            throw new IllegalArgumentException("Niederschlag darf nicht negativ sein");
        }
        this.niederschlagMm24h = niederschlagMm24h;

        // Wahrscheinlichkeit: 0-100%
        if (niederschlagWahrscheinlichkeit < 0 || niederschlagWahrscheinlichkeit > 100) {
            throw new IllegalArgumentException("Niederschlagswahrscheinlichkeit muss zwischen 0-100% liegen");
        }
        this.niederschlagWahrscheinlichkeit = niederschlagWahrscheinlichkeit;

        // Luftfeuchtigkeit: 0-100%
        if (luftfeuchtigkeitProzent < 0 || luftfeuchtigkeitProzent > 100) {
            throw new IllegalArgumentException("Luftfeuchtigkeit muss zwischen 0-100% liegen");
        }
        this.luftfeuchtigkeitProzent = luftfeuchtigkeitProzent;

        // Windgeschwindigkeit: nicht negativ
        if (windGeschwindigkeitKmh < 0) {
            throw new IllegalArgumentException("Windgeschwindigkeit darf nicht negativ sein");
        }
        this.windGeschwindigkeitKmh = windGeschwindigkeitKmh;

        // Evapotranspiration: nicht negativ
        if (evapotranspirationMm < 0) {
            throw new IllegalArgumentException("Evapotranspiration darf nicht negativ sein");
        }
        this.evapotranspirationMm = evapotranspirationMm;
    }

    // Fabrikmethode für Testdaten
    public static Wetterdaten createTestDaten() {
        return new Wetterdaten(
                LocalDateTime.now(),
                22.5,
                0.0,
                10.0,
                65.0,
                12.5,
                4.2
        );
    }

    // Fachliche Methoden

    /**
     * Prüft ob Regen zu erwarten ist
     * @param thresholdMm Schwellenwert in mm für "signifikanter" Regen
     * @return true wenn Regen erwartet wird
     */
    public boolean isRainExpected(double thresholdMm) {
        return niederschlagMm24h > thresholdMm ||
                (niederschlagWahrscheinlichkeit > 70 && niederschlagMm24h > 0);
    }

    /**
     * Berechnet Temperaturfaktor für Wasserbedarf
     * Basis: 1.0 bei 20°C, steigt um 5% pro Grad über 20°C
     */
    public double getTempFactor() {
        if (temperaturCelsius <= 20) {
            return 1.0;
        }
        double delta = temperaturCelsius - 20;
        return 1.0 + (delta * 0.05);
    }

    /**
     * Berechnet ET0-Faktor für Bewässerung
     * Höhere ET0 = mehr Bewässerung nötig
     */
    public double getEt0Factor() {
        // Normalisierung: ET0 von 0-10mm
        return Math.min(evapotranspirationMm / 5.0, 2.0);
    }

    /**
     * Berechnet Gesamtfaktor für Wasserbedarf
     */
    public double getWaterNeedFactor() {
        return getTempFactor() * getEt0Factor();
    }

    // Getter
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTemperaturCelsius() { return temperaturCelsius; }
    public double getNiederschlagMm24h() { return niederschlagMm24h; }
    public double getNiederschlagWahrscheinlichkeit() { return niederschlagWahrscheinlichkeit; }
    public double getLuftfeuchtigkeitProzent() { return luftfeuchtigkeitProzent; }
    public double getWindGeschwindigkeitKmh() { return windGeschwindigkeitKmh; }
    public double getEvapotranspirationMm() { return evapotranspirationMm; }

    // Value Object equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wetterdaten)) return false;
        Wetterdaten that = (Wetterdaten) o;
        return Double.compare(that.temperaturCelsius, temperaturCelsius) == 0 &&
                Double.compare(that.niederschlagMm24h, niederschlagMm24h) == 0 &&
                Double.compare(that.niederschlagWahrscheinlichkeit, niederschlagWahrscheinlichkeit) == 0 &&
                Double.compare(that.luftfeuchtigkeitProzent, luftfeuchtigkeitProzent) == 0 &&
                Double.compare(that.windGeschwindigkeitKmh, windGeschwindigkeitKmh) == 0 &&
                Double.compare(that.evapotranspirationMm, evapotranspirationMm) == 0 &&
                timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, temperaturCelsius, niederschlagMm24h,
                niederschlagWahrscheinlichkeit, luftfeuchtigkeitProzent,
                windGeschwindigkeitKmh, evapotranspirationMm);
    }

    @Override
    public String toString() {
        return String.format("Wetter [%s]: %.1f°C, %.1fmm, ET0=%.1fmm",
                timestamp.toLocalTime(), temperaturCelsius, niederschlagMm24h, evapotranspirationMm);
    }
}
package de.smartolive.irrigation.domain.model;

import java.util.Objects;

/**
 * Value Object: Konfiguration des Bewässerungssystems
 */
public class SystemConfiguration {

    private final boolean automaticModeEnabled;
    private final int maxParzellen;
    private final double dailyWaterLimitLiters;
    private final int sensorPollingIntervalMinutes;
    private final int weatherUpdateIntervalHours;
    private final boolean emailAlertsEnabled;
    private final boolean smsAlertsEnabled;
    private final double criticalMoistureThreshold;

    public SystemConfiguration(boolean automaticModeEnabled, int maxParzellen,
                               double dailyWaterLimitLiters, int sensorPollingIntervalMinutes,
                               int weatherUpdateIntervalHours, boolean emailAlertsEnabled,
                               boolean smsAlertsEnabled, double criticalMoistureThreshold) {

        this.automaticModeEnabled = automaticModeEnabled;

        if (maxParzellen < 1 || maxParzellen > 100) {
            throw new IllegalArgumentException("Max Parzellen muss zwischen 1-100 liegen");
        }
        this.maxParzellen = maxParzellen;

        if (dailyWaterLimitLiters < 0) {
            throw new IllegalArgumentException("Tägliches Wasserlimit darf nicht negativ sein");
        }
        this.dailyWaterLimitLiters = dailyWaterLimitLiters;

        if (sensorPollingIntervalMinutes < 1 || sensorPollingIntervalMinutes > 60) {
            throw new IllegalArgumentException("Sensor-Interval muss zwischen 1-60 Minuten liegen");
        }
        this.sensorPollingIntervalMinutes = sensorPollingIntervalMinutes;

        if (weatherUpdateIntervalHours < 1 || weatherUpdateIntervalHours > 24) {
            throw new IllegalArgumentException("Wetter-Update muss zwischen 1-24 Stunden liegen");
        }
        this.weatherUpdateIntervalHours = weatherUpdateIntervalHours;

        this.emailAlertsEnabled = emailAlertsEnabled;
        this.smsAlertsEnabled = smsAlertsEnabled;

        if (criticalMoistureThreshold < 0 || criticalMoistureThreshold > 100) {
            throw new IllegalArgumentException("Kritische Feuchte muss zwischen 0-100% liegen");
        }
        this.criticalMoistureThreshold = criticalMoistureThreshold;
    }

    // Default-Konfiguration
    public static SystemConfiguration getDefault() {
        return new SystemConfiguration(
                true,    // automaticModeEnabled
                50,      // maxParzellen
                5000.0,  // dailyWaterLimitLiters (5m³)
                10,      // sensorPollingIntervalMinutes
                3,       // weatherUpdateIntervalHours
                true,    // emailAlertsEnabled
                false,   // smsAlertsEnabled
                20.0     // criticalMoistureThreshold
        );
    }

    // Getter
    public boolean isAutomaticModeEnabled() { return automaticModeEnabled; }
    public int getMaxParzellen() { return maxParzellen; }
    public double getDailyWaterLimitLiters() { return dailyWaterLimitLiters; }
    public int getSensorPollingIntervalMinutes() { return sensorPollingIntervalMinutes; }
    public int getWeatherUpdateIntervalHours() { return weatherUpdateIntervalHours; }
    public boolean isEmailAlertsEnabled() { return emailAlertsEnabled; }
    public boolean isSmsAlertsEnabled() { return smsAlertsEnabled; }
    public double getCriticalMoistureThreshold() { return criticalMoistureThreshold; }

    // Builder-Pattern für einfache Konfigurationsänderungen
    public Builder toBuilder() {
        return new Builder(this);
    }

    public static class Builder {
        private boolean automaticModeEnabled;
        private int maxParzellen;
        private double dailyWaterLimitLiters;
        private int sensorPollingIntervalMinutes;
        private int weatherUpdateIntervalHours;
        private boolean emailAlertsEnabled;
        private boolean smsAlertsEnabled;
        private double criticalMoistureThreshold;

        public Builder() {
            // Default-Werte
            this.automaticModeEnabled = true;
            this.maxParzellen = 50;
            this.dailyWaterLimitLiters = 5000.0;
            this.sensorPollingIntervalMinutes = 10;
            this.weatherUpdateIntervalHours = 3;
            this.emailAlertsEnabled = true;
            this.smsAlertsEnabled = false;
            this.criticalMoistureThreshold = 20.0;
        }

        public Builder(SystemConfiguration config) {
            this.automaticModeEnabled = config.automaticModeEnabled;
            this.maxParzellen = config.maxParzellen;
            this.dailyWaterLimitLiters = config.dailyWaterLimitLiters;
            this.sensorPollingIntervalMinutes = config.sensorPollingIntervalMinutes;
            this.weatherUpdateIntervalHours = config.weatherUpdateIntervalHours;
            this.emailAlertsEnabled = config.emailAlertsEnabled;
            this.smsAlertsEnabled = config.smsAlertsEnabled;
            this.criticalMoistureThreshold = config.criticalMoistureThreshold;
        }

        public Builder automaticModeEnabled(boolean enabled) {
            this.automaticModeEnabled = enabled;
            return this;
        }

        public Builder maxParzellen(int max) {
            this.maxParzellen = max;
            return this;
        }

        public Builder dailyWaterLimitLiters(double limit) {
            this.dailyWaterLimitLiters = limit;
            return this;
        }

        public Builder sensorPollingIntervalMinutes(int interval) {
            this.sensorPollingIntervalMinutes = interval;
            return this;
        }

        public Builder weatherUpdateIntervalHours(int interval) {
            this.weatherUpdateIntervalHours = interval;
            return this;
        }

        public Builder emailAlertsEnabled(boolean enabled) {
            this.emailAlertsEnabled = enabled;
            return this;
        }

        public Builder smsAlertsEnabled(boolean enabled) {
            this.smsAlertsEnabled = enabled;
            return this;
        }

        public Builder criticalMoistureThreshold(double threshold) {
            this.criticalMoistureThreshold = threshold;
            return this;
        }

        public SystemConfiguration build() {
            return new SystemConfiguration(
                    automaticModeEnabled,
                    maxParzellen,
                    dailyWaterLimitLiters,
                    sensorPollingIntervalMinutes,
                    weatherUpdateIntervalHours,
                    emailAlertsEnabled,
                    smsAlertsEnabled,
                    criticalMoistureThreshold
            );
        }
    }

    // equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SystemConfiguration)) return false;
        SystemConfiguration that = (SystemConfiguration) o;
        return automaticModeEnabled == that.automaticModeEnabled &&
                maxParzellen == that.maxParzellen &&
                Double.compare(that.dailyWaterLimitLiters, dailyWaterLimitLiters) == 0 &&
                sensorPollingIntervalMinutes == that.sensorPollingIntervalMinutes &&
                weatherUpdateIntervalHours == that.weatherUpdateIntervalHours &&
                emailAlertsEnabled == that.emailAlertsEnabled &&
                smsAlertsEnabled == that.smsAlertsEnabled &&
                Double.compare(that.criticalMoistureThreshold, criticalMoistureThreshold) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(automaticModeEnabled, maxParzellen, dailyWaterLimitLiters,
                sensorPollingIntervalMinutes, weatherUpdateIntervalHours,
                emailAlertsEnabled, smsAlertsEnabled, criticalMoistureThreshold);
    }
}
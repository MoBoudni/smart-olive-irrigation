package de.smartolive.irrigation.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "sensor_readings")
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parzelle_id", nullable = false)
    private Long parzelleId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "moisture_percent")
    private Double moisturePercent;

    @Column(name = "temperature_celsius")
    private Double temperatureCelsius;

    @Column(name = "ec_value")
    private Double ecValue; // Elektrische Leitfähigkeit (Salzgehalt) in µS/cm

    @Column(name = "ph_value")
    private Double phValue; // pH-Wert (neu hinzugefügt)

    @Column(name = "battery_level")
    private Integer batteryLevel; // in Prozent (neu hinzugefügt)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SensorStatus status;

    @Column(name = "sensor_id")
    private String sensorId; // Eindeutige Sensor-ID (z.B. MAC-Adresse)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "signal_strength")
    private Integer signalStrength; // in Prozent (0-100)

    @Column(name = "location_accuracy")
    private Double locationAccuracy; // in Metern

    @Column(name = "data_quality_score")
    private Integer dataQualityScore; // 0-100

    // Konstruktoren

    public SensorReading() {
        // JPA benötigt einen no-arg Konstruktor
    }

    public SensorReading(Long parzelleId, double moisturePercent, Double temperatureCelsius,
                         Double ecValue, Double phValue, Integer batteryLevel, String sensorId) {
        this.parzelleId = Objects.requireNonNull(parzelleId, "Parzellen-ID darf nicht null sein");
        this.timestamp = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();

        // Validierung der Sensorwerte
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

        if (batteryLevel != null && (batteryLevel < 0 || batteryLevel > 100)) {
            throw new IllegalArgumentException("Batteriestand muss zwischen 0-100% liegen");
        }
        this.batteryLevel = batteryLevel;

        this.sensorId = sensorId;
        this.status = SensorStatus.ONLINE;
        this.dataQualityScore = calculateDataQualityScore();
    }

    // Vor der Persistierung
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = SensorStatus.ONLINE;
        }
        if (dataQualityScore == null) {
            dataQualityScore = calculateDataQualityScore();
        }
    }

    // Vor dem Update
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        dataQualityScore = calculateDataQualityScore();
    }

    // Fachliche Methoden

    /**
     * Prüft ob die Messung veraltet ist
     * @param maxAgeMinutes Maximale Alter in Minuten
     * @return true wenn veraltet
     */
    public boolean isStale(int maxAgeMinutes) {
        return timestamp.plusMinutes(maxAgeMinutes).isBefore(LocalDateTime.now());
    }

    /**
     * Prüft ob der Sensorwert plausibel ist
     */
    public boolean isValid() {
        if (moisturePercent < 0 || moisturePercent > 100) return false;
        if (temperatureCelsius != null && (temperatureCelsius < -50 || temperatureCelsius > 80)) return false;
        if (ecValue != null && ecValue < 0) return false;
        if (phValue != null && (phValue < 0 || phValue > 14)) return false;
        if (batteryLevel != null && (batteryLevel < 0 || batteryLevel > 100)) return false;
        return status != SensorStatus.ERROR;
    }

    /**
     * Prüft ob die Batterie kritisch ist
     */
    public boolean isBatteryCritical() {
        return batteryLevel != null && batteryLevel < 10;
    }

    /**
     * Korrigiert einen Sensorwert (z.B. nach Kalibrierung)
     */
    public void correctMoisture(double correctedValue) {
        if (correctedValue < 0 || correctedValue > 100) {
            throw new IllegalArgumentException("Korrigierte Feuchte muss zwischen 0-100% liegen");
        }
        this.moisturePercent = correctedValue;
        this.updatedAt = LocalDateTime.now();
        this.dataQualityScore = calculateDataQualityScore();
    }

    /**
     * Markiert den Sensor als offline
     */
    public void markAsOffline() {
        this.status = SensorStatus.OFFLINE;
        this.updatedAt = LocalDateTime.now();
        this.dataQualityScore = calculateDataQualityScore();
    }

    /**
     * Markiert den Sensor als fehlerhaft
     */
    public void markAsError(String errorDetails) {
        this.status = SensorStatus.ERROR;
        this.updatedAt = LocalDateTime.now();
        this.dataQualityScore = 0; // Datenqualität bei Fehler = 0
    }

    /**
     * Berechnet Wasserstress-Index basierend auf allen Werten
     */
    public double calculateWaterStressIndex() {
        if (!canProvideData()) {
            return 1.0; // Maximaler Stress wenn keine Daten
        }

        double baseIndex = (100 - moisturePercent) / 100.0;

        if (temperatureCelsius != null && temperatureCelsius > 25) {
            baseIndex *= 1.5; // Hitze verstärkt Stress
        }

        if (ecValue != null && ecValue > 2000) {
            baseIndex *= 1.3; // Hoher Salzgehalt verstärkt Stress
        }

        if (phValue != null && (phValue < 5.5 || phValue > 7.5)) {
            baseIndex *= 1.2; // Ungünstiger pH-Wert verstärkt Stress
        }

        return Math.min(baseIndex, 1.0);
    }

    /**
     * Prüft ob der Sensor Daten liefern kann
     */
    public boolean canProvideData() {
        return status.canProvideData() && !isStale(30) && isValid();
    }

    /**
     * Status ändern mit Validierung
     */
    public void updateSensorStatus(SensorStatus newStatus) {
        if (this.status.canTransitionTo(newStatus)) {
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
            this.dataQualityScore = calculateDataQualityScore();
        } else {
            throw new IllegalStateException(
                    String.format("Statusübergang von %s zu %s nicht erlaubt",
                            this.status, newStatus)
            );
        }
    }

    /**
     * Prüft ob der Sensor Aufmerksamkeit benötigt
     */
    public boolean requiresAttention() {
        return getStatus().isErrorState() ||
                getStatus() == SensorStatus.LOW_BATTERY ||
                isBatteryCritical() ||
                isStale(60) ||
                (dataQualityScore != null && dataQualityScore < 50);
    }

    /**
     * Berechnet Datenqualität-Score (0-100)
     */
    private int calculateDataQualityScore() {
        int score = 100;

        // Abzug für Batterie
        if (batteryLevel != null && batteryLevel < 20) {
            score -= 20;
        } else if (batteryLevel != null && batteryLevel < 50) {
            score -= 10;
        }

        // Abzug für Signalstärke
        if (signalStrength != null && signalStrength < 50) {
            score -= 15;
        } else if (signalStrength != null && signalStrength < 80) {
            score -= 5;
        }

        // Abzug für Status
        if (status == SensorStatus.LOW_BATTERY) {
            score -= 10;
        } else if (status == SensorStatus.ERROR) {
            score = 0;
        } else if (status.isMaintenanceState()) {
            score -= 30;
        }

        // Abzug für veraltete Daten
        if (isStale(60)) {
            score -= 40;
        } else if (isStale(30)) {
            score -= 20;
        }

        // Abzug für fehlende Werte
        if (moisturePercent == null) score -= 30;
        if (temperatureCelsius == null) score -= 10;
        if (ecValue == null) score -= 10;
        if (phValue == null) score -= 10;

        return Math.max(0, Math.min(100, score));
    }

    /**
     * Gibt eine Zusammenfassung der Sensordaten zurück
     */
    public String getDataSummary() {
        return String.format(
                "Sensor %s: %.1f%% Feuchte, %.1f°C, %s, Qualität: %d/100",
                sensorId != null ? sensorId : "Unbekannt",
                moisturePercent != null ? moisturePercent : 0.0,
                temperatureCelsius != null ? temperatureCelsius : 0.0,
                status.getBeschreibung(),
                dataQualityScore != null ? dataQualityScore : 0
        );
    }

    /**
     * Prüft ob alle kritischen Werte vorhanden sind
     */
    public boolean hasCriticalData() {
        return moisturePercent != null &&
                status.canProvideData() &&
                !isStale(120);
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParzelleId() {
        return parzelleId;
    }

    public void setParzelleId(Long parzelleId) {
        this.parzelleId = parzelleId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getMoisturePercent() {
        return moisturePercent;
    }

    public void setMoisturePercent(Double moisturePercent) {
        this.moisturePercent = moisturePercent;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public Double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(Double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public Double getEcValue() {
        return ecValue;
    }

    public void setEcValue(Double ecValue) {
        this.ecValue = ecValue;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public Double getPhValue() {
        return phValue;
    }

    public void setPhValue(Double phValue) {
        this.phValue = phValue;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public SensorStatus getStatus() {
        return status;
    }

    public void setStatus(SensorStatus status) {
        this.status = status;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(Integer signalStrength) {
        this.signalStrength = signalStrength;
        this.dataQualityScore = calculateDataQualityScore();
    }

    public Double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(Double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public Integer getDataQualityScore() {
        return dataQualityScore;
    }

    public void setDataQualityScore(Integer dataQualityScore) {
        this.dataQualityScore = dataQualityScore;
    }

    // equals und hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorReading that = (SensorReading) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "SensorReading[id=%d, sensor=%s, moisture=%.1f%%, temp=%.1f°C, status=%s, quality=%d/100, time=%s]",
                id,
                sensorId != null ? sensorId : "N/A",
                moisturePercent != null ? moisturePercent : 0.0,
                temperatureCelsius != null ? temperatureCelsius : 0.0,
                status,
                dataQualityScore != null ? dataQualityScore : 0,
                timestamp
        );
    }
}
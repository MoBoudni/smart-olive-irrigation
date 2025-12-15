package de.smartolive.irrigation.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "irrigation_events")
public class IrrigationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parzelle_id", nullable = false)
    private Long parzelleId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "wasser_menge_liter", nullable = false)
    private double wasserMengeLiter;

    @Column(name = "type", nullable = false)
    private String type; // "AUTOMATIC", "MANUAL", "SCHEDULED", etc.

    @Column(name = "triggered_by", nullable = false)
    private String triggeredBy;

    @Column(name = "bemerkungen", length = 500)
    private String bemerkungen;

    // Standard-Konstruktor (für JPA)
    public IrrigationEvent() {
    }

    // Geschäfts-Konstruktor
    public IrrigationEvent(Long parzelleId,
                           double wasserMengeLiter,
                           String type,
                           String triggeredBy) {

        this.parzelleId = Objects.requireNonNull(parzelleId, "Parzellen-ID darf nicht null sein");
        this.startTime = LocalDateTime.now();
        this.endTime = null; // Wird nach Beendigung gesetzt

        if (wasserMengeLiter < 0) {
            throw new IllegalArgumentException("Wassermenge darf nicht negativ sein");
        }
        this.wasserMengeLiter = wasserMengeLiter;

        this.type = Objects.requireNonNull(type, "Bewässerungstyp darf nicht null sein");
        this.triggeredBy = Objects.requireNonNull(triggeredBy, "Auslöser darf nicht null sein");
    }

    // Fachliche Methoden

    /**
     * Beendet die Bewässerung
     */
    public void complete() {
        this.endTime = LocalDateTime.now();
    }

    /**
     * Beendet die Bewässerung mit Bemerkung (z.B. "Abbruch wegen Regen")
     */
    public void complete(String bemerkung) {
        complete();
        this.bemerkungen = bemerkung;
    }

    /**
     * Berechnet die Dauer in Minuten
     */
    public Long getDurationMinutes() {
        if (endTime == null) {
            return null; // Noch laufend
        }
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    /**
     * Prüft ob die Bewässerung noch läuft
     */
    public boolean isActive() {
        return endTime == null;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getWasserMengeLiter() {
        return wasserMengeLiter;
    }

    public void setWasserMengeLiter(double wasserMengeLiter) {
        this.wasserMengeLiter = wasserMengeLiter;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }

    public void setTriggeredBy(String triggeredBy) {
        this.triggeredBy = triggeredBy;
    }

    public String getBemerkungen() {
        return bemerkungen;
    }

    public void setBemerkungen(String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    // equals/hashCode für Entity
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IrrigationEvent)) return false;
        IrrigationEvent that = (IrrigationEvent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (isActive()) {
            return String.format("IrrigationEvent[%s]: %.1fL läuft seit %s",
                    id, wasserMengeLiter, startTime.toLocalTime());
        } else {
            return String.format("IrrigationEvent[%s]: %.1fL (%s) %s-%s",
                    id, wasserMengeLiter, type,
                    startTime.toLocalTime(), endTime.toLocalTime());
        }
    }
}
package de.smartolive.irrigation.domain.valueobject;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Value Object: Repräsentiert ein erlaubtes Zeitfenster für automatische Bewässerung.
 * Beispiel: von 06:00 bis 09:00 Uhr.
 * Immutable.
 */
public class TimeWindow {

    private final LocalTime start;
    private final LocalTime end;

    public TimeWindow(LocalTime start, LocalTime end) {
        Objects.requireNonNull(start, "Startzeit darf nicht null sein.");
        Objects.requireNonNull(end, "Endzeit darf nicht null sein.");
        if (start.isAfter(end) && !start.equals(end)) {
            // Erlaubt Über-Mitternacht-Fenster? Für Einfachheit erstmal nein.
            throw new IllegalArgumentException("Startzeit darf nicht nach Endzeit liegen (Über-Mitternacht noch nicht unterstützt).");
        }
        this.start = start;
        this.end = end;
    }

    /** Fachliche Methode: Prüft, ob die aktuelle Uhrzeit innerhalb des Fensters liegt */
    public boolean containsNow() {
        LocalTime now = LocalTime.now();
        return contains(now);
    }

    public boolean contains(LocalTime time) {
        if (start.equals(end)) {
            return true; // Ganzes Tag erlaubt
        }
        if (start.isBefore(end)) {
            return !time.isBefore(start) && time.isBefore(end);
        } else {
            // Über Mitternacht (z. B. 22:00 – 06:00)
            return !time.isBefore(start) || !time.isAfter(end);
        }
    }

    // Getter
    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    // equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeWindow that)) return false;
        return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start + " – " + end;
    }
}
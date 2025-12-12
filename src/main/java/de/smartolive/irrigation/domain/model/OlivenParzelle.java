package de.smartolive.irrigation.domain.model;

import de.smartolive.irrigation.domain.exception.DomainException;
import de.smartolive.irrigation.domain.valueobject.MoistureRange;
import de.smartolive.irrigation.domain.valueobject.TimeWindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root: Repräsentiert eine räumlich abgegrenzte Olivenparzelle
 * mit eigener Sensorik, Bewässerungslogik und Konfiguration.
 */
public class OlivenParzelle {

    private Long id;
    private String name;
    private OlivenbaumProfil profil;                    // Spezifische Olivenbaum-Eigenschaften
    private MoistureRange targetMoistureRange;          // Soll-Feuchtebereich (z. B. 30–60%)
    private List<TimeWindow> allowedTimeWindows;        // Erlaubte Bewässerungszeiten
    private int maxDailyDurationMinutes;                // Max. Bewässerungsdauer pro Tag
    private ParzellenStatus status;                     // RUHE, BEWAESSERUNG, FEHLER, GESPERRT etc.

    // Konstruktor für Factory-Methode (DDD-Best-Practice)
    private OlivenParzelle() {
        this.allowedTimeWindows = new ArrayList<>();
        this.status = ParzellenStatus.RUHE;
    }

    public static OlivenParzelle create(String name, OlivenbaumProfil profil, MoistureRange targetRange)
            throws DomainException {
        if (name == null || name.isBlank()) {
            throw new DomainException("Parzellenname darf nicht leer sein.");
        }
        if (profil == null) {
            throw new DomainException("Olivenbaumprofil muss angegeben werden.");
        }
        if (targetRange == null) {
            throw new DomainException("Feuchte-Sollbereich muss angegeben werden.");
        }
        // Validierung erfolgt bereits im MoistureRange-Konstruktor

        OlivenParzelle parzelle = new OlivenParzelle();
        parzelle.name = name;
        parzelle.profil = profil;
        parzelle.targetMoistureRange = targetRange;
        parzelle.maxDailyDurationMinutes = 60;
        return parzelle;
    }

    // --- Fachliche Methoden (Beispiele) ---

    public boolean isIrrigationAllowedNow() {
        // Prüft Zeitfenster – hier später erweiterbar um Feiertage, Frost etc.
        return allowedTimeWindows.isEmpty() || // Wenn keine Einschränkung → immer erlaubt
                allowedTimeWindows.stream().anyMatch(TimeWindow::containsNow);
    }

    public void addTimeWindow(TimeWindow window) {
        Objects.requireNonNull(window, "Zeitfenster darf nicht null sein.");
        this.allowedTimeWindows.add(window);
    }

    // --- Getter (keine Setter für Immutabilität der Kernkonfiguration) ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public OlivenbaumProfil getProfil() {
        return profil;
    }

    public MoistureRange getTargetMoistureRange() {
        return targetMoistureRange;
    }

    public List<TimeWindow> getAllowedTimeWindows() {
        return Collections.unmodifiableList(allowedTimeWindows);
    }

    public int getMaxDailyDurationMinutes() {
        return maxDailyDurationMinutes;
    }

    public ParzellenStatus getStatus() {
        return status;
    }

    public void setStatus(ParzellenStatus status) {
        this.status = status;
    }

    // Weitere fachliche Methoden folgen später (z. B. calculateDailyWaterNeed())
}
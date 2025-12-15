package de.smartolive.irrigation.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value Object: Repräsentiert eine Bewässerungsempfehlung für eine Parzelle
 * basierend auf aktuellen Sensorwerten, Wetter und Parzellenprofil.
 */
public class BewaesserungsEmpfehlung {

    private final LocalDateTime erstelltAm;
    private final double empfohleneMengeLiter;
    private final EmpfehlungsStufe stufe;
    private final List<String> begruendungen;

    public BewaesserungsEmpfehlung(LocalDateTime erstelltAm,
                                   double empfohleneMengeLiter,
                                   EmpfehlungsStufe stufe,
                                   List<String> begruendungen) {

        this.erstelltAm = Objects.requireNonNull(erstelltAm, "Erstellungszeit darf nicht null sein");

        if (empfohleneMengeLiter < 0) {
            throw new IllegalArgumentException("Empfohlene Menge darf nicht negativ sein");
        }
        this.empfohleneMengeLiter = empfohleneMengeLiter;

        this.stufe = Objects.requireNonNull(stufe, "Empfehlungsstufe darf nicht null sein");
        this.begruendungen = begruendungen != null ?
                Collections.unmodifiableList(begruendungen) :
                Collections.emptyList();
    }

    // Fabrikmethode für "Keine Bewässerung"
    public static BewaesserungsEmpfehlung keineBewaesserung(String... begruendungen) {
        return new BewaesserungsEmpfehlung(
                LocalDateTime.now(),
                0.0,
                EmpfehlungsStufe.KEINE,
                List.of(begruendungen)
        );
    }

    // Fabrikmethode für "Empfohlene Bewässerung"
    public static BewaesserungsEmpfehlung empfehlung(double liter, String... begruendungen) {
        EmpfehlungsStufe stufe = liter > 50 ? EmpfehlungsStufe.ERHOEHT : EmpfehlungsStufe.NORMAL;
        return new BewaesserungsEmpfehlung(
                LocalDateTime.now(),
                liter,
                stufe,
                List.of(begruendungen)
        );
    }

    // Fachliche Methoden

    public boolean sollBewaessertWerden() {
        return empfohleneMengeLiter > 0;
    }

    public double getEmpfohleneMengeLiter() {
        return empfohleneMengeLiter;
    }

    public EmpfehlungsStufe getStufe() {
        return stufe;
    }

    public List<String> getBegruendungen() {
        return begruendungen;
    }

    public LocalDateTime getErstelltAm() {
        return erstelltAm;
    }

    // Value Object equals/hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BewaesserungsEmpfehlung)) return false;
        BewaesserungsEmpfehlung that = (BewaesserungsEmpfehlung) o;
        return Double.compare(that.empfohleneMengeLiter, empfohleneMengeLiter) == 0 &&
                erstelltAm.equals(that.erstelltAm) &&
                stufe == that.stufe &&
                begruendungen.equals(that.begruendungen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erstelltAm, empfohleneMengeLiter, stufe, begruendungen);
    }

    @Override
    public String toString() {
        if (sollBewaessertWerden()) {
            return String.format("Empfehlung: %.1fL (%s) - %s",
                    empfohleneMengeLiter, stufe, String.join(", ", begruendungen));
        } else {
            return String.format("Keine Bewässerung (%s) - %s",
                    stufe, String.join(", ", begruendungen));
        }
    }
}
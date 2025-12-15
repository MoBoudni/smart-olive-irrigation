package de.smartolive.irrigation.domain.service;

import de.smartolive.irrigation.domain.model.*;
import de.smartolive.irrigation.domain.valueobject.*;
import de.smartolive.irrigation.domain.exception.DomainException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OlivenIrrigationRuleService {

    // Konfigurationsparameter
    private static final int MAX_STALE_MINUTES = 30;
    private static final double RAIN_THRESHOLD_MM = 3.0;
    private static final double HIGH_RAIN_PROBABILITY = 70.0;
    private static final double CRITICAL_MOISTURE_THRESHOLD = 20.0; // Unter 20% = kritisch

    /**
     * Evaluiiert ob und wie viel bewässert werden soll
     */
    public BewaesserungsEmpfehlung evaluate(
            OlivenParzelle parzelle,
            Wetterdaten wetter,
            SensorReading latestReading,
            List<IrrigationEvent> todayEvents) throws DomainException {

        List<String> begruendungen = new ArrayList<>();

        // 1. Validierung der Eingaben
        validateInputs(parzelle, wetter, begruendungen);

        // 2. Sensor-Check
        if (!isSensorDataValid(latestReading, begruendungen)) {
            return createFallbackEmpfehlung(parzelle, wetter, begruendungen);
        }

        // 3. Wetter-Check
        if (shouldSkipDueToWeather(wetter, begruendungen)) {
            return BewaesserungsEmpfehlung.keineBewaesserung(
                    begruendungen.toArray(new String[0])
            );
        }

        // 4. Zeitfenster-Check
        if (!parzelle.isIrrigationAllowedNow()) {
            begruendungen.add("Aktuelle Zeit nicht in erlaubten Zeitfenstern");
            return BewaesserungsEmpfehlung.keineBewaesserung(
                    begruendungen.toArray(new String[0])
            );
        }

        // 5. Tägliches Limit prüfen
        double alreadyWateredToday = calculateTodayWaterUsage(todayEvents);
        if (alreadyWateredToday >= parzelle.getMaxDailyDurationMinutes() * 10) { // 10L pro Minute
            begruendungen.add("Tägliches Limit bereits erreicht: " + alreadyWateredToday + "L");
            return BewaesserungsEmpfehlung.keineBewaesserung(
                    begruendungen.toArray(new String[0])
            );
        }

        // 6. Feuchte-Bereich prüfen
        MoistureRange targetRange = parzelle.getTargetMoistureRange();
        double currentMoisture = latestReading.getMoisturePercent();

        if (targetRange.contains(currentMoisture)) {
            begruendungen.add("Feuchte im optimalen Bereich: " + currentMoisture + "%");
            return BewaesserungsEmpfehlung.keineBewaesserung(
                    begruendungen.toArray(new String[0])
            );
        }

        // 7. Wasserbedarf berechnen
        double waterNeed = calculateWaterNeed(parzelle, wetter, currentMoisture, targetRange, begruendungen);

        // 8. Empfehlungsstufe bestimmen
        EmpfehlungsStufe stufe = determineRecommendationLevel(
                currentMoisture, targetRange, waterNeed, parzelle, begruendungen);

        return new BewaesserungsEmpfehlung(
                LocalDateTime.now(),
                waterNeed,
                stufe,
                begruendungen
        );
    }

    private void validateInputs(OlivenParzelle parzelle, Wetterdaten wetter, List<String> begruendungen)
            throws DomainException {

        if (parzelle == null) {
            throw new DomainException("Parzelle darf nicht null sein");
        }

        if (wetter == null) {
            throw new DomainException("Wetterdaten dürfen nicht null sein");
        }

        // Prüfe ob Wetterdaten aktuell sind (max 2 Stunden alt)
        if (wetter.getTimestamp().isBefore(LocalDateTime.now().minusHours(2))) {
            begruendungen.add("Warnung: Wetterdaten sind älter als 2 Stunden");
        }
    }

    private boolean isSensorDataValid(SensorReading reading, List<String> begruendungen) {
        if (reading == null) {
            begruendungen.add("Keine Sensordaten verfügbar");
            return false;
        }

        if (reading.isStale(MAX_STALE_MINUTES)) {
            begruendungen.add("Sensordaten veraltet (" + MAX_STALE_MINUTES + "min+)");
            return false;
        }

        if (!reading.isValid()) {
            begruendungen.add("Sensor meldet Fehler: " + reading.getStatus());
            return false;
        }

        return true;
    }

    private BewaesserungsEmpfehlung createFallbackEmpfehlung(
            OlivenParzelle parzelle,
            Wetterdaten wetter,
            List<String> begruendungen) {

        // Fallback-Logik: Bewässere basierend auf Tageszeit und Wetter
        LocalTime now = LocalTime.now();
        boolean isDaytime = now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(20, 0));

        if (isDaytime && !wetter.isRainExpected(RAIN_THRESHOLD_MM)) {
            double fallbackAmount = parzelle.getProfil().getBasisWasserbedarfLiterProTag() * 0.5;
            begruendungen.add("Fallback: Standardbewässerung aufgrund fehlender Sensordaten");

            return new BewaesserungsEmpfehlung(
                    LocalDateTime.now(),
                    fallbackAmount,
                    EmpfehlungsStufe.FALLBACK,
                    begruendungen
            );
        }

        begruendungen.add("Fallback: Keine Bewässerung (Nacht oder Regen)");
        return BewaesserungsEmpfehlung.keineBewaesserung(
                begruendungen.toArray(new String[0])
        );
    }

    private boolean shouldSkipDueToWeather(Wetterdaten wetter, List<String> begruendungen) {
        // Regen-Check
        if (wetter.isRainExpected(RAIN_THRESHOLD_MM)) {
            begruendungen.add("Regen vorhergesagt: " +
                    wetter.getNiederschlagMm24h() + "mm (" +
                    wetter.getNiederschlagWahrscheinlichkeit() + "%)");
            return true;
        }

        // Temperatur-Check (unter 0°C → Frostgefahr)
        if (wetter.getTemperaturCelsius() < 0) {
            begruendungen.add("Frostgefahr: " + wetter.getTemperaturCelsius() + "°C");
            return true;
        }

        // Starker Wind (> 40 km/h)
        if (wetter.getWindGeschwindigkeitKmh() > 40) {
            begruendungen.add("Starker Wind: " + wetter.getWindGeschwindigkeitKmh() + "km/h");
            return true;
        }

        return false;
    }

    private double calculateTodayWaterUsage(List<IrrigationEvent> todayEvents) {
        if (todayEvents == null || todayEvents.isEmpty()) {
            return 0.0;
        }

        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        return todayEvents.stream()
                .filter(event -> event.getStartTime().isAfter(todayStart))
                .mapToDouble(IrrigationEvent::getWasserMengeLiter)
                .sum();
    }

    private double calculateWaterNeed(
            OlivenParzelle parzelle,
            Wetterdaten wetter,
            double currentMoisture,
            MoistureRange targetRange,
            List<String> begruendungen) {

        // Basis: Täglicher Wasserbedarf der Olivensorte
        double baseNeed = parzelle.getProfil().getBasisWasserbedarfLiterProTag();

        // Faktor 1: Feuchtedefizit
        double moistureFactor = calculateMoistureFactor(currentMoisture, targetRange);

        // Faktor 2: Wetter (Temperatur, ET0)
        double weatherFactor = wetter.getWaterNeedFactor();

        // Faktor 3: Bodentyp
        double soilFactor = calculateSoilFactor(parzelle.getProfil().getBodenTyp());

        // Faktor 4: Baumalter (junge Bäume brauchen mehr)
        double ageFactor = calculateAgeFactor(parzelle.getProfil().getAlterJahre());

        // Gesamtberechnung
        double totalNeed = baseNeed * moistureFactor * weatherFactor * soilFactor * ageFactor;

        // Begründungen hinzufügen
        begruendungen.add(String.format("Basis: %.1fL/Tag (%s)",
                baseNeed, parzelle.getProfil().getSorte()));
        begruendungen.add(String.format("Feuchtefaktor: %.2f (aktuell: %.1f%%)",
                moistureFactor, currentMoisture));
        begruendungen.add(String.format("Wetterfaktor: %.2f (%.1f°C, ET0=%.1fmm)",
                weatherFactor, wetter.getTemperaturCelsius(), wetter.getEvapotranspirationMm()));
        begruendungen.add(String.format("Bodenfaktor: %.2f (%s)",
                soilFactor, parzelle.getProfil().getBodenTyp()));
        begruendungen.add(String.format("Altersfaktor: %.2f (%d Jahre)",
                ageFactor, parzelle.getProfil().getAlterJahre()));

        return Math.round(totalNeed * 10.0) / 10.0; // Auf 0.1L runden
    }

    private double calculateMoistureFactor(double currentMoisture, MoistureRange targetRange) {
        if (targetRange.isBelow(currentMoisture)) {
            // Unter Sollbereich: mehr Bewässerung
            double deficit = targetRange.getLower() - currentMoisture;
            return 1.0 + (deficit / 10.0); // +10% pro %-Punkt Defizit
        } else if (targetRange.isAbove(currentMoisture)) {
            // Über Sollbereich: weniger Bewässerung
            double excess = currentMoisture - targetRange.getUpper();
            return Math.max(0.1, 1.0 - (excess / 20.0)); // -5% pro %-Punkt Überschuss
        }
        return 1.0;
    }

    private double calculateSoilFactor(BodenTyp bodenTyp) {
        switch (bodenTyp) {
            case SANDIG:    return 1.3;  // Sand speichert schlecht
            case LEHMIG:    return 1.0;  // Optimal
            case TONIG:     return 0.8;  // Ton speichert gut
            case LOESS:     return 1.1;  // Löss speichert mittel
            case KALKHALTIG: return 1.2; // Kalk speichert schlecht
            default:        return 1.0;
        }
    }

    private double calculateAgeFactor(int alterJahre) {
        if (alterJahre < 3)  return 1.5;  // Junge Bäume
        if (alterJahre < 10) return 1.2;  // Heranwachsend
        if (alterJahre < 30) return 1.0;  // Ausgewachsen
        if (alterJahre < 50) return 0.9;  // Reif
        return 0.8;                        // Alte Bäume
    }

    private EmpfehlungsStufe determineRecommendationLevel(
            double currentMoisture,
            MoistureRange targetRange,
            double waterNeed,
            OlivenParzelle parzelle,
            List<String> begruendungen) {

        if (currentMoisture < CRITICAL_MOISTURE_THRESHOLD) {
            begruendungen.add("KRITISCH: Feuchte unter " + CRITICAL_MOISTURE_THRESHOLD + "%");
            return EmpfehlungsStufe.KRITISCH;
        }

        if (waterNeed > parzelle.getProfil().getBasisWasserbedarfLiterProTag() * 1.5) {
            return EmpfehlungsStufe.ERHOEHT;
        }

        if (waterNeed > 0) {
            return EmpfehlungsStufe.NORMAL;
        }

        return EmpfehlungsStufe.KEINE;
    }

    // Getter für Test-Zwecke
    public int getMaxStaleMinutes() { return MAX_STALE_MINUTES; }
    public double getRainThresholdMm() { return RAIN_THRESHOLD_MM; }
}
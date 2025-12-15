package de.smartolive.irrigation.domain.service;

import de.smartolive.irrigation.domain.model.*;
import de.smartolive.irrigation.domain.valueobject.*;
import de.smartolive.irrigation.domain.repository.OlivenParzelleRepository;
import de.smartolive.irrigation.domain.repository.SensorReadingRepository;
import de.smartolive.irrigation.domain.repository.IrrigationEventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RecommendationService {

    private final OlivenIrrigationRuleService ruleService;
    private final OlivenParzelleRepository parzelleRepository;
    private final SensorReadingRepository sensorRepository;
    private final IrrigationEventRepository eventRepository;

    public RecommendationService(
            OlivenIrrigationRuleService ruleService,
            OlivenParzelleRepository parzelleRepository,
            SensorReadingRepository sensorRepository,
            IrrigationEventRepository eventRepository) {
        this.ruleService = ruleService;
        this.parzelleRepository = parzelleRepository;
        this.sensorRepository = sensorRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Generiert Empfehlungen für alle Parzellen
     */
    public Map<Long, BewaesserungsEmpfehlung> generateRecommendationsForAll(Wetterdaten wetter) {
        Map<Long, BewaesserungsEmpfehlung> recommendations = new HashMap<>();

        List<OlivenParzelle> parzellen = parzelleRepository.findAll();

        for (OlivenParzelle parzelle : parzellen) {
            try {
                BewaesserungsEmpfehlung empfehlung = generateRecommendation(parzelle.getId(), wetter);
                recommendations.put(parzelle.getId(), empfehlung);
            } catch (Exception e) {
                // Fallback für fehlerhafte Parzellen
                recommendations.put(parzelle.getId(),
                        createErrorEmpfehlung("Fehler bei Berechnung: " + e.getMessage()));
            }
        }

        return recommendations;
    }

    /**
     * Generiert Empfehlung für eine spezifische Parzelle
     */
    public BewaesserungsEmpfehlung generateRecommendation(Long parzelleId, Wetterdaten wetter) {
        OlivenParzelle parzelle = parzelleRepository.findById(parzelleId)
                .orElseThrow(() -> new IllegalArgumentException("Parzelle nicht gefunden: " + parzelleId));

        // Letzte Sensor-Messung laden
        Optional<SensorReading> latestReading = sensorRepository
                .findTopByParzelleIdOrderByTimestampDesc(parzelleId);

        // Heutige Bewässerungsereignisse laden
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<IrrigationEvent> todayEvents = eventRepository
                .findByParzelleIdAndStartTimeAfter(parzelleId, todayStart);

        try {
            return ruleService.evaluate(
                    parzelle,
                    wetter,
                    latestReading.orElse(null),
                    todayEvents
            );
        } catch (Exception e) {
            return createErrorEmpfehlung(e.getMessage());
        }
    }

    /**
     * Berechnet die optimale Bewässerungszeit basierend auf Wettervorhersage
     */
    public OptimalWateringTime calculateOptimalTime(Long parzelleId, Wetterdaten forecast) {
        OlivenParzelle parzelle = parzelleRepository.findById(parzelleId)
                .orElseThrow(() -> new IllegalArgumentException("Parzelle nicht gefunden: " + parzelleId));

        // Analyse der nächsten 24 Stunden (simuliert)
        LocalDateTime now = LocalDateTime.now();
        List<TimeWindow> bestWindows = new ArrayList<>();

        // Heutige erlaubte Zeitfenster
        List<TimeWindow> allowedWindows = parzelle.getAllowedTimeWindows();
        if (allowedWindows.isEmpty()) {
            // Standard: 06:00-09:00 morgens (optimal für Oliven)
            allowedWindows = List.of(
                    new TimeWindow(now.withHour(6).withMinute(0).toLocalTime(),
                            now.withHour(9).withMinute(0).toLocalTime())
            );
        }

        // Bewertung der Zeitfenster (vereinfacht)
        for (TimeWindow window : allowedWindows) {
            double score = evaluateTimeWindow(window, forecast, parzelle);
            if (score > 0.5) { // Mindest-Score
                bestWindows.add(window);
            }
        }

        // Bestes Fenster finden
        TimeWindow bestWindow = bestWindows.stream()
                .findFirst()
                .orElse(allowedWindows.get(0)); // Fallback

        return new OptimalWateringTime(
                parzelleId,
                bestWindow,
                "Optimal: Niedrige Verdunstung, keine Regenvorhersage"
        );
    }

    /**
     * Generiert Langzeit-Empfehlungen für die Woche
     */
    public WeeklyWateringPlan generateWeeklyPlan(Long parzelleId, List<Wetterdaten> weeklyForecast) {
        Map<LocalDate, DailyRecommendation> dailyPlans = new HashMap<>();

        for (int i = 0; i < Math.min(weeklyForecast.size(), 7); i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            Wetterdaten wetter = weeklyForecast.get(i);

            // Simulierte Empfehlung für jeden Tag
            double recommendedAmount = calculateDailyWaterNeed(parzelleId, wetter);
            String reasoning = generateDailyReasoning(wetter);

            dailyPlans.put(date, new DailyRecommendation(
                    date,
                    recommendedAmount,
                    recommendedAmount > 0 ? "Bewässern empfohlen" : "Keine Bewässerung",
                    reasoning
            ));
        }

        // Gesamt-Wasserbedarf der Woche
        double totalWeekly = dailyPlans.values().stream()
                .mapToDouble(DailyRecommendation::getRecommendedAmount)
                .sum();

        return new WeeklyWateringPlan(
                parzelleId,
                LocalDate.now(),
                dailyPlans,
                totalWeekly
        );
    }

    /**
     * Generiert historische Analyse für eine Parzelle
     */
    public HistoricalAnalysis generateHistoricalAnalysis(Long parzelleId, int daysBack) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(daysBack);

        // Historische Sensordaten
        List<SensorReading> historicalReadings = sensorRepository
                .findByParzelleIdAndTimestampAfter(parzelleId, startDate);

        // Historische Bewässerungsereignisse
        List<IrrigationEvent> historicalEvents = eventRepository
                .findByParzelleIdAndStartTimeAfter(parzelleId, startDate);

        // Durchschnittsfeuchte berechnen
        Optional<Double> avgMoisture = sensorRepository
                .findAverageMoistureSince(parzelleId, startDate);

        // Wasserverbrauch berechnen
        double totalWaterUsed = historicalEvents.stream()
                .mapToDouble(IrrigationEvent::getWasserMengeLiter)
                .sum();

        return new HistoricalAnalysis(
                parzelleId,
                startDate.toLocalDate(),
                LocalDate.now(),
                avgMoisture.orElse(0.0),
                totalWaterUsed,
                historicalReadings.size(),
                historicalEvents.size()
        );
    }

    private double evaluateTimeWindow(TimeWindow window, Wetterdaten forecast, OlivenParzelle parzelle) {
        double score = 0.0;

        // Morgens (06:00-09:00) ist optimal für Oliven
        if (window.getStart().getHour() >= 6 && window.getEnd().getHour() <= 9) {
            score += 0.3;
        }

        // Kein Regen erwartet
        if (!forecast.isRainExpected(ruleService.getRainThresholdMm())) {
            score += 0.4;
        }

        // Moderate Temperatur (15-25°C optimal)
        double temp = forecast.getTemperaturCelsius();
        if (temp >= 15 && temp <= 25) {
            score += 0.3;
        }

        return score;
    }

    private double calculateDailyWaterNeed(Long parzelleId, Wetterdaten wetter) {
        // Vereinfachte Berechnung
        OlivenParzelle parzelle = parzelleRepository.findById(parzelleId).orElse(null);
        if (parzelle == null) return 0.0;

        double baseNeed = parzelle.getProfil().getBasisWasserbedarfLiterProTag();
        double weatherFactor = wetter.getWaterNeedFactor();

        return baseNeed * weatherFactor;
    }

    private String generateDailyReasoning(Wetterdaten wetter) {
        List<String> reasons = new ArrayList<>();

        if (wetter.isRainExpected(5.0)) {
            reasons.add("Starker Regen erwartet");
        } else if (wetter.getNiederschlagMm24h() > 0) {
            reasons.add("Leichter Regen erwartet");
        }

        if (wetter.getTemperaturCelsius() > 30) {
            reasons.add("Hohe Temperaturen erhöhen Wasserbedarf");
        }

        if (wetter.getEvapotranspirationMm() > 6) {
            reasons.add("Hohe Verdunstung (ET0=" + wetter.getEvapotranspirationMm() + "mm)");
        }

        return reasons.isEmpty() ? "Optimale Bedingungen" : String.join(", ", reasons);
    }

    private BewaesserungsEmpfehlung createErrorEmpfehlung(String errorMessage) {
        return new BewaesserungsEmpfehlung(
                LocalDateTime.now(),
                0.0,
                EmpfehlungsStufe.FALLBACK,
                List.of("Fehler: " + errorMessage)
        );
    }

    // Hilfsklassen für die Rückgabe

    public static class OptimalWateringTime {
        private final Long parzelleId;
        private final TimeWindow bestWindow;
        private final String reasoning;

        public OptimalWateringTime(Long parzelleId, TimeWindow bestWindow, String reasoning) {
            this.parzelleId = parzelleId;
            this.bestWindow = bestWindow;
            this.reasoning = reasoning;
        }

        public Long getParzelleId() { return parzelleId; }
        public TimeWindow getBestWindow() { return bestWindow; }
        public String getReasoning() { return reasoning; }
    }

    public static class DailyRecommendation {
        private final LocalDate date;
        private final double recommendedAmount;
        private final String action;
        private final String reasoning;

        public DailyRecommendation(LocalDate date, double recommendedAmount,
                                   String action, String reasoning) {
            this.date = date;
            this.recommendedAmount = recommendedAmount;
            this.action = action;
            this.reasoning = reasoning;
        }

        public LocalDate getDate() { return date; }
        public double getRecommendedAmount() { return recommendedAmount; }
        public String getAction() { return action; }
        public String getReasoning() { return reasoning; }
    }

    public static class WeeklyWateringPlan {
        private final Long parzelleId;
        private final LocalDate generatedOn;
        private final Map<LocalDate, DailyRecommendation> dailyPlans;
        private final double totalWaterNeed;

        public WeeklyWateringPlan(Long parzelleId, LocalDate generatedOn,
                                  Map<LocalDate, DailyRecommendation> dailyPlans,
                                  double totalWaterNeed) {
            this.parzelleId = parzelleId;
            this.generatedOn = generatedOn;
            this.dailyPlans = dailyPlans;
            this.totalWaterNeed = totalWaterNeed;
        }

        public Long getParzelleId() { return parzelleId; }
        public LocalDate getGeneratedOn() { return generatedOn; }
        public Map<LocalDate, DailyRecommendation> getDailyPlans() { return dailyPlans; }
        public double getTotalWaterNeed() { return totalWaterNeed; }
    }

    public static class HistoricalAnalysis {
        private final Long parzelleId;
        private final LocalDate periodStart;
        private final LocalDate periodEnd;
        private final double averageMoisture;
        private final double totalWaterUsed;
        private final int readingCount;
        private final int eventCount;

        public HistoricalAnalysis(Long parzelleId, LocalDate periodStart, LocalDate periodEnd,
                                  double averageMoisture, double totalWaterUsed,
                                  int readingCount, int eventCount) {
            this.parzelleId = parzelleId;
            this.periodStart = periodStart;
            this.periodEnd = periodEnd;
            this.averageMoisture = averageMoisture;
            this.totalWaterUsed = totalWaterUsed;
            this.readingCount = readingCount;
            this.eventCount = eventCount;
        }

        public Long getParzelleId() { return parzelleId; }
        public LocalDate getPeriodStart() { return periodStart; }
        public LocalDate getPeriodEnd() { return periodEnd; }
        public double getAverageMoisture() { return averageMoisture; }
        public double getTotalWaterUsed() { return totalWaterUsed; }
        public int getReadingCount() { return readingCount; }
        public int getEventCount() { return eventCount; }

        public double getWaterEfficiency() {
            if (eventCount == 0) return 0.0;
            return averageMoisture / (totalWaterUsed / eventCount);
        }
    }
}
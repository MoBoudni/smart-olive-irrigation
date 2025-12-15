package de.smartolive.irrigation.application;

import de.smartolive.irrigation.domain.model.SensorReading;
import de.smartolive.irrigation.domain.repository.SensorReadingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class SensorAnalysisService {

    private final SensorReadingRepository sensorReadingRepository;

    public SensorAnalysisService(SensorReadingRepository sensorReadingRepository) {
        this.sensorReadingRepository = sensorReadingRepository;
    }

    /**
     * Berechnet den durchschnittlichen Feuchtigkeitswert der letzten 24 Stunden
     */
    public Optional<Double> get24hAverageMoisture(Long parzelleId) {
        LocalDateTime yesterday = LocalDateTime.now().minus(24, ChronoUnit.HOURS);
        return sensorReadingRepository.findAverageMoistureSince(parzelleId, yesterday);
    }

    /**
     * Prüft ob die Feuchte unter einem kritischen Wert liegt
     */
    public boolean isMoistureCritical(Long parzelleId, double criticalThreshold) {
        LocalDateTime lastHour = LocalDateTime.now().minus(1, ChronoUnit.HOURS);
        Optional<Double> avgMoisture = sensorReadingRepository
                .findAverageMoistureSince(parzelleId, lastHour);

        return avgMoisture.isPresent() && avgMoisture.get() < criticalThreshold;
    }

    /**
     * Berechnet Feuchtigkeitstrend (steigend/fallend)
     */
    public double calculateMoistureTrend(Long parzelleId, int hours) {
        LocalDateTime start = LocalDateTime.now().minus(hours, ChronoUnit.HOURS);

        // Erste und letzte Messung im Zeitraum finden
        List<SensorReading> readings = sensorReadingRepository
                .findByParzelleIdAndTimestampAfter(parzelleId, start);

        if (readings.size() < 2) {
            return 0.0; // Nicht genug Daten
        }

        double first = readings.get(readings.size() - 1).getMoisturePercent();
        double last = readings.get(0).getMoisturePercent();

        return last - first; // Positiv = steigend, Negativ = fallend
    }
}
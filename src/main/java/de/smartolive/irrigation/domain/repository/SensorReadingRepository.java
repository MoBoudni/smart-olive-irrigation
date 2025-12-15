package de.smartolive.irrigation.domain.repository;

import de.smartolive.irrigation.domain.model.SensorReading;
import de.smartolive.irrigation.domain.model.SensorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorReadingRepository extends JpaRepository<SensorReading, Long> {

    // 1. Grundlegende Abfragen
    Optional<SensorReading> findTopByParzelleIdOrderByTimestampDesc(Long parzelleId);

    List<SensorReading> findByParzelleIdOrderByTimestampDesc(Long parzelleId);

    List<SensorReading> findByParzelleIdAndTimestampBetween(
            Long parzelleId, LocalDateTime start, LocalDateTime end);

    List<SensorReading> findByParzelleIdAndTimestampAfter(
            Long parzelleId, LocalDateTime timestamp);

    // 2. Durchschnittsberechnungen
    @Query("SELECT AVG(s.moisturePercent) FROM SensorReading s " +
            "WHERE s.parzelleId = :parzelleId AND s.timestamp >= :since " +
            "AND s.status = 'ONLINE'")
    Optional<Double> findAverageMoistureSince(
            @Param("parzelleId") Long parzelleId,
            @Param("since") LocalDateTime since);

    @Query("SELECT AVG(s.temperatureCelsius) FROM SensorReading s " +
            "WHERE s.parzelleId = :parzelleId AND s.timestamp >= :since " +
            "AND s.temperatureCelsius IS NOT NULL")
    Optional<Double> findAverageTemperatureSince(
            @Param("parzelleId") Long parzelleId,
            @Param("since") LocalDateTime since);

    @Query("SELECT AVG(s.moisturePercent) FROM SensorReading s " +
            "WHERE s.parzelleId = :parzelleId " +
            "AND DATE(s.timestamp) = CURRENT_DATE " +
            "AND s.status = 'ONLINE'")
    Optional<Double> findAverageMoistureToday(@Param("parzelleId") Long parzelleId);

    // 3. Aggregierte Statistiken
    @Query("SELECT MIN(s.moisturePercent), MAX(s.moisturePercent), " +
            "AVG(s.moisturePercent) FROM SensorReading s " +
            "WHERE s.parzelleId = :parzelleId AND s.timestamp >= :start")
    Object[] findMoistureStatistics(
            @Param("parzelleId") Long parzelleId,
            @Param("start") LocalDateTime start);

    // 4. Kritische Werte
    @Query("SELECT s FROM SensorReading s WHERE s.parzelleId = :parzelleId " +
            "AND s.moisturePercent < :criticalThreshold " +
            "ORDER BY s.timestamp DESC")
    List<SensorReading> findCriticalReadings(
            @Param("parzelleId") Long parzelleId,
            @Param("criticalThreshold") double criticalThreshold);

    List<SensorReading> findByParzelleIdAndStatus(
            Long parzelleId, SensorStatus status);

    // 5. Zeitbasierte Abfragen
    @Query("SELECT s FROM SensorReading s WHERE s.parzelleId = :parzelleId " +
            "AND s.timestamp >= :timestamp " +
            "ORDER BY s.timestamp ASC")
    List<SensorReading> findReadingsSince(
            @Param("parzelleId") Long parzelleId,
            @Param("timestamp") LocalDateTime timestamp);

    // Letzte N Messungen
    @Query(value = "SELECT * FROM sensor_readings WHERE parzelle_id = :parzelleId " +
            "ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<SensorReading> findLastNReadings(
            @Param("parzelleId") Long parzelleId,
            @Param("limit") int limit);

    // 6. Datenbereinigung
    long deleteByParzelleIdAndTimestampBefore(
            Long parzelleId, LocalDateTime timestamp);

    // 7. Überwachung
    @Query("SELECT COUNT(s) FROM SensorReading s WHERE s.parzelleId = :parzelleId " +
            "AND s.timestamp >= :since AND s.status = 'ONLINE'")
    Long countReadingsSince(
            @Param("parzelleId") Long parzelleId,
            @Param("since") LocalDateTime since);

    @Query("SELECT s.timestamp, s.moisturePercent FROM SensorReading s " +
            "WHERE s.parzelleId = :parzelleId AND s.timestamp >= :start " +
            "ORDER BY s.timestamp ASC")
    List<Object[]> findMoistureTrend(
            @Param("parzelleId") Long parzelleId,
            @Param("start") LocalDateTime start);
}
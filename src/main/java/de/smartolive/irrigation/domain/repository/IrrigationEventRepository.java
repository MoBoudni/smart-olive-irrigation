package de.smartolive.irrigation.domain.repository;

import de.smartolive.irrigation.domain.model.IrrigationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IrrigationEventRepository extends JpaRepository<IrrigationEvent, Long> {

    // Heutige Bewässerungsereignisse
    List<IrrigationEvent> findByParzelleIdAndStartTimeAfter(
            Long parzelleId, LocalDateTime startTime);

    // Aktive (noch laufende) Bewässerungen
    List<IrrigationEvent> findByParzelleIdAndEndTimeIsNull(Long parzelleId);

    // Ereignisse eines bestimmten Typs
    List<IrrigationEvent> findByParzelleIdAndType(
            Long parzelleId, String type);

    // Ereignisse innerhalb eines Zeitraums
    List<IrrigationEvent> findByParzelleIdAndStartTimeBetween(
            Long parzelleId, LocalDateTime start, LocalDateTime end);

    // Letztes Bewässerungsereignis einer Parzelle
    Optional<IrrigationEvent> findTopByParzelleIdOrderByStartTimeDesc(Long parzelleId);

    // Summe der Wassermenge eines Tages
    @Query("SELECT SUM(e.wasserMengeLiter) FROM IrrigationEvent e " +
            "WHERE e.parzelleId = :parzelleId AND e.startTime >= :startOfDay")
    Optional<Double> findTotalWaterAmountToday(
            @Param("parzelleId") Long parzelleId,
            @Param("startOfDay") LocalDateTime startOfDay);

    // Summe der Wassermenge eines Monats
    @Query("SELECT SUM(e.wasserMengeLiter) FROM IrrigationEvent e " +
            "WHERE e.parzelleId = :parzelleId AND e.startTime >= :startOfMonth")
    Optional<Double> findTotalWaterAmountThisMonth(
            @Param("parzelleId") Long parzelleId,
            @Param("startOfMonth") LocalDateTime startOfMonth);

    // Durchschnittliche Bewässerungsdauer
    @Query("SELECT AVG(TIMESTAMPDIFF(MINUTE, e.startTime, e.endTime)) " +
            "FROM IrrigationEvent e WHERE e.parzelleId = :parzelleId " +
            "AND e.endTime IS NOT NULL")
    Optional<Double> findAverageDurationMinutes(@Param("parzelleId") Long parzelleId);

    // Bewässerungshistorie mit Pagination
    @Query("SELECT e FROM IrrigationEvent e WHERE e.parzelleId = :parzelleId " +
            "ORDER BY e.startTime DESC")
    List<IrrigationEvent> findHistoryByParzelleId(
            @Param("parzelleId") Long parzelleId);

    // Automatische Bewässerungen (nicht manuelle)
    @Query("SELECT e FROM IrrigationEvent e WHERE e.parzelleId = :parzelleId " +
            "AND e.type != 'MANUAL' ORDER BY e.startTime DESC")
    List<IrrigationEvent> findAutomaticEventsByParzelleId(
            @Param("parzelleId") Long parzelleId);

    // Bewässerungen mit hohem Wasserverbrauch (über Schwellenwert)
    @Query("SELECT e FROM IrrigationEvent e WHERE e.parzelleId = :parzelleId " +
            "AND e.wasserMengeLiter > :threshold ORDER BY e.wasserMengeLiter DESC")
    List<IrrigationEvent> findHighWaterUsageEvents(
            @Param("parzelleId") Long parzelleId,
            @Param("threshold") double threshold);

    // Abgebrochene Bewässerungen (mit Bemerkung)
    List<IrrigationEvent> findByParzelleIdAndBemerkungenIsNotNull(Long parzelleId);

    // Bewässerungsereignisse nach Auslöser
    List<IrrigationEvent> findByParzelleIdAndTriggeredBy(
            Long parzelleId, String triggeredBy);

    // Anzahl der Bewässerungen pro Tag
    @Query("SELECT DATE(e.startTime), COUNT(e) FROM IrrigationEvent e " +
            "WHERE e.parzelleId = :parzelleId AND e.startTime >= :startDate " +
            "GROUP BY DATE(e.startTime) ORDER BY DATE(e.startTime) DESC")
    List<Object[]> countEventsPerDay(
            @Param("parzelleId") Long parzelleId,
            @Param("startDate") LocalDateTime startDate);

    // Wasserbilanz (Ein/Aus) für einen Zeitraum
    @Query("SELECT SUM(e.wasserMengeLiter) FROM IrrigationEvent e " +
            "WHERE e.parzelleId = :parzelleId AND e.startTime BETWEEN :start AND :end")
    Optional<Double> calculateWaterBalance(
            @Param("parzelleId") Long parzelleId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
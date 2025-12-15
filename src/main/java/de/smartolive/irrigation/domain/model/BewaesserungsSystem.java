package de.smartolive.irrigation.domain.model;

import de.smartolive.irrigation.domain.exception.DomainException;
import de.smartolive.irrigation.domain.valueobject.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Aggregate Root: Repräsentiert das gesamte Bewässerungssystem
 * koordiniert mehrere Parzellen und stellt System-weite Funktionen bereit
 */
public class BewaesserungsSystem {

    private final String systemId;
    private final String name;
    private final LocalDateTime installationDate;
    private final SystemStatus status;
    private final Set<Long> parzellenIds; // Referenzen zu OlivenParzelle Aggregates
    private final SystemConfiguration configuration;

    // Privater Konstruktor für Factory-Methode
    private BewaesserungsSystem(String systemId, String name,
                                SystemConfiguration configuration) {
        this.systemId = systemId;
        this.name = name;
        this.installationDate = LocalDateTime.now();
        this.status = SystemStatus.AKTIV;
        this.parzellenIds = new HashSet<>();
        this.configuration = configuration;
    }

    /**
     * Factory-Methode zur Erstellung eines neuen Systems
     */
    public static BewaesserungsSystem create(String name, SystemConfiguration config)
            throws DomainException {

        if (name == null || name.isBlank()) {
            throw new DomainException("Systemname darf nicht leer sein");
        }
        if (config == null) {
            throw new DomainException("Konfiguration muss angegeben werden");
        }

        String systemId = "SYS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BewaesserungsSystem(systemId, name, config);
    }

    // Fachliche Methoden

    /**
     * Fügt eine Parzelle zum System hinzu
     */
    public void addParzelle(Long parzelleId) throws DomainException {
        if (parzelleId == null) {
            throw new DomainException("Parzellen-ID darf nicht null sein");
        }

        if (parzellenIds.size() >= configuration.getMaxParzellen()) {
            throw new DomainException("Maximale Anzahl an Parzellen erreicht: " +
                    configuration.getMaxParzellen());
        }

        parzellenIds.add(parzelleId);
    }

    /**
     * Entfernt eine Parzelle aus dem System
     */
    public void removeParzelle(Long parzelleId) {
        parzellenIds.remove(parzelleId);
    }

    /**
     * Prüft ob das System im Automatik-Modus laufen kann
     */
    public boolean canRunAutomaticMode() {
        return status == SystemStatus.AKTIV &&
                !parzellenIds.isEmpty() &&
                configuration.isAutomaticModeEnabled();
    }

    /**
     * Berechnet den gesamten Wasserbedarf aller Parzellen
     */
    public SystemWaterNeed calculateTotalWaterNeed(Map<Long, Double> waterNeedPerParzelle) {
        double totalNeed = waterNeedPerParzelle.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();

        double dailyLimit = configuration.getDailyWaterLimitLiters();
        boolean limitExceeded = totalNeed > dailyLimit;

        return new SystemWaterNeed(
                totalNeed,
                dailyLimit,
                limitExceeded,
                limitExceeded ? "Wasserlimit überschritten" : "Innerhalb der Limits"
        );
    }

    /**
     * Generiert System-Statusbericht
     */
    public SystemStatusReport generateStatusReport(int activeParzellen,
                                                   int errorParzellen,
                                                   double totalWaterUsedToday) {

        SystemHealth health = calculateSystemHealth(activeParzellen, errorParzellen);

        return new SystemStatusReport(
                systemId,
                LocalDateTime.now(),
                status,
                health,
                parzellenIds.size(),
                activeParzellen,
                errorParzellen,
                totalWaterUsedToday,
                configuration.getDailyWaterLimitLiters(),
                generateRecommendations(health)
        );
    }

    private SystemHealth calculateSystemHealth(int activeParzellen, int errorParzellen) {
        int totalParzellen = parzellenIds.size();
        if (totalParzellen == 0) return SystemHealth.UNKNOWN;

        double successRate = (double) activeParzellen / totalParzellen;

        if (errorParzellen > 0) {
            return SystemHealth.CRITICAL;
        } else if (successRate < 0.8) {
            return SystemHealth.WARNING;
        } else {
            return SystemHealth.HEALTHY;
        }
    }

    private List<String> generateRecommendations(SystemHealth health) {
        List<String> recommendations = new ArrayList<>();

        switch (health) {
            case CRITICAL:
                recommendations.add("⚠️ KRITISCH: Parzellen mit Fehlern überprüfen");
                recommendations.add("🔧 Sensor-Kalibrierung durchführen");
                break;
            case WARNING:
                recommendations.add("⚠️ Einige Parzellen benötigen Aufmerksamkeit");
                recommendations.add("📊 Sensordaten überprüfen");
                break;
            case HEALTHY:
                recommendations.add("✅ System läuft optimal");
                recommendations.add("📈 Regelmäßige Wartung durchführen");
                break;
            case UNKNOWN:
                recommendations.add("❓ Keine Parzellen konfiguriert");
                recommendations.add("➕ Parzellen zum System hinzufügen");
                break;
        }

        return recommendations;
    }

    // Getter
    public String getSystemId() { return systemId; }
    public String getName() { return name; }
    public LocalDateTime getInstallationDate() { return installationDate; }
    public SystemStatus getStatus() { return status; }
    public Set<Long> getParzellenIds() { return Collections.unmodifiableSet(parzellenIds); }
    public SystemConfiguration getConfiguration() { return configuration; }

    // Value Objects für dieses Aggregate

    public static class SystemWaterNeed {
        private final double totalNeedLiters;
        private final double dailyLimitLiters;
        private final boolean limitExceeded;
        private final String message;

        public SystemWaterNeed(double totalNeedLiters, double dailyLimitLiters,
                               boolean limitExceeded, String message) {
            this.totalNeedLiters = totalNeedLiters;
            this.dailyLimitLiters = dailyLimitLiters;
            this.limitExceeded = limitExceeded;
            this.message = message;
        }

        public double getTotalNeedLiters() { return totalNeedLiters; }
        public double getDailyLimitLiters() { return dailyLimitLiters; }
        public boolean isLimitExceeded() { return limitExceeded; }
        public String getMessage() { return message; }
    }

    public static class SystemStatusReport {
        private final String systemId;
        private final LocalDateTime generatedAt;
        private final SystemStatus status;
        private final SystemHealth health;
        private final int totalParzellen;
        private final int activeParzellen;
        private final int errorParzellen;
        private final double waterUsedToday;
        private final double dailyWaterLimit;
        private final List<String> recommendations;

        public SystemStatusReport(String systemId, LocalDateTime generatedAt,
                                  SystemStatus status, SystemHealth health,
                                  int totalParzellen, int activeParzellen,
                                  int errorParzellen, double waterUsedToday,
                                  double dailyWaterLimit, List<String> recommendations) {
            this.systemId = systemId;
            this.generatedAt = generatedAt;
            this.status = status;
            this.health = health;
            this.totalParzellen = totalParzellen;
            this.activeParzellen = activeParzellen;
            this.errorParzellen = errorParzellen;
            this.waterUsedToday = waterUsedToday;
            this.dailyWaterLimit = dailyWaterLimit;
            this.recommendations = recommendations;
        }

        // Getter
        public String getSystemId() { return systemId; }
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public SystemStatus getStatus() { return status; }
        public SystemHealth getHealth() { return health; }
        public int getTotalParzellen() { return totalParzellen; }
        public int getActiveParzellen() { return activeParzellen; }
        public int getErrorParzellen() { return errorParzellen; }
        public double getWaterUsedToday() { return waterUsedToday; }
        public double getDailyWaterLimit() { return dailyWaterLimit; }
        public List<String> getRecommendations() { return recommendations; }
    }
}
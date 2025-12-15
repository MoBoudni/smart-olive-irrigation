# Technischer Entwurf für das Oliven-Bewässerungssystem

## 1. Systemarchitektur
- **Schichtenarchitektur:**
    - Präsentationsschicht (JavaFX)
    - Anwendungslogik (Spring Boot)
    - Datenzugriffsschicht (JPA/Hibernate)
    - Datenbank (MySQL)

## 2. Datenbankdesign
- **Tabellen:**
    - `Zones` (id, name, moisture_threshold)
    - `Sensors` (id, zone_id, type, value)
    - `WateringSchedules` (id, zone_id, start_time, duration)

## 3. Schnittstellen
- **REST-API:**
    - Endpunkte für Wetterdatenabfrage
    - Endpunkte für die Steuerung der Bewässerung

## 4. Technologie-Stack
- **Backend:** Java, Spring Boot
- **Frontend:** JavaFX
- **Datenbank:** MySQL, H2 (für Tests)

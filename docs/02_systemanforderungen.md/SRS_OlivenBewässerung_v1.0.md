# Systemanforderungen (SRS) für das Oliven-Bewässerungssystem

## 1. Einführung
- Zweck: Detaillierte Beschreibung der Systemanforderungen

## 2. Funktionale Anforderungen
- **Bewässerungssteuerung:**
    - Automatische Steuerung basierend auf Bodenfeuchte und Wetterdaten
    - Manuelle Steuerung durch den Benutzer
- **Zonenverwaltung:**
    - Unterstützung mehrerer Bewässerungszonen
    - Individuelle Einstellungen pro Zone

## 3. Nicht-funktionale Anforderungen
- **Leistung:**
    - Reaktionszeit < 2 Sekunden
    - Unterstützung von bis zu 50 Zonen
- **Sicherheit:**
    - Authentifizierung für Benutzer
    - Verschlüsselung der Kommunikationsdaten

## 4. Schnittstellen
- REST-API für die Kommunikation mit Wetterdiensten
- Benutzeroberfläche für die Konfiguration und Überwachung

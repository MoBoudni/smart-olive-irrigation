-- Test-Daten für Entwicklung
INSERT INTO oliven_parzellen (name, sorte, boden_typ, alter_jahre, bio_zertifiziert, basis_wasserbedarf, feuchte_untergrenze, feuchte_obergrenze, status) VALUES
('Nordhang', 'Picual', 'LEHMIG', 15, true, 25.0, 30.0, 60.0, 'RUHE'),
('Südhügel', 'Koroneiki', 'SANDIG', 8, false, 20.0, 25.0, 55.0, 'RUHE'),
('Talboden', 'Arbequina', 'TONIG', 25, true, 18.0, 35.0, 65.0, 'BEWAESSERUNG_AKTIV')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Zeitfenster für Nordhang
INSERT INTO time_windows (parzelle_id, start_time, end_time) VALUES
(1, '06:00:00', '09:00:00'),
(1, '18:00:00', '21:00:00')
ON DUPLICATE KEY UPDATE start_time = VALUES(start_time);

-- Beispiel-Sensor-Messungen
INSERT INTO sensor_readings (parzelle_id, moisture_percent, temperature_celsius, battery_level) VALUES
(1, 45.5, 22.3, 85),
(2, 38.2, 24.1, 92),
(3, 52.7, 20.8, 78)
ON DUPLICATE KEY UPDATE moisture_percent = VALUES(moisture_percent);
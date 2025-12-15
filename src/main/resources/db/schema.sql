-- Tabelle für Olivenparzellen
CREATE TABLE IF NOT EXISTS oliven_parzellen (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    sorte VARCHAR(100),
    boden_typ VARCHAR(50),
    alter_jahre INT,
    bio_zertifiziert BOOLEAN,
    basis_wasserbedarf DOUBLE,
    feuchte_untergrenze DOUBLE,
    feuchte_obergrenze DOUBLE,
    max_daily_duration_minutes INT DEFAULT 60,
    status VARCHAR(50) DEFAULT 'RUHE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabelle für Zeitfenster
CREATE TABLE IF NOT EXISTS time_windows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parzelle_id BIGINT NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (parzelle_id) REFERENCES oliven_parzellen(id) ON DELETE CASCADE
);

-- Tabelle für Bewässerungsereignisse
CREATE TABLE IF NOT EXISTS irrigation_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parzelle_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    wasser_menge_liter DOUBLE NOT NULL,
    type VARCHAR(50) NOT NULL,
    triggered_by VARCHAR(100) NOT NULL,
    bemerkungen TEXT,
    FOREIGN KEY (parzelle_id) REFERENCES oliven_parzellen(id)
);

-- Tabelle für Sensor-Messungen
CREATE TABLE IF NOT EXISTS sensor_readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parzelle_id BIGINT NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    moisture_percent DOUBLE,
    temperature_celsius DOUBLE,
    ec_value DOUBLE,
    ph_value DOUBLE,
    battery_level INT,
    status VARCHAR(50) DEFAULT 'ONLINE',
    FOREIGN KEY (parzelle_id) REFERENCES oliven_parzellen(id)
);

-- Indexe für Performance
CREATE INDEX idx_parzelle_status ON oliven_parzellen(status);
CREATE INDEX idx_events_parzelle_time ON irrigation_events(parzelle_id, start_time);
CREATE INDEX idx_sensor_parzelle_time ON sensor_readings(parzelle_id, timestamp);
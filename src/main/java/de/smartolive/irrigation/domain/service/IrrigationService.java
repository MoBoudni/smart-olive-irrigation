package de.smartolive.irrigation.domain.service;

import org.springframework.stereotype.Service;

@Service
public class IrrigationService {

    public String getStatus() {
        return "H2 DB ready - Interval: 10min, Regen: 1.0mm";
    }

    public void startIrrigation() {
        System.out.println("🚿 Bewässerung gestartet! (2 Min Test)");
    }
}

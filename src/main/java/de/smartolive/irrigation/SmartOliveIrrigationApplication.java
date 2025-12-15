package de.smartolive.irrigation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartOliveIrrigationApplication {
    public static void main(String[] args) {
        // Standard: H2, oder Profil aus Umgebungsvariable
        SpringApplication.run(SmartOliveIrrigationApplication.class, args);
    }
}
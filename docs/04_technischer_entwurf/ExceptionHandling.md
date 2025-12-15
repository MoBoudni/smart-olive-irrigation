# Exception Handling für das Oliven-Bewässerungssystem

## 1. Einführung
Das Exception Handling beschreibt, wie das System mit **Fehlern und Ausnahmen** umgeht, um die Stabilität und Benutzerfreundlichkeit zu gewährleisten. Es umfasst die Behandlung von Fehlern in der Anwendungslogik, Datenbankzugriffen und externen Schnittstellen.

---

## 2. Grundsätze des Exception Handlings

### **2.1 Konsistente Fehlerbehandlung**
- Alle Ausnahmen sollten **konsistent** behandelt werden, um die Wartbarkeit des Codes zu verbessern.
- Verwende **benutzerfreundliche Fehlermeldungen**, um den Benutzer über Probleme zu informieren.

### **2.2 Logging**
- Alle Ausnahmen sollten **protokolliert** werden, um die Fehleranalyse zu erleichtern.
- Verwende **Logback** oder ein anderes Logging-Framework, um Fehler zu protokollieren.

### **2.3 Wiederherstellung**
- Das System sollte versuchen, sich von Fehlern zu **erholen**, wo immer dies möglich ist.
- Falls eine Wiederherstellung nicht möglich ist, sollte das System **graceful shutdown** durchführen.

---

## 3. Arten von Ausnahmen

### **3.1 Technische Ausnahmen**
- **Datenbankfehler** (z. B. Verbindung zur Datenbank fehlgeschlagen)
- **Netzwerkfehler** (z. B. Verbindung zur Wetter-API fehlgeschlagen)
- **Konfigurationsfehler** (z. B. ungültige Konfiguration in `application.properties`)

### **3.2 Fachliche Ausnahmen**
- **Ungültige Benutzereingaben** (z. B. ungültige Bewässerungsdauer)
- **Verletzung von Geschäftsregeln** (z. B. Bewässerung außerhalb der erlaubten Zeiten)

---

## 4. Exception Handling-Strategie

### **4.1 Globale Exception Handler**
- Verwende **globale Exception Handler** in Spring Boot, um Ausnahmen zentral zu behandeln.
- Beispiel für einen globalen Exception Handler:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error("Ein Fehler ist aufgetreten: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ein interner Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        log.warn("Validierungsfehler: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}

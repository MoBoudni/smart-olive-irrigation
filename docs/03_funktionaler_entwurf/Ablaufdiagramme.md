# Ablaufdiagramme für das Oliven-Bewässerungssystem

## 1. Einführung
Ablaufdiagramme beschreiben die **Schritt-für-Schritt-Prozesse** im Oliven-Bewässerungssystem. Sie helfen, die Logik und Interaktionen zwischen den Komponenten zu visualisieren.

---

## 2. Ablaufdiagramm: Automatische Bewässerung

### **2.1 Beschreibung**
Dieses Ablaufdiagramm zeigt den Prozess der **automatischen Bewässerung** basierend auf Bodenfeuchte und Wettervorhersage.

### **2.2 Diagramm**

```mermaid
flowchart TD
    A[Start] --> B[Bodenfeuchte messen]
    B --> C{Sensorwert < Schwellenwert?}
    C -->|Ja| D[Wettervorhersage prüfen]
    C -->|Nein| E[Keine Bewässerung]
    D --> F{Regen vorhergesagt?}
    F -->|Ja| E
    F -->|Nein| G[Bewässerung starten]
    G --> H[Bewässerungsdauer abwarten]
    H --> I[Bewässerung beenden]
    I --> A

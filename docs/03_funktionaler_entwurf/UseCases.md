# Use Cases für das Oliven-Bewässerungssystem

## 1. Use-Case-Diagramm
![Use-Case-Diagramm](diagramme/use_case_diagramm.png)

## 2. Use-Case-Beschreibungen

### UC1: Bewässerung starten
- **Aktor:** Benutzer
- **Beschreibung:** Der Benutzer startet die Bewässerung manuell.
- **Schritte:**
    1. Benutzer wählt Zone aus.
    2. Benutzer klickt auf "Bewässerung starten".
    3. System startet die Bewässerung für die ausgewählte Zone.

### UC2: Bewässerung basierend auf Bodenfeuchte
- **Aktor:** System
- **Beschreibung:** Das System startet die Bewässerung automatisch, wenn die Bodenfeuchte unter einen bestimmten Wert fällt.
- **Schritte:**
    1. System misst die Bodenfeuchte.
    2. Wenn die Bodenfeuchte unter den Schwellenwert fällt, startet das System die Bewässerung.

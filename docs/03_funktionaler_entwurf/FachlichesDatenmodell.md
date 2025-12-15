# Fachliches Datenmodell für das Oliven-Bewässerungssystem

## 1. Einführung
Das fachliche Datenmodell beschreibt die **logischen Datenstrukturen** und deren Beziehungen, die für das Oliven-Bewässerungssystem 
relevant sind. Es dient als Grundlage für das spätere technische Datenbankdesign.

---

## 2. Entitäten und Beziehungen

### **2.1 Entitäten**

| Entität               | Beschreibung                                                                                |
|-----------------------|---------------------------------------------------------------------------------------------|
| **Zone**              | Repräsentiert eine Bewässerungszone (z. B. ein Beet oder Feld).                             |
| **Sensor**            | Misst Umweltparameter wie Bodenfeuchte oder Temperatur in einer Zone.                       |
| **Bewässerungsplan**  | Definiert, wann und wie lange eine Zone bewässert werden soll.                              |
| **Wettervorhersage**  | Enthält Wetterdaten, die die Bewässerung beeinflussen (z. B. Regenvorhersage).              |
| **Benutzer**          | Repräsentiert einen Benutzer des Systems, der Zonen und Bewässerungspläne konfiguriert.     |

---

### **2.2 Beziehungen zwischen Entitäten**

| Beziehung                                            | Beschreibung                                                                                |
|------------------------------------------------------|---------------------------------------------------------------------------------------------|
| **Zone hat Sensoren**                                | Eine Zone kann mehrere Sensoren enthalten, die Umweltparameter messen.                      |
| **Zone hat Bewässerungspläne**                       | Eine Zone kann mehrere Bewässerungspläne haben, die definieren, wann bewässert wird.        |
| **Bewässerungsplan berücksichtigt Wettervorhersage** | Ein Bewässerungsplan kann Wettervorhersagen berücksichtigen, um die Bewässerung anzupassen. |
| **Benutzer verwaltet Zonen**                         | Ein Benutzer kann mehrere Zonen verwalten und konfigurieren.                                |

---

### **3. Fachliches Entity-Relationship-Diagramm (ERD)**

Hier ist ein **fachliches ER-Diagramm**, das die Beziehungen zwischen den Entitäten darstellt:

```plaintext
+------------+       +----------------+       +-----------------------+
|   Zone     |       |    Sensor      |       |   Bewässerungsplan    |
+------------+       +----------------+       +-----------------------+
| - id       |<>---->| - id           |       | - id                  |
| - name     |       | - zone_id      |       | - zone_id             |
| - location |       | - type         |       | - start_time          |
+------------+       | - value        |       | - duration            |
                     +----------------+       +-----------------------+
                    | Wettervorhersage       |
                    +----------------------------+
                    | - id                       |
                    | - datum                    |
                    | - regen_wahrscheinlichkeit |
                    +----------------------------+

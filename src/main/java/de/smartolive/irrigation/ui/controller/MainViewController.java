package de.smartolive.irrigation.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller für die Hauptansicht.
 * Wird später mit echten Daten aus der Domain-Schicht befüllt.
 */
public class MainViewController implements Initializable {

    @FXML private TableView<?> parzellenTable;
    @FXML private Label statusLabel;
    @FXML private Label lastUpdateLabel;
    @FXML private Label noDataLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initiale Platzhalter-Logik
        updateLastUpdateTime();
        statusLabel.setText("System bereit");
        noDataLabel.setVisible(true);
        parzellenTable.setVisible(false);
    }

    @FXML
    private void onCreateParzelle() {
        // TODO: Dialog öffnen für neue Parzelle
        showInfo("Funktion 'Neue Parzelle' wird implementiert...");
    }

    @FXML
    private void onRefresh() {
        updateLastUpdateTime();
        statusLabel.setText("Daten aktualisiert");
        // TODO: Daten aus Repository laden und TableView befüllen
        showInfo("Aktualisierungsvorgang wird simuliert...");
    }

    private void updateLastUpdateTime() {
        String formatted = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        lastUpdateLabel.setText(formatted);
    }

    private void showInfo(String message) {
        // Platzhalter – später Alert oder Toast
        statusLabel.setText(message);
    }
}
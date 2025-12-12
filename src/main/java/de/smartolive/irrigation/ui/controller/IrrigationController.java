package de.smartolive.irrigation.ui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class IrrigationController implements Initializable {

    @FXML private Label statusLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button refreshButton;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;
    @FXML private VBox progressContainer;

    private Timeline irrigationTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("System bereit");
        stopButton.setDisable(true);
        progressContainer.setVisible(false);
    }

    @FXML
    private void onStartClicked() {
        statusLabel.setText("Bewässerung läuft...");
        statusLabel.setStyle("-fx-text-fill: orange;");

        startButton.setDisable(true);
        stopButton.setDisable(false);
        progressContainer.setVisible(true);
        progressLabel.setText("Simuliere Bewässerung (30 Sekunden)...");

        // Simulierte Bewässerung: 30 Sekunden ProgressBar
        progressBar.setProgress(0);
        irrigationTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), e -> {
                    double progress = progressBar.getProgress() + 0.0333; // ~30 Schritte
                    progressBar.setProgress(Math.min(progress, 1.0));
                    if (progress >= 1.0) {
                        finishIrrigation();
                    }
                })
        );
        irrigationTimeline.setCycleCount(30);
        irrigationTimeline.play();
    }

    @FXML
    private void onStopClicked() {
        if (irrigationTimeline != null) {
            irrigationTimeline.stop();
        }
        finishIrrigation();
        statusLabel.setText("Bewässerung manuell gestoppt");
        statusLabel.setStyle("-fx-text-fill: var(--danger);");
    }

    private void finishIrrigation() {
        progressBar.setProgress(1.0);
        progressContainer.setVisible(false);
        startButton.setDisable(false);
        stopButton.setDisable(true);
        statusLabel.setText("Bewässerung abgeschlossen");
        statusLabel.setStyle("-fx-text-fill: var(--success);");
    }

    @FXML
    private void onRefreshClicked() {
        statusLabel.setText("Daten aktualisiert");
        statusLabel.setStyle("-fx-text-fill: var(--primary);");

        // Kurze Animation
        new Timeline(new KeyFrame(Duration.seconds(1.5), e ->
                statusLabel.setText("System bereit")
        )).play();
    }

    @FXML
    private void onSettingsClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Einstellungen");
        alert.setHeaderText("Einstellungen (in Entwicklung)");
        alert.setContentText("Hier kommen später:\n• Parzellen verwalten\n• Zeitfenster\n• Wetter-API\n• Sensor-Kalibrierung");
        alert.showAndWait();
    }
}
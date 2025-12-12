package de.smartolive.irrigation.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Einstiegspunkt der JavaFX-Anwendung.
 * Lädt die Hauptansicht (irrigation-view.fxml) und bindet das CSS-Stylesheet ein.
 */
public class MainApp extends Application {

    private static final String FXML_PATH = "/fxml/irrigation-view.fxml";
    private static final String CSS_PATH = "/css/application.css";
    private static final String APP_TITLE = "Smart Olive Irrigation – Intelligenter Bewässerungs-Assistent";

    private static final double WIDTH = 700;
    private static final double HEIGHT = 470;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // FXML laden
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);

        // CSS automatisch laden
        String css = getClass().getResource(CSS_PATH).toExternalForm();
        scene.getStylesheets().add(css);

        // Stage konfigurieren
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.centerOnScreen();

        // Optional: Icon hinzufügen (falls du später ein App-Icon hast)
        // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/app-icon.png")));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
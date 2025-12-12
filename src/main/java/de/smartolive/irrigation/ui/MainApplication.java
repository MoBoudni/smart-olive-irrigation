package de.smartolive.irrigation.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Einstiegspunkt der JavaFX-Anwendung.
 * Lädt die Hauptansicht (MainView.fxml).
 */
public class MainApplication extends Application {

    private static final String MAIN_FXML = "/fxml/MainView.fxml";
    private static final String APP_TITLE = "Smart Olive Irrigation – Intelligenter Bewässerungs-Assistent";
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_FXML));
        Parent root = loader.load();

        Scene scene = new Scene(root, WIDTH, HEIGHT);

        // Optional: CSS hinzufügen (später in resources/css/application.css)
        // scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
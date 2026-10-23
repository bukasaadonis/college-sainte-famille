package com.mycompany.college.saint.raphael;

import com.mycompany.college.saint.raphael.utils.AlertUtils;
import com.mycompany.college.saint.raphael.utils.BrandingUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Collège Sainte Famille — Gestion scolaire");
        BrandingUtils.applyStageIcon(primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            closeApplication();
        });
        showSplash();
        primaryStage.show();
    }

    public static void showSplash() {
        if (!loadScene("/fxml/SplashView.fxml", 760, 440)) return;

        resetStageConstraints();
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(760);
        primaryStage.setMinHeight(440);
        primaryStage.setMaxWidth(760);
        primaryStage.setMaxHeight(440);
        primaryStage.setMaximized(false);
        primaryStage.setWidth(760);
        primaryStage.setHeight(440);
        primaryStage.centerOnScreen();
    }

    public static void showLogin() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double availableWidth = Math.max(640, bounds.getWidth() - 24);
        double availableHeight = Math.max(480, bounds.getHeight() - 24);
        double width = Math.min(availableWidth, Math.min(1040, Math.max(860, bounds.getWidth() * 0.72)));
        double height = Math.min(availableHeight, Math.min(690, Math.max(560, bounds.getHeight() * 0.78)));

        // La scène est chargée avant de modifier la taille de la fenêtre. En cas
        // d'erreur FXML, l'ancienne interface reste donc intacte et bien dimensionnée.
        if (!loadScene("/fxml/LoginView.fxml", width, height)) return;

        resetStageConstraints();
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(Math.min(860, width));
        primaryStage.setMinHeight(Math.min(560, height));
        primaryStage.setMaxWidth(1180);
        primaryStage.setMaxHeight(780);
        primaryStage.setMaximized(false);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
    }

    /**
     * Ouvre l'espace principal.
     *
     * @return true si l'interface a été chargée, false sinon.
     */
    public static boolean showMain() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double availableWidth = Math.max(760, bounds.getWidth() - 16);
        double availableHeight = Math.max(560, bounds.getHeight() - 16);
        double minimumWidth = Math.min(1180, availableWidth);
        double minimumHeight = Math.min(720, availableHeight);
        double width = Math.min(availableWidth, Math.min(1480, Math.max(minimumWidth, bounds.getWidth() * 0.92)));
        double height = Math.min(availableHeight, Math.min(920, Math.max(minimumHeight, bounds.getHeight() * 0.90)));

        // Charger d'abord le FXML évite de déformer l'écran de connexion lorsque
        // la vue principale contient une erreur.
        if (!loadScene("/fxml/MainLayout.fxml", width, height)) return false;

        resetStageConstraints();
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(minimumWidth);
        primaryStage.setMinHeight(minimumHeight);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
        primaryStage.setMaximized(false);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
        return true;
    }

    /**
     * Charge complètement la vue avant de remplacer la scène affichée.
     */
    private static boolean loadScene(String resource, double width, double height) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(resource),
                    "Ressource introuvable : " + resource));
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(Objects.requireNonNull(
                    App.class.getResource("/css/app.css"),
                    "Feuille de style introuvable : /css/app.css").toExternalForm());
            primaryStage.setScene(scene);
            return true;
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            Throwable cause = deepestCause(e);
            String detail = cause.getMessage();
            if (detail == null || detail.isBlank()) detail = cause.getClass().getSimpleName();
            AlertUtils.showError(
                    "Erreur de démarrage",
                    "Impossible de charger l’interface " + resource + "\n\nDétail : " + detail
            );
            return false;
        }
    }

    private static Throwable deepestCause(Throwable error) {
        Throwable result = error;
        while (result.getCause() != null && result.getCause() != result) {
            result = result.getCause();
        }
        return result;
    }

    private static void resetStageConstraints() {
        primaryStage.setMinWidth(0);
        primaryStage.setMinHeight(0);
        primaryStage.setMaxWidth(Double.MAX_VALUE);
        primaryStage.setMaxHeight(Double.MAX_VALUE);
    }

    public static void closeApplication() {
        javafx.application.Platform.exit();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}

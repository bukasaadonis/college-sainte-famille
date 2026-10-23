package com.mycompany.college.saint.raphael.controllers;

import com.mycompany.college.saint.raphael.App;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class SplashController {
    @FXML private StackPane splashRoot;
    @FXML private Circle orbitOne;
    @FXML private Circle orbitTwo;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Label percentLabel;

    @FXML
    public void initialize() {
        Platform.runLater(this::playAnimation);
    }

    private void playAnimation() {
        splashRoot.setOpacity(0);
        FadeTransition entrance = new FadeTransition(Duration.millis(650), splashRoot);
        entrance.setFromValue(0);
        entrance.setToValue(1);

        RotateTransition rotateOne = new RotateTransition(Duration.seconds(4), orbitOne);
        rotateOne.setByAngle(360);
        rotateOne.setCycleCount(Animation.INDEFINITE);
        rotateOne.setInterpolator(Interpolator.LINEAR);

        RotateTransition rotateTwo = new RotateTransition(Duration.seconds(6), orbitTwo);
        rotateTwo.setByAngle(-360);
        rotateTwo.setCycleCount(Animation.INDEFINITE);
        rotateTwo.setInterpolator(Interpolator.LINEAR);

        Timeline progress = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(progressBar.progressProperty(), 0),
                        new KeyValue(percentLabel.textProperty(), "0 %")),
                new KeyFrame(Duration.seconds(2), e -> statusLabel.setText("Préparation de l’environnement…")),
                new KeyFrame(Duration.seconds(4), e -> statusLabel.setText("Chargement des modules scolaires…")),
                new KeyFrame(Duration.seconds(6), e -> statusLabel.setText("Initialisation de la sécurité…")),
                new KeyFrame(Duration.seconds(8), e -> statusLabel.setText("Finalisation de l’interface…")),
                new KeyFrame(Duration.seconds(10),
                        new KeyValue(progressBar.progressProperty(), 1),
                        new KeyValue(percentLabel.textProperty(), "100 %"))
        );

        Timeline percent = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            int value = (int) Math.round(progressBar.getProgress() * 100);
            percentLabel.setText(Math.max(0, Math.min(100, value)) + " %");
        }));
        percent.setCycleCount(100);

        progress.setOnFinished(e -> {
            FadeTransition exit = new FadeTransition(Duration.millis(350), splashRoot);
            exit.setFromValue(1);
            exit.setToValue(0);
            exit.setOnFinished(done -> App.showLogin());
            exit.play();
        });

        entrance.play();
        rotateOne.play();
        rotateTwo.play();
        progress.play();
        percent.play();
    }
}

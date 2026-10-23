package com.mycompany.college.saint.raphael.controllers;

import com.mycompany.college.saint.raphael.App;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.services.AuthService;
import com.mycompany.college.saint.raphael.utils.WindowUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

public class LoginController {
    @FXML private HBox titleBar;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private Label databaseLabel;
    @FXML private ProgressIndicator loginProgress;

    @FXML
    public void initialize() {
        passwordVisibleField.setManaged(false);
        passwordVisibleField.setVisible(false);
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        loginProgress.setVisible(false);
        loginProgress.setManaged(false);
        Platform.runLater(() -> {
            WindowUtils.installDrag(titleBar, App.getPrimaryStage());
            loginField.requestFocus();
        });
        checkDatabaseAsync();
    }

    private void checkDatabaseAsync() {
        databaseLabel.setText("Vérification de la base de données…");
        databaseLabel.getStyleClass().removeAll("status-online", "status-offline");
        Thread worker = new Thread(() -> {
            boolean ok = DatabaseConfig.getInstance().testConnection();
            Platform.runLater(() -> {
                databaseLabel.setText(ok ? "Base de données connectée" : "Base de données indisponible — vérifiez XAMPP et le port 3307");
                databaseLabel.getStyleClass().add(ok ? "status-online" : "status-offline");
            });
        }, "database-check");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    private void togglePassword() {
        boolean visible = showPasswordCheck.isSelected();
        passwordVisibleField.setManaged(visible);
        passwordVisibleField.setVisible(visible);
        passwordField.setManaged(!visible);
        passwordField.setVisible(!visible);
        if (visible) {
            passwordVisibleField.positionCaret(passwordVisibleField.getText().length());
            passwordVisibleField.requestFocus();
        } else {
            passwordField.positionCaret(passwordField.getText().length());
            passwordField.requestFocus();
        }
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText() == null ? "" : loginField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        messageLabel.setText("");
        if (login.isBlank() || password.isBlank()) {
            messageLabel.setText("Saisissez votre identifiant et votre mot de passe.");
            return;
        }

        setBusy(true);
        Thread worker = new Thread(() -> {
            try {
                AuthService.getInstance().login(login, password);
                Platform.runLater(() -> {
                    if (!App.showMain()) {
                        // L'authentification a réussi, mais l'interface principale
                        // n'a pas pu être ouverte : on ferme proprement la session
                        // et on rend le formulaire de connexion à nouveau utilisable.
                        AuthService.getInstance().logout();
                        messageLabel.setText("L’interface principale n’a pas pu être chargée. Consultez le détail affiché.");
                        setBusy(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    messageLabel.setText(e.getMessage() == null ? "Connexion impossible." : e.getMessage());
                    passwordField.clear();
                    loginField.requestFocus();
                    setBusy(false);
                });
            }
        }, "authentication");
        worker.setDaemon(true);
        worker.start();
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        loginField.setDisable(busy);
        passwordField.setDisable(busy);
        passwordVisibleField.setDisable(busy);
        loginProgress.setVisible(busy);
        loginProgress.setManaged(busy);
        loginButton.setText(busy ? "Connexion en cours…" : "Ouvrir la session");
    }

    @FXML private void minimizeWindow() { App.getPrimaryStage().setIconified(true); }
    @FXML private void closeWindow() { App.closeApplication(); }
}

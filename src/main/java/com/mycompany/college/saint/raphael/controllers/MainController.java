package com.mycompany.college.saint.raphael.controllers;

import com.mycompany.college.saint.raphael.App;
import com.mycompany.college.saint.raphael.config.DatabaseConfig;
import com.mycompany.college.saint.raphael.models.Utilisateur;
import com.mycompany.college.saint.raphael.services.AuthService;
import com.mycompany.college.saint.raphael.utils.AlertUtils;
import com.mycompany.college.saint.raphael.utils.SessionManager;
import com.mycompany.college.saint.raphael.utils.WindowUtils;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class MainController {
    @FXML private HBox titleBar;
    @FXML private VBox navigationPane;
    @FXML private StackPane contentPane;
    @FXML private Label pageTitleLabel, userNameLabel, roleLabel, dateTimeLabel, initialsLabel;
    @FXML private Label statusLabel, databaseStatusLabel, windowStateLabel, resizeGrip;
    @FXML private Button dashboardButton, studentsButton, classesButton, enrollmentsButton;
    @FXML private Button responsiblesButton, teachersButton, coursesButton, gradesButton;
    @FXML private Button bulletinsButton, academicButton, usersButton, historyButton;
    @FXML private MenuItem studentsMenuItem, classesMenuItem, enrollmentsMenuItem, responsiblesMenuItem;
    @FXML private MenuItem teachersMenuItem, coursesMenuItem, gradesMenuItem, bulletinsMenuItem;
    @FXML private MenuItem academicMenuItem, usersMenuItem, historyMenuItem;

    private Button activeButton;
    private String currentResource;
    private String currentTitle;
    private Timeline clock;
    private Timeline databaseMonitor;

    @FXML
    public void initialize() {
        Utilisateur user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            App.showLogin();
            return;
        }

        userNameLabel.setText(user.getNomComplet());
        roleLabel.setText(user.getRoleNom());
        String name = user.getNomComplet() == null ? "" : user.getNomComplet().trim();
        initialsLabel.setText(name.isBlank() ? "U" : Arrays.stream(name.split("\\s+"))
                .limit(2).map(x -> x.substring(0, 1)).reduce("", String::concat).toUpperCase());

        applyPermissions();
        Platform.runLater(() -> {
            WindowUtils.installDrag(titleBar, App.getPrimaryStage());
            WindowUtils.installResize(resizeGrip, App.getPrimaryStage(), 980, 620);
        });
        startClock();
        startDatabaseMonitor();
        showDashboard();
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy  •  HH:mm", Locale.FRENCH);
        clock = new Timeline(new KeyFrame(Duration.ZERO,
                e -> dateTimeLabel.setText(LocalDateTime.now().format(formatter))),
                new KeyFrame(Duration.seconds(30)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void startDatabaseMonitor() {
        databaseMonitor = new Timeline(new KeyFrame(Duration.ZERO, e -> checkDatabaseAsync()),
                new KeyFrame(Duration.seconds(45)));
        databaseMonitor.setCycleCount(Timeline.INDEFINITE);
        databaseMonitor.play();
    }

    private void checkDatabaseAsync() {
        Thread worker = new Thread(() -> {
            boolean connected = DatabaseConfig.getInstance().testConnection();
            Platform.runLater(() -> {
                databaseStatusLabel.setText(connected ? "● Base connectée" : "● Base déconnectée");
                databaseStatusLabel.getStyleClass().removeAll("db-online", "db-offline");
                databaseStatusLabel.getStyleClass().add(connected ? "db-online" : "db-offline");
            });
        }, "database-monitor");
        worker.setDaemon(true);
        worker.start();
    }

    private void applyPermissions() {
        permit(studentsButton, studentsMenuItem, "ELEVE_MANAGE");
        permit(classesButton, classesMenuItem, "CLASSE_MANAGE");
        permit(enrollmentsButton, enrollmentsMenuItem, "INSCRIPTION_MANAGE");
        permit(responsiblesButton, responsiblesMenuItem, "RESPONSABLE_MANAGE");
        permit(teachersButton, teachersMenuItem, "RESULTAT_MANAGE");
        permit(coursesButton, coursesMenuItem, "RESULTAT_MANAGE");
        permit(gradesButton, gradesMenuItem, "RESULTAT_MANAGE");
        permit(bulletinsButton, bulletinsMenuItem, "RESULTAT_MANAGE");
        permit(academicButton, academicMenuItem, "CLASSE_MANAGE");
        permit(usersButton, usersMenuItem, "USER_MANAGE");
        permit(historyButton, historyMenuItem, "HISTORIQUE_VIEW");
    }

    private void permit(Button button, MenuItem menuItem, String code) {
        boolean allowed = SessionManager.getInstance().hasPermission(code);
        if (button != null) {
            button.setVisible(allowed);
            button.setManaged(allowed);
        }
        if (menuItem != null) menuItem.setVisible(allowed);
    }

    private void load(String resource, String title, Button button) {
        try {
            statusLabel.setText("Ouverture de « " + title + " »…");
            Node page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resource)));
            page.setOpacity(0);
            contentPane.getChildren().setAll(page);
            pageTitleLabel.setText(title);
            currentResource = resource;
            currentTitle = title;

            if (activeButton != null) activeButton.getStyleClass().remove("nav-active");
            activeButton = button;
            if (button != null && !button.getStyleClass().contains("nav-active")) {
                button.getStyleClass().add("nav-active");
            }

            FadeTransition fade = new FadeTransition(Duration.millis(180), page);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            statusLabel.setText("Prêt");
        } catch (IOException | NullPointerException e) {
            statusLabel.setText("Erreur d’ouverture");
            AlertUtils.showError("Navigation", "Impossible d’ouvrir " + title + ".\n" + e.getMessage());
        }
    }

    @FXML private void refreshCurrentPage() {
        if (currentResource != null) load(currentResource, currentTitle, activeButton);
    }

    @FXML private void toggleNavigation() {
        boolean visible = navigationPane.isVisible();
        navigationPane.setVisible(!visible);
        navigationPane.setManaged(!visible);
        windowStateLabel.setText(visible ? "Navigation masquée" : "Navigation affichée");
    }

    @FXML private void showDashboard() { load("/fxml/DashboardView.fxml", "Tableau de bord", dashboardButton); }
    @FXML private void showStudents() { load("/fxml/ElevesView.fxml", "Gestion des élèves", studentsButton); }
    @FXML private void showClasses() { load("/fxml/ClassesView.fxml", "Organisation des classes", classesButton); }
    @FXML private void showEnrollments() { load("/fxml/InscriptionsView.fxml", "Gestion des inscriptions", enrollmentsButton); }
    @FXML private void showResponsibles() { load("/fxml/ResponsablesView.fxml", "Parents et responsables", responsiblesButton); }
    @FXML private void showTeachers() { load("/fxml/EnseignantsView.fxml", "Gestion des enseignants", teachersButton); }
    @FXML private void showCourses() { load("/fxml/CoursView.fxml", "Cours et affectations", coursesButton); }
    @FXML private void showGrades() { load("/fxml/NotesView.fxml", "Saisie des notes", gradesButton); }
    @FXML private void showBulletins() { load("/fxml/BulletinsView.fxml", "Résultats et bulletins", bulletinsButton); }
    @FXML private void showAcademic() { load("/fxml/AcademicView.fxml", "Années scolaires et périodes", academicButton); }
    @FXML private void showUsers() { load("/fxml/UtilisateursView.fxml", "Utilisateurs et accès", usersButton); }
    @FXML private void showHistory() { load("/fxml/HistoriqueView.fxml", "Historique des opérations", historyButton); }

    @FXML private void minimizeWindow() { App.getPrimaryStage().setIconified(true); }

    @FXML private void maximizeWindow() {
        WindowUtils.toggleMaximize(App.getPrimaryStage());
        windowStateLabel.setText(App.getPrimaryStage().isMaximized() ? "Fenêtre agrandie" : "Fenêtre restaurée");
    }

    @FXML private void closeWindow() {
        if (AlertUtils.confirm("Quitter l’application", "Voulez-vous fermer complètement l’application ?")) {
            AuthService.getInstance().logout();
            App.closeApplication();
        }
    }

    @FXML private void logout() {
        if (AlertUtils.confirm("Déconnexion", "Voulez-vous fermer votre session ?")) {
            if (clock != null) clock.stop();
            if (databaseMonitor != null) databaseMonitor.stop();
            AuthService.getInstance().logout();
            App.showLogin();
        }
    }

    @FXML private void showAbout() {
        AlertUtils.showSuccess("À propos",
                "Collège Sainte Famille\nDiscipline • Amour • Travail\nSystème professionnel de gestion scolaire\nVersion Desktop 3.2");
    }
}

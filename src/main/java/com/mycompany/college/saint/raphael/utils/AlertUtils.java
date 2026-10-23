package com.mycompany.college.saint.raphael.utils;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
public final class AlertUtils {
    private AlertUtils() {
    }
    public static void showInfo(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }
    public static void showSuccess(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }
    public static void showWarning(String title, String message) {
        show(Alert.AlertType.WARNING, title, message);
    }
    public static void showError(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }
    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        BrandingUtils.applyDialogIcon(alert);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
    public static boolean confirmDelete(String item) {
        return confirm("Confirmation", "Supprimer « " + item + " » ?");
    }
    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        BrandingUtils.applyDialogIcon(alert);
        alert.showAndWait();
    }
}

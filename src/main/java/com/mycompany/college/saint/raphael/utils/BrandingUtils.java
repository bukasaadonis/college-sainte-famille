package com.mycompany.college.saint.raphael.utils;

import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Objects;

/** Centralise le logo et l'identité visuelle de l'application. */
public final class BrandingUtils {
    private static final String LOGO_PATH = "/images/logo_256.png";
    private static final Image LOGO = new Image(Objects.requireNonNull(
            BrandingUtils.class.getResourceAsStream(LOGO_PATH),
            "Logo introuvable : " + LOGO_PATH));

    private BrandingUtils() {
    }

    public static Image getLogo() {
        return LOGO;
    }

    public static void applyStageIcon(Stage stage) {
        if (stage != null) {
            stage.getIcons().setAll(LOGO);
        }
    }

    public static void applyDialogIcon(Dialog<?> dialog) {
        if (dialog == null) return;
        dialog.setOnShown(event -> {
            Window window = dialog.getDialogPane().getScene().getWindow();
            if (window instanceof Stage stage) {
                applyStageIcon(stage);
            }
        });
    }
}

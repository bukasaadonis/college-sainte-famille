package com.mycompany.college.saint.raphael.utils;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.stage.Screen;
import javafx.stage.Stage;

public final class WindowUtils {
    private WindowUtils() {}

    public static void installDrag(Node handle, Stage stage) {
        final double[] offset = new double[2];
        handle.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                offset[0] = event.getSceneX();
                offset[1] = event.getSceneY();
            }
        });
        handle.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) return;
            if (stage.isMaximized()) stage.setMaximized(false);
            stage.setX(event.getScreenX() - offset[0]);
            stage.setY(event.getScreenY() - offset[1]);
        });
        handle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) toggleMaximize(stage);
        });
    }

    public static void installResize(Node handle, Stage stage, double minWidth, double minHeight) {
        final double[] start = new double[4];
        handle.setOnMousePressed(event -> {
            start[0] = event.getScreenX();
            start[1] = event.getScreenY();
            start[2] = stage.getWidth();
            start[3] = stage.getHeight();
        });
        handle.setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown() || stage.isMaximized()) return;
            double width = Math.max(minWidth, start[2] + event.getScreenX() - start[0]);
            double height = Math.max(minHeight, start[3] + event.getScreenY() - start[1]);
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            width = Math.min(width, screen.getWidth() - Math.max(0, stage.getX() - screen.getMinX()));
            height = Math.min(height, screen.getHeight() - Math.max(0, stage.getY() - screen.getMinY()));
            stage.setWidth(width);
            stage.setHeight(height);
        });
    }

    public static void toggleMaximize(Stage stage) {
        stage.setMaximized(!stage.isMaximized());
    }

    public static void keepInsideVisibleScreen(Stage stage) {
        Rectangle2D b = Screen.getPrimary().getVisualBounds();
        if (stage.getWidth() > b.getWidth()) stage.setWidth(b.getWidth());
        if (stage.getHeight() > b.getHeight()) stage.setHeight(b.getHeight());
        if (stage.getX() < b.getMinX()) stage.setX(b.getMinX());
        if (stage.getY() < b.getMinY()) stage.setY(b.getMinY());
    }
}

package com.mycompany.college.saint.raphael.utils;

import javafx.animation.PauseTransition;
import javafx.scene.control.TextInputControl;
import javafx.util.Duration;

public final class SearchDebouncer {
    private SearchDebouncer() {}

    public static void bind(TextInputControl field, Runnable action) {
        PauseTransition delay = new PauseTransition(Duration.millis(320));
        delay.setOnFinished(event -> action.run());
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            delay.stop();
            delay.playFromStart();
        });
    }
}

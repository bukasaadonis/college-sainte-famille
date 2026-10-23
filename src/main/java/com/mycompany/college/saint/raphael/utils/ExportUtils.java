package com.mycompany.college.saint.raphael.utils;

import com.mycompany.college.saint.raphael.App;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class ExportUtils {
    private ExportUtils() {}

    public static void exportTable(TableView<?> table, String suggestedName) {
        if (table == null || table.getItems() == null || table.getItems().isEmpty()) {
            AlertUtils.showWarning("Export", "Aucune donnée n’est disponible pour l’exportation.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exporter les données");
        chooser.setInitialFileName(sanitize(suggestedName) + ".csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV UTF-8", "*.csv"));
        File file = chooser.showSaveDialog(App.getPrimaryStage());
        if (file == null) return;
        if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getParentFile(), file.getName() + ".csv");

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write('\ufeff');
            for (int i = 0; i < table.getColumns().size(); i++) {
                if (i > 0) writer.write(';');
                writer.write(escape(table.getColumns().get(i).getText()));
            }
            writer.newLine();

            for (Object item : table.getItems()) {
                for (int i = 0; i < table.getColumns().size(); i++) {
                    if (i > 0) writer.write(';');
                    @SuppressWarnings("unchecked")
                    TableColumn<Object, Object> column = (TableColumn<Object, Object>) table.getColumns().get(i);
                    var observable = column.getCellObservableValue(item);
                    Object value = observable == null ? null : observable.getValue();
                    writer.write(escape(value == null ? "" : value.toString()));
                }
                writer.newLine();
            }
            AlertUtils.showSuccess("Export terminé", "Le fichier a été enregistré :\n" + file.getAbsolutePath());
        } catch (IOException e) {
            AlertUtils.showError("Export impossible", e.getMessage());
        }
    }

    private static String sanitize(String name) {
        String safe = name == null || name.isBlank() ? "export" : name.trim();
        return safe.replaceAll("[\\\\/:*?\"<>|]", "-");
    }

    private static String escape(String value) {
        String v = value.replace("\"", "\"\"");
        return v.contains(";") || v.contains("\n") || v.contains("\r") ? '"' + v + '"' : v;
    }
}

package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.HistoriqueDAO;
import com.mycompany.college.saint.raphael.models.Historique;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class HistoriqueController {
    @FXML TextField searchField;
    @FXML DatePicker fromPicker,toPicker;
    @FXML TableView<Historique>table;
    @FXML TableColumn<Historique,java.time.LocalDateTime>dateCol;
    @FXML TableColumn<Historique,String>userCol,actionCol,entityCol,detailsCol,ipCol;
    @FXML Label countLabel;
    final HistoriqueDAO dao=new HistoriqueDAO();
    @FXML public void initialize() {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateAction"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("utilisateurNom"));
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        entityCol.setCellValueFactory(new PropertyValueFactory<>("entite"));
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        ipCol.setCellValueFactory(new PropertyValueFactory<>("adresseIp"));
        SearchDebouncer.bind(searchField, this::load);
        load();
    }
    @FXML void load() {
        try {
            var l=dao.find(searchField.getText(),fromPicker.getValue(),toPicker.getValue());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" opération(s)");
        }
        catch(Exception e) {
            AlertUtils.showError("Historique",e.getMessage());
        }
    }
    @FXML void clearFilters() {
        searchField.clear();
        fromPicker.setValue(null);
        toPicker.setValue(null);
        load();
    }
    @FXML void export() {
        ExportUtils.exportTable(table,"historique");
    }
}

package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class BulletinsController {
    @FXML ComboBox<AnneeScolaire>yearCombo;
    @FXML ComboBox<Classe>classCombo;
    @FXML ComboBox<Periode>periodCombo;
    @FXML TableView<Bulletin>table;
    @FXML TableColumn<Bulletin,String>matriculeCol,studentCol,averageCol,decisionCol;
    @FXML TableColumn<Bulletin,Integer>rankCol;
    @FXML Label infoLabel;
    final BulletinDAO dao=new BulletinDAO();
    @FXML public void initialize() {
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        studentCol.setCellValueFactory(new PropertyValueFactory<>("eleveNom"));
        averageCol.setCellValueFactory(new PropertyValueFactory<>("moyenneAffichee"));
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rang"));
        decisionCol.setCellValueFactory(new PropertyValueFactory<>("decision"));
        try {
            var ys=new AnneeScolaireDAO().findAll();
            yearCombo.setItems(FXCollections.observableArrayList(ys));
            ys.stream().filter(AnneeScolaire::isActive).findFirst().ifPresent(yearCombo::setValue);
            classCombo.setItems(FXCollections.observableArrayList(new ClasseDAO().findActive()));
        }
        catch(Exception e) {
            infoLabel.setText("Chargement impossible : "+e.getMessage());
        }
        yearCombo.valueProperty().addListener((o,a,b)-> { periodCombo.setValue(null); loadPeriods(); });
        loadPeriods();
    }
    void loadPeriods() {
        try {
            if(yearCombo.getValue()!=null)periodCombo.setItems(FXCollections.observableArrayList(new PeriodeDAO().findByYear(yearCombo.getValue().getId())));
        }
        catch(Exception e) {
            AlertUtils.showError("Périodes",e.getMessage());
        }
    }
    @FXML void calculate() {
        try {
            if(yearCombo.getValue()==null||classCombo.getValue()==null||periodCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez l'année, la classe et la période.");
            var l=dao.calculate(classCombo.getValue().getId(),yearCombo.getValue().getId(),periodCombo.getValue().getId());
            table.setItems(FXCollections.observableArrayList(l));
            infoLabel.setText(l.size()+" bulletin(s) calculé(s)");
        }
        catch(Exception e) {
            AlertUtils.showError("Calcul",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            if(periodCombo.getValue()==null||table.getItems().isEmpty())throw new IllegalArgumentException("Calculez d'abord les résultats.");
            dao.saveAll(table.getItems(),periodCombo.getValue().getId());
            AuditService.getInstance().log("GENERATION","BULLETIN",periodCombo.getValue().getId(),classCombo.getValue().getNom());
            AlertUtils.showSuccess("Bulletins","Les résultats ont été enregistrés.");
        }
        catch(Exception e) {
            AlertUtils.showError("Bulletins",e.getMessage());
        }
    }
    @FXML void export() {
        ExportUtils.exportTable(table,"bulletins_"+(classCombo.getValue()==null?"classe":classCombo.getValue().getNom()));
    }
}

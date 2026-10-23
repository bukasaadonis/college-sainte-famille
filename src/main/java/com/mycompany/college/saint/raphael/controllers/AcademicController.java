package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class AcademicController {
    @FXML TableView<AnneeScolaire>yearTable;
    @FXML TableColumn<AnneeScolaire,String>yearLabelCol,yearStateCol;
    @FXML TableColumn<AnneeScolaire,java.time.LocalDate>yearStartCol,yearEndCol;
    @FXML TextField yearLabelField;
    @FXML DatePicker yearStartPicker,yearEndPicker;
    @FXML TableView<Periode>periodTable;
    @FXML TableColumn<Periode,String>periodNameCol,periodTypeCol,periodYearCol;
    @FXML TableColumn<Periode,java.time.LocalDate>periodStartCol,periodEndCol;
    @FXML TextField periodNameField;
    @FXML ComboBox<String>periodTypeCombo;
    @FXML ComboBox<AnneeScolaire>periodYearCombo;
    @FXML DatePicker periodStartPicker,periodEndPicker;
    final AnneeScolaireDAO ydao=new AnneeScolaireDAO();
    final PeriodeDAO pdao=new PeriodeDAO();
    AnneeScolaire sy;
    Periode sp;
    @FXML public void initialize() {
        yearLabelCol.setCellValueFactory(new PropertyValueFactory<>("libelle"));
        yearStartCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        yearEndCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        yearStateCol.setCellValueFactory(new PropertyValueFactory<>("etat"));
        periodNameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        periodTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        periodYearCol.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
        periodStartCol.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        periodEndCol.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        periodTypeCombo.setItems(FXCollections.observableArrayList("TRIMESTRE","SEMESTRE","SESSION"));
        yearTable.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fillYear(b));
        periodTable.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fillPeriod(b));
        load();
    }
    @FXML void load() {
        try {
            var ys=ydao.findAll();
            yearTable.setItems(FXCollections.observableArrayList(ys));
            periodYearCombo.setItems(FXCollections.observableArrayList(ys));
            periodTable.setItems(FXCollections.observableArrayList(pdao.findAll()));
        }
        catch(Exception e) {
            AlertUtils.showError("Paramètres académiques",e.getMessage());
        }
    }
    @FXML void saveYear() {
        try {
            AnneeScolaire o=sy==null?new AnneeScolaire():sy;
            o.setLibelle(ValidationUtils.required(yearLabelField.getText(),"Libellé"));
            o.setDateDebut(yearStartPicker.getValue());
            o.setDateFin(yearEndPicker.getValue());
            if(o.getDateDebut()==null||o.getDateFin()==null||!o.getDateFin().isAfter(o.getDateDebut()))throw new IllegalArgumentException("Les dates de l'année scolaire sont invalides.");
            long id;
            if(sy==null)id=ydao.insert(o);
            else {
                ydao.update(o);
                id=o.getId();
            }
            AuditService.getInstance().log(sy==null?"CREATION":"MODIFICATION","ANNEE_SCOLAIRE",id,o.getLibelle());
            clearYear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Année scolaire",e.getMessage());
        }
    }
    @FXML void activateYear() {
        var o=yearTable.getSelectionModel().getSelectedItem();
        if(o!=null&&AlertUtils.confirm("Année active","Définir "+o.getLibelle()+" comme année active ?"))try {
            ydao.activate(o.getId());
            AuditService.getInstance().log("ACTIVATION","ANNEE_SCOLAIRE",o.getId(),o.getLibelle());
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Activation",e.getMessage());
        }
    }
    @FXML void savePeriod() {
        try {
            Periode o=sp==null?new Periode():sp;
            if(periodYearCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez une année scolaire.");
            o.setAnneeScolaireId(periodYearCombo.getValue().getId());
            o.setNom(ValidationUtils.required(periodNameField.getText(),"Nom de la période"));
            if(periodTypeCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le type de période.");
            o.setType(periodTypeCombo.getValue());
            o.setDateDebut(periodStartPicker.getValue());
            o.setDateFin(periodEndPicker.getValue());
            if(o.getDateDebut()==null||o.getDateFin()==null||!o.getDateFin().isAfter(o.getDateDebut()))throw new IllegalArgumentException("Dates invalides.");
            long id;
            if(sp==null)id=pdao.insert(o);
            else {
                pdao.update(o);
                id=o.getId();
            }
            AuditService.getInstance().log(sp==null?"CREATION":"MODIFICATION","PERIODE",id,o.getNom());
            clearPeriod();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Période",e.getMessage());
        }
    }
    @FXML void deletePeriod() {
        var o=periodTable.getSelectionModel().getSelectedItem();
        if(o!=null&&AlertUtils.confirmDelete(o.getNom()))try {
            pdao.delete(o.getId());
            clearPeriod();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Suppression",e.getMessage());
        }
    }
    @FXML void clearYear() {
        sy=null;
        yearLabelField.clear();
        yearStartPicker.setValue(null);
        yearEndPicker.setValue(null);
        yearTable.getSelectionModel().clearSelection();
    }
    @FXML void clearPeriod() {
        sp=null;
        periodNameField.clear();
        periodTypeCombo.setValue(null);
        periodYearCombo.setValue(null);
        periodStartPicker.setValue(null);
        periodEndPicker.setValue(null);
        periodTable.getSelectionModel().clearSelection();
    }
    void fillYear(AnneeScolaire o) {
        if(o==null)return;
        sy=o;
        yearLabelField.setText(o.getLibelle());
        yearStartPicker.setValue(o.getDateDebut());
        yearEndPicker.setValue(o.getDateFin());
    }
    void fillPeriod(Periode o) {
        if(o==null)return;
        sp=o;
        periodNameField.setText(o.getNom());
        periodTypeCombo.setValue(o.getType());
        periodStartPicker.setValue(o.getDateDebut());
        periodEndPicker.setValue(o.getDateFin());
        periodYearCombo.getItems().stream().filter(x->x.getId().equals(o.getAnneeScolaireId())).findFirst().ifPresent(periodYearCombo::setValue);
    }
}

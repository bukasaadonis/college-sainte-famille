package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
public class NotesController {
    @FXML ComboBox<AnneeScolaire>yearCombo;
    @FXML ComboBox<Classe>classCombo;
    @FXML ComboBox<Cours>courseCombo;
    @FXML ComboBox<Periode>periodCombo;
    @FXML TableView<NoteRow>table;
    @FXML TableColumn<NoteRow,String>matriculeCol,studentCol,percentCol;
    @FXML TableColumn<NoteRow,Double>valueCol,maxCol,coefCol;
    @FXML Label infoLabel;
    final NoteDAO dao=new NoteDAO();
    final CoursDAO cdao=new CoursDAO();
    final PeriodeDAO pdao=new PeriodeDAO();
    @FXML public void initialize() {
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        studentCol.setCellValueFactory(new PropertyValueFactory<>("eleveNom"));
        valueCol.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.DoubleStringConverter()));
        valueCol.setOnEditCommit(e-> {
            Double v=e.getNewValue();if(v!=null&&(v<0||v>e.getRowValue().getMaximum())) {
                AlertUtils.showWarning("Note invalide","La note doit être comprise entre 0 et "+e.getRowValue().getMaximum());
                e.getRowValue().setValeur(e.getOldValue());
                table.refresh();
            }
            else e.getRowValue().setValeur(v);
        }
        );
        maxCol.setCellValueFactory(new PropertyValueFactory<>("maximum"));
        coefCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        percentCol.setCellValueFactory(new PropertyValueFactory<>("pourcentage"));
        table.setEditable(true);
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
        classCombo.valueProperty().addListener((o,a,b)-> { courseCombo.setValue(null); loadCourses(); });
        loadPeriods();
    }
    void loadPeriods() {
        try {
            if(yearCombo.getValue()!=null)periodCombo.setItems(FXCollections.observableArrayList(pdao.findByYear(yearCombo.getValue().getId())));
        }
        catch(Exception e) {
            AlertUtils.showError("Périodes",e.getMessage());
        }
    }
    void loadCourses() {
        try {
            if(classCombo.getValue()!=null)courseCombo.setItems(FXCollections.observableArrayList(cdao.findByClass(classCombo.getValue().getId())));
        }
        catch(Exception e) {
            AlertUtils.showError("Cours",e.getMessage());
        }
    }
    @FXML void load() {
        try {
            if(yearCombo.getValue()==null||classCombo.getValue()==null||courseCombo.getValue()==null||periodCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez l'année, la classe, le cours et la période.");
            var rows=dao.loadRows(classCombo.getValue().getId(),yearCombo.getValue().getId(),courseCombo.getValue().getId(),periodCombo.getValue().getId());
            table.setItems(FXCollections.observableArrayList(rows));
            infoLabel.setText(rows.size()+" élève(s) — double-cliquez sur la colonne Note");
        }
        catch(Exception e) {
            AlertUtils.showError("Chargement",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            if(courseCombo.getValue()==null||periodCombo.getValue()==null)throw new IllegalArgumentException("Chargez d'abord une feuille de notes.");
            dao.saveAll(courseCombo.getValue().getId(),periodCombo.getValue().getId(),table.getItems());
            AuditService.getInstance().log("SAISIE_NOTES","NOTE",courseCombo.getValue().getId(),courseCombo.getValue().getCode()+" / "+periodCombo.getValue().getNom());
            AlertUtils.showSuccess("Notes enregistrées","La feuille de notes a été sauvegardée.");
            table.refresh();
        }
        catch(Exception e) {
            AlertUtils.showError("Enregistrement",e.getMessage());
        }
    }
}

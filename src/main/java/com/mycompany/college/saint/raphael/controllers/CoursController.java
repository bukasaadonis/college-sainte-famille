package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
public class CoursController {
    @FXML TextField searchField,nameField,codeField,coefField;
    @FXML TextArea descriptionArea;
    @FXML ComboBox<Classe>classCombo;
    @FXML ComboBox<Enseignant>teacherCombo;
    @FXML TableView<Cours>table;
    @FXML TableColumn<Cours,String>codeCol,nameCol,classCol,teacherCol;
    @FXML TableColumn<Cours,Double>coefCol;
    @FXML Label formTitle,countLabel;
    final CoursDAO dao=new CoursDAO();
    Cours selected;
    @FXML public void initialize() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        classCol.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        teacherCol.setCellValueFactory(new PropertyValueFactory<>("enseignantNom"));
        coefCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fill(b));
        SearchDebouncer.bind(searchField, this::load);
        try {
            classCombo.setItems(FXCollections.observableArrayList(new ClasseDAO().findActive()));
            teacherCombo.setItems(FXCollections.observableArrayList(new EnseignantDAO().findAll("")));
        }
        catch(Exception e) {
            AlertUtils.showError("Chargement des listes", e.getMessage());
        }
        load();
    }
    @FXML void load() {
        try {
            var l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" cours");
        }
        catch(Exception e) {
            AlertUtils.showError("Cours",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            Cours o=selected==null?new Cours():selected;
            if(classCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez une classe.");
            o.setClasseId(classCombo.getValue().getId());
            o.setEnseignantId(teacherCombo.getValue()==null?null:teacherCombo.getValue().getId());
            o.setNom(ValidationUtils.required(nameField.getText(),"Nom du cours"));
            o.setCode(ValidationUtils.required(codeField.getText(),"Code").toUpperCase());
            double coefficient=Double.parseDouble(ValidationUtils.required(coefField.getText(),"Coefficient").replace(',', '.'));
            if(coefficient<=0 || coefficient>20)throw new IllegalArgumentException("Le coefficient doit être compris entre 0 et 20.");
            o.setCoefficient(coefficient);
            o.setDescription(ValidationUtils.optional(descriptionArea.getText()));
            long id;
            if(selected==null)id=dao.insert(o);
            else {
                dao.update(o);
                id=o.getId();
            }
            AuditService.getInstance().log(selected==null?"CREATION":"MODIFICATION","COURS",id,o.getCode());
            clear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Enregistrement",e.getMessage());
        }
    }
    @FXML void delete() {
        var o=table.getSelectionModel().getSelectedItem();
        if(o!=null&&AlertUtils.confirmDelete(o.getNom()))try {
            dao.delete(o.getId());
            clear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Suppression",e.getMessage());
        }
    }
    @FXML void clear() {
        selected=null;
        formTitle.setText("Nouveau cours");
        nameField.clear();
        codeField.clear();
        coefField.setText("1.00");
        descriptionArea.clear();
        classCombo.setValue(null);
        teacherCombo.setValue(null);
        table.getSelectionModel().clearSelection();
    }
    @FXML void export() {
        ExportUtils.exportTable(table,"cours");
    }
    void fill(Cours o) {
        if(o==null)return;
        selected=o;
        formTitle.setText("Modifier le cours");
        nameField.setText(o.getNom());
        codeField.setText(o.getCode());
        coefField.setText(String.valueOf(o.getCoefficient()));
        descriptionArea.setText(o.getDescription());
        classCombo.getItems().stream().filter(x->x.getId().equals(o.getClasseId())).findFirst().ifPresent(classCombo::setValue);
        if(o.getEnseignantId()!=null)teacherCombo.getItems().stream().filter(x->x.getId().equals(o.getEnseignantId())).findFirst().ifPresent(teacherCombo::setValue);
    }
}

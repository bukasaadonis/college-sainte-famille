package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
public class EnseignantsController {
    @FXML TextField searchField,nomField,postnomField,prenomField,specialiteField,emailField,telephoneField;
    @FXML TableView<Enseignant>table;
    @FXML TableColumn<Enseignant,String>nomCol,specialiteCol,emailCol,telephoneCol;
    @FXML TableColumn<Enseignant,Integer>coursCol;
    @FXML Label formTitle,countLabel;
    final EnseignantDAO dao=new EnseignantDAO();
    Enseignant selected;
    @FXML public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        specialiteCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        coursCol.setCellValueFactory(new PropertyValueFactory<>("nombreCours"));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fill(b));
        SearchDebouncer.bind(searchField, this::load);
        load();
    }
    @FXML void load() {
        try {
            var l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" enseignant(s)");
        }
        catch(Exception e) {
            AlertUtils.showError("Enseignants",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            Enseignant o=selected==null?new Enseignant():selected;
            o.setNom(ValidationUtils.required(nomField.getText(),"Nom").toUpperCase());
            o.setPostnom(ValidationUtils.optional(postnomField.getText()));
            o.setPrenom(ValidationUtils.required(prenomField.getText(),"Prénom"));
            o.setSpecialite(ValidationUtils.optional(specialiteField.getText()));
            o.setEmail(ValidationUtils.email(emailField.getText()));
            o.setTelephone(ValidationUtils.optional(telephoneField.getText()));
            long id;
            if(selected==null)id=dao.insert(o);
            else {
                dao.update(o);
                id=o.getId();
            }
            AuditService.getInstance().log(selected==null?"CREATION":"MODIFICATION","ENSEIGNANT",id,o.getNomComplet());
            clear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Enregistrement",e.getMessage());
        }
    }
    @FXML void delete() {
        var o=table.getSelectionModel().getSelectedItem();
        if(o!=null&&AlertUtils.confirmDelete(o.getNomComplet()))try {
            dao.delete(o.getId());
            clear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Suppression",e.getMessage());
        }
    }
    @FXML void export() {
        ExportUtils.exportTable(table,"enseignants");
    }
    @FXML void clear() {
        selected=null;
        formTitle.setText("Nouvel enseignant");
        for(var f:List.of(nomField,postnomField,prenomField,specialiteField,emailField,telephoneField))f.clear();
        table.getSelectionModel().clearSelection();
    }
    void fill(Enseignant o) {
        if(o==null)return;
        selected=o;
        formTitle.setText("Modifier l'enseignant");
        nomField.setText(o.getNom());
        postnomField.setText(o.getPostnom());
        prenomField.setText(o.getPrenom());
        specialiteField.setText(o.getSpecialite());
        emailField.setText(o.getEmail());
        telephoneField.setText(o.getTelephone());
    }
}

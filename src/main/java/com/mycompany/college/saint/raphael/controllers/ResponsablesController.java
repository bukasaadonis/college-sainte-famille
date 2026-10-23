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
public class ResponsablesController {
    @FXML TextField searchField,nomField,postnomField,prenomField,professionField,telephoneField,emailField,adresseField;
    @FXML ComboBox<String>lienCombo;
    @FXML ComboBox<Eleve>eleveCombo;
    @FXML TableView<Responsable>table;
    @FXML TableColumn<Responsable,String>nomCol,lienCol,telephoneCol,professionCol;
    @FXML TableColumn<Responsable,Integer>elevesCol;
    @FXML Label formTitle,countLabel;
    final ResponsableDAO dao=new ResponsableDAO();
    final EleveDAO edao=new EleveDAO();
    Responsable selected;
    @FXML public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        lienCol.setCellValueFactory(new PropertyValueFactory<>("lienAvecEleve"));
        telephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        professionCol.setCellValueFactory(new PropertyValueFactory<>("profession"));
        elevesCol.setCellValueFactory(new PropertyValueFactory<>("nombreEleves"));
        lienCombo.setItems(FXCollections.observableArrayList("PERE","MERE","TUTEUR","TUTRICE","AUTRE"));
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fill(b));
        SearchDebouncer.bind(searchField, this::load);
        try {
            eleveCombo.setItems(FXCollections.observableArrayList(edao.findAll("")));
        }
        catch(Exception e) {
            AlertUtils.showError("Chargement des élèves", e.getMessage());
        }
        load();
    }
    @FXML void load() {
        try {
            var l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" responsable(s)");
        }
        catch(Exception e) {
            AlertUtils.showError("Responsables",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            Responsable o=selected==null?new Responsable():selected;
            o.setNom(ValidationUtils.required(nomField.getText(),"Nom").toUpperCase());
            o.setPostnom(ValidationUtils.optional(postnomField.getText()));
            o.setPrenom(ValidationUtils.required(prenomField.getText(),"Prénom"));
            if(lienCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le lien avec l’élève.");
            o.setLienAvecEleve(lienCombo.getValue());
            o.setProfession(ValidationUtils.optional(professionField.getText()));
            o.setTelephone(ValidationUtils.required(telephoneField.getText(),"Téléphone"));
            o.setEmail(ValidationUtils.email(emailField.getText()));
            o.setAdresse(ValidationUtils.optional(adresseField.getText()));
            long id;
            if(selected==null)id=dao.insert(o);
            else {
                dao.update(o);
                id=o.getId();
            }
            if(eleveCombo.getValue()!=null)dao.linkStudent(id,eleveCombo.getValue().getId());
            AuditService.getInstance().log(selected==null?"CREATION":"MODIFICATION","RESPONSABLE",id,o.getNomComplet());
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
        ExportUtils.exportTable(table,"responsables");
    }
    @FXML void clear() {
        selected=null;
        formTitle.setText("Nouveau responsable");
        for(var f:List.of(nomField,postnomField,prenomField,professionField,telephoneField,emailField,adresseField))f.clear();
        lienCombo.setValue(null);
        eleveCombo.setValue(null);
        table.getSelectionModel().clearSelection();
    }
    void fill(Responsable o) {
        if(o==null)return;
        selected=o;
        formTitle.setText("Modifier le responsable");
        nomField.setText(o.getNom());
        postnomField.setText(o.getPostnom());
        prenomField.setText(o.getPrenom());
        lienCombo.setValue(o.getLienAvecEleve());
        professionField.setText(o.getProfession());
        telephoneField.setText(o.getTelephone());
        emailField.setText(o.getEmail());
        adresseField.setText(o.getAdresse());
    }
}

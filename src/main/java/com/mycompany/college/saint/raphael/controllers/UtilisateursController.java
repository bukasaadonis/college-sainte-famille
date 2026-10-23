package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.security.PasswordHasher;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
public class UtilisateursController {
    @FXML TextField searchField,loginField,nomField,postnomField,prenomField,emailField,telephoneField;
    @FXML PasswordField passwordField;
    @FXML ComboBox<Role>roleCombo;
    @FXML ComboBox<String>statusCombo;
    @FXML CheckBox blockedCheck;
    @FXML TableView<Utilisateur>table;
    @FXML TableColumn<Utilisateur,String>loginCol,nameCol,roleCol,emailCol,statusCol;
    @FXML TableColumn<Utilisateur,Boolean>blockedCol;
    @FXML Label formTitle,countLabel;
    final UtilisateurDAO dao=new UtilisateurDAO();
    Utilisateur selected;
    @FXML public void initialize() {
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("roleNom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        blockedCol.setCellValueFactory(new PropertyValueFactory<>("bloque"));
        statusCombo.setItems(FXCollections.observableArrayList("ACTIF","INACTIF"));
        statusCombo.setValue("ACTIF");
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->fill(b));
        SearchDebouncer.bind(searchField, this::load);
        try {
            roleCombo.setItems(FXCollections.observableArrayList(dao.findRoles()));
        }
        catch(Exception e) {
            AlertUtils.showError("Chargement des rôles", e.getMessage());
        }
        load();
    }
    @FXML void load() {
        try {
            var l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" utilisateur(s)");
        }
        catch(Exception e) {
            AlertUtils.showError("Utilisateurs",e.getMessage());
        }
    }
    @FXML void save() {
        try {
            Utilisateur u=selected==null?new Utilisateur():selected;
            if(roleCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez un rôle.");
            u.setRoleId(roleCombo.getValue().getId());
            u.setLogin(ValidationUtils.required(loginField.getText(),"Identifiant"));
            u.setNom(ValidationUtils.required(nomField.getText(),"Nom").toUpperCase());
            u.setPostnom(ValidationUtils.optional(postnomField.getText()));
            u.setPrenom(ValidationUtils.required(prenomField.getText(),"Prénom"));
            u.setEmail(ValidationUtils.email(emailField.getText()));
            u.setTelephone(ValidationUtils.optional(telephoneField.getText()));
            u.setStatut(statusCombo.getValue());
            boolean change=!passwordField.getText().isBlank();
            long id;
            if(selected==null) {
                if(!change)throw new IllegalArgumentException("Le mot de passe est obligatoire.");
                u.setMotDePasse(PasswordHasher.hash(passwordField.getText()));
                id=dao.insert(u);
            }
            else {
                if(change)u.setMotDePasse(PasswordHasher.hash(passwordField.getText()));
                dao.update(u,change);
                dao.setBlocked(u.getId(),blockedCheck.isSelected());
                id=u.getId();
            }
            AuditService.getInstance().log(selected==null?"CREATION":"MODIFICATION","UTILISATEUR",id,u.getLogin());
            clear();
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Enregistrement",e.getMessage());
        }
    }
    @FXML void toggleBlock() {
        var u=table.getSelectionModel().getSelectedItem();
        if(u==null)return;
        try {
            dao.setBlocked(u.getId(),!u.isBloque());
            AuditService.getInstance().log(u.isBloque()?"DEBLOCAGE":"BLOCAGE","UTILISATEUR",u.getId(),u.getLogin());
            load();
        }
        catch(Exception e) {
            AlertUtils.showError("Compte",e.getMessage());
        }
    }
    @FXML void export() {
        ExportUtils.exportTable(table,"utilisateurs");
    }
    @FXML void clear() {
        selected=null;
        formTitle.setText("Nouvel utilisateur");
        for(var f:List.of(loginField,nomField,postnomField,prenomField,emailField,telephoneField))f.clear();
        passwordField.clear();
        roleCombo.setValue(null);
        statusCombo.setValue("ACTIF");
        blockedCheck.setSelected(false);
        table.getSelectionModel().clearSelection();
    }
    void fill(Utilisateur u) {
        if(u==null)return;
        selected=u;
        formTitle.setText("Modifier l'utilisateur");
        loginField.setText(u.getLogin());
        nomField.setText(u.getNom());
        postnomField.setText(u.getPostnom());
        prenomField.setText(u.getPrenom());
        emailField.setText(u.getEmail());
        telephoneField.setText(u.getTelephone());
        statusCombo.setValue(u.getStatut());
        blockedCheck.setSelected(u.isBloque());
        passwordField.clear();
        roleCombo.getItems().stream().filter(x->x.getId().equals(u.getRoleId())).findFirst().ifPresent(roleCombo::setValue);
    }
}

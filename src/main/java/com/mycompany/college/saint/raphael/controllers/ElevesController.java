package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.App;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.*;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.stage.FileChooser;
import java.io.*;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;
public class ElevesController {
    @FXML private TextField searchField,nomField,postnomField,prenomField,lieuField,nationaliteField,adresseField,telephoneField,emailField,pereField,mereField,provenanceField;
    @FXML private ComboBox<String> sexeCombo,etatParentsCombo,statutCombo;
    @FXML private ComboBox<Classe> classeCombo;
    @FXML private ComboBox<AnneeScolaire> anneeCombo;
    @FXML private DatePicker naissancePicker;
    @FXML private ImageView photoView;
    @FXML private Label formTitle,matriculePreview,countLabel;
    @FXML private TableView<Eleve> table;
    @FXML private TableColumn<Eleve,String> matriculeCol,nomCol,sexeCol,classeCol,anneeCol,statutCol;
    private final EleveDAO dao=new EleveDAO();
    private final ClasseDAO classeDAO=new ClasseDAO();
    private final AnneeScolaireDAO anneeDAO=new AnneeScolaireDAO();
    private final EnrollmentService enrollmentService=new EnrollmentService();
    private Eleve selected;
    private byte[] photoBytes;
    @FXML public void initialize() {
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        sexeCol.setCellValueFactory(new PropertyValueFactory<>("sexe"));
        classeCol.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        anneeCol.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        sexeCombo.setItems(FXCollections.observableArrayList("MASCULIN","FEMININ"));
        etatParentsCombo.setItems(FXCollections.observableArrayList("MARIES","SEPARES","DIVORCES","VEUF_VEUVE","AUTRE"));
        statutCombo.setItems(FXCollections.observableArrayList("ACTIF","INACTIF","EXCLU","TRANSFERE"));
        sexeCombo.setValue("MASCULIN");
        statutCombo.setValue("ACTIF");
        nationaliteField.setText("Congolaise");
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->populate(b));
        SearchDebouncer.bind(searchField, this::load);
        loadCombos();
        load();
    }
    private void loadCombos() {
        try {
            classeCombo.setItems(FXCollections.observableArrayList(classeDAO.findActive()));
            List<AnneeScolaire>years=anneeDAO.findAll();
            anneeCombo.setItems(FXCollections.observableArrayList(years));
            years.stream().filter(AnneeScolaire::isActive).findFirst().ifPresent(anneeCombo::setValue);
        }
        catch(SQLException e) {
            AlertUtils.showError("Chargement",e.getMessage());
        }
    }
    @FXML private void load() {
        try {
            List<Eleve>items=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(items));
            countLabel.setText(items.size()+" élève(s)");
        }
        catch(SQLException e) {
            AlertUtils.showError("Élèves",e.getMessage());
        }
    }
    @FXML private void newStudent() {
        clear();
    }
    @FXML private void choosePhoto() {
        FileChooser fc=new FileChooser();
        fc.setTitle("Choisir une photo");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
        File f=fc.showOpenDialog(App.getPrimaryStage());
        if(f==null)return;
        try {
            if(f.length()>2_000_000)throw new IOException("La photo dépasse 2 Mo.");
            photoBytes=Files.readAllBytes(f.toPath());
            photoView.setImage(new Image(new ByteArrayInputStream(photoBytes)));
        }
        catch(IOException e) {
            AlertUtils.showError("Photo",e.getMessage());
        }
    }
    @FXML private void save() {
        try {
            Eleve e=selected==null?new Eleve():selected;
            e.setNom(ValidationUtils.required(nomField.getText(),"Le nom").toUpperCase());
            e.setPostnom(ValidationUtils.optional(postnomField.getText()));
            if(e.getPostnom()!=null)e.setPostnom(e.getPostnom().toUpperCase());
            e.setPrenom(ValidationUtils.required(prenomField.getText(),"Le prénom"));
            if(sexeCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le sexe.");
            if(statutCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le statut.");
            e.setSexe(sexeCombo.getValue());
            e.setDateNaissance(ValidationUtils.birthDate(naissancePicker.getValue()));
            e.setLieuNaissance(ValidationUtils.optional(lieuField.getText()));
            e.setNationalite(ValidationUtils.optional(nationaliteField.getText()));
            e.setAdresse(ValidationUtils.optional(adresseField.getText()));
            e.setTelephone(ValidationUtils.optional(telephoneField.getText()));
            e.setEmail(ValidationUtils.email(emailField.getText()));
            e.setPhoto(photoBytes);
            e.setNomPere(ValidationUtils.optional(pereField.getText()));
            e.setNomMere(ValidationUtils.optional(mereField.getText()));
            e.setEtatCivilParents(etatParentsCombo.getValue());
            e.setEtablissementProvenance(ValidationUtils.optional(provenanceField.getText()));
            e.setStatut(statutCombo.getValue());
            if(selected==null) {
                Classe cl=classeCombo.getValue();
                AnneeScolaire y=anneeCombo.getValue();
                if(cl==null||y==null)throw new IllegalArgumentException("Sélectionnez la classe et l'année scolaire initiales.");
                enrollmentService.createStudentAndEnroll(e,cl,y,"Inscription initiale");
                AlertUtils.showSuccess("Élève créé","Le dossier et l'inscription ont été enregistrés. Matricule : "+e.getMatricule());
            }
            else {
                dao.update(e);
                AuditService.getInstance().log("MODIFICATION","ELEVE",e.getId(),"Mise à jour du dossier "+e.getMatricule());
                AlertUtils.showSuccess("Modification","Le dossier a été mis à jour.");
            }
            clear();
            load();
        }
        catch(Exception ex) {
            AlertUtils.showError("Enregistrement impossible",ex.getMessage());
        }
    }
    @FXML private void delete() {
        Eleve e=table.getSelectionModel().getSelectedItem();
        if(e==null) {
            AlertUtils.showWarning("Sélection","Sélectionnez un élève.");
            return;
        }
        if(AlertUtils.confirmDelete(e.getNomComplet()))try {
            dao.softDelete(e.getId());
            AuditService.getInstance().log("SUPPRESSION","ELEVE",e.getId(),"Suppression logique "+e.getMatricule());
            clear();
            load();
        }
        catch(SQLException ex) {
            AlertUtils.showError("Suppression",ex.getMessage());
        }
    }
    @FXML private void export() {
        ExportUtils.exportTable(table,"eleves");
    }
    private void populate(Eleve e) {
        if(e==null)return;
        selected=e;
        formTitle.setText("Modifier le dossier");
        matriculePreview.setText(e.getMatricule());
        nomField.setText(e.getNom());
        postnomField.setText(e.getPostnom());
        prenomField.setText(e.getPrenom());
        sexeCombo.setValue(e.getSexe());
        naissancePicker.setValue(e.getDateNaissance());
        lieuField.setText(e.getLieuNaissance());
        nationaliteField.setText(e.getNationalite());
        adresseField.setText(e.getAdresse());
        telephoneField.setText(e.getTelephone());
        emailField.setText(e.getEmail());
        pereField.setText(e.getNomPere());
        mereField.setText(e.getNomMere());
        etatParentsCombo.setValue(e.getEtatCivilParents());
        provenanceField.setText(e.getEtablissementProvenance());
        statutCombo.setValue(e.getStatut());
        photoBytes=e.getPhoto();
        photoView.setImage(photoBytes==null?null:new Image(new ByteArrayInputStream(photoBytes)));
        classeCombo.setDisable(true);
        anneeCombo.setDisable(true);
    }
    private void clear() {
        selected=null;
        table.getSelectionModel().clearSelection();
        formTitle.setText("Nouvel élève");
        matriculePreview.setText("Généré automatiquement");
        for(TextField f:List.of(nomField,postnomField,prenomField,lieuField,adresseField,telephoneField,emailField,pereField,mereField,provenanceField))f.clear();
        nationaliteField.setText("Congolaise");
        sexeCombo.setValue("MASCULIN");
        etatParentsCombo.setValue(null);
        statutCombo.setValue("ACTIF");
        naissancePicker.setValue(null);
        photoBytes=null;
        photoView.setImage(null);
        classeCombo.setDisable(false);
        anneeCombo.setDisable(false);
    }
}

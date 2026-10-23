package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.services.*;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.*;
public class InscriptionsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<Eleve>studentCombo;
    @FXML private ComboBox<Classe>classCombo;
    @FXML private ComboBox<AnneeScolaire>yearCombo;
    @FXML private DatePicker datePicker;
    @FXML private TextArea observationsArea;
    @FXML private TableView<Inscription>table;
    @FXML private TableColumn<Inscription,String>numberCol,studentCol,matriculeCol,classCol,yearCol,statusCol;
    @FXML private TableColumn<Inscription,java.time.LocalDate>dateCol;
    @FXML private Label countLabel;
    private final InscriptionDAO dao=new InscriptionDAO();
    private final EleveDAO eleveDAO=new EleveDAO();
    private final ClasseDAO classeDAO=new ClasseDAO();
    private final AnneeScolaireDAO yearDAO=new AnneeScolaireDAO();
    private final EnrollmentService service=new EnrollmentService();
    @FXML public void initialize() {
        numberCol.setCellValueFactory(new PropertyValueFactory<>("numInscription"));
        studentCol.setCellValueFactory(new PropertyValueFactory<>("eleveNom"));
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        classCol.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("anneeScolaire"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));
        SearchDebouncer.bind(searchField, this::load);
        yearCombo.valueProperty().addListener((o,a,b)->loadStudents());
        datePicker.setValue(java.time.LocalDate.now());
        loadCombos();
        load();
    }
    private void loadCombos() {
        try {
            classCombo.setItems(FXCollections.observableArrayList(classeDAO.findActive()));
            List<AnneeScolaire>ys=yearDAO.findAll();
            yearCombo.setItems(FXCollections.observableArrayList(ys));
            ys.stream().filter(AnneeScolaire::isActive).findFirst().ifPresent(yearCombo::setValue);
            loadStudents();
        }
        catch(SQLException e) {
            AlertUtils.showError("Chargement",e.getMessage());
        }
    }
    private void loadStudents() {
        AnneeScolaire y=yearCombo.getValue();
        if(y==null)return;
        try {
            studentCombo.setItems(FXCollections.observableArrayList(eleveDAO.findAvailableForYear(y.getId())));
        }
        catch(SQLException e) {
            AlertUtils.showError("Élèves",e.getMessage());
        }
    }
    @FXML private void load() {
        try {
            List<Inscription>l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" inscription(s)");
        }
        catch(SQLException e) {
            AlertUtils.showError("Inscriptions",e.getMessage());
        }
    }
    @FXML private void save() {
        try {
            Eleve e=studentCombo.getValue();
            Classe c=classCombo.getValue();
            AnneeScolaire y=yearCombo.getValue();
            if(e==null||c==null||y==null)throw new IllegalArgumentException("Sélectionnez l'élève, la classe et l'année scolaire.");
            service.enrollExisting(e,c,y,datePicker.getValue(),ValidationUtils.optional(observationsArea.getText()));
            studentCombo.setValue(null);
            classCombo.setValue(null);
            observationsArea.clear();
            loadStudents();
            load();
            AlertUtils.showSuccess("Inscription","L'inscription a été enregistrée.");
        }
        catch(Exception e) {
            AlertUtils.showError("Inscription impossible",e.getMessage());
        }
    }
    @FXML private void cancel() {
        Inscription i=table.getSelectionModel().getSelectedItem();
        if(i!=null&&AlertUtils.confirm("Annuler l'inscription","Annuler "+i.getNumInscription()+" ?"))try {
            dao.cancel(i.getId());
            AuditService.getInstance().log("ANNULATION","INSCRIPTION",i.getId(),i.getNumInscription());
            loadStudents();
            load();
        }
        catch(SQLException e) {
            AlertUtils.showError("Annulation",e.getMessage());
        }
    }
    @FXML private void export() {
        ExportUtils.exportTable(table,"inscriptions");
    }
}

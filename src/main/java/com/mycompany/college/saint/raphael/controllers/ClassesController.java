package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.ClasseDAO;
import com.mycompany.college.saint.raphael.models.Classe;
import com.mycompany.college.saint.raphael.services.AuditService;
import com.mycompany.college.saint.raphael.utils.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.util.*;
public class ClassesController {
    @FXML private TextField searchField,nomField,optionField,capacityField;
    @FXML private ComboBox<String>niveauCombo,sectionCombo,statusCombo;
    @FXML private TableView<Classe>table;
    @FXML private TableColumn<Classe,String>nomCol,niveauCol,sectionCol,optionCol,occupationCol,statusCol;
    @FXML private Label formTitle,countLabel;
    private final ClasseDAO dao=new ClasseDAO();
    private Classe selected;
    @FXML public void initialize() {
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        niveauCol.setCellValueFactory(new PropertyValueFactory<>("niveau"));
        sectionCol.setCellValueFactory(new PropertyValueFactory<>("section"));
        optionCol.setCellValueFactory(new PropertyValueFactory<>("option"));
        occupationCol.setCellValueFactory(new PropertyValueFactory<>("occupation"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("statut"));
        niveauCombo.setItems(FXCollections.observableArrayList("SEPT","HUIT","NEUF","DIX","ONZE","DOUZE"));
        sectionCombo.setItems(FXCollections.observableArrayList("GENERALE","SCIENTIFIQUE","LITTERAIRE","COMMERCIALE","PEDAGOGIQUE","TECHNIQUE"));
        statusCombo.setItems(FXCollections.observableArrayList("ACTIVE","INACTIVE"));
        statusCombo.setValue("ACTIVE");
        table.getSelectionModel().selectedItemProperty().addListener((o,a,b)->populate(b));
        SearchDebouncer.bind(searchField, this::load);
        load();
    }
    @FXML private void load() {
        try {
            List<Classe>l=dao.findAll(searchField.getText());
            table.setItems(FXCollections.observableArrayList(l));
            countLabel.setText(l.size()+" classe(s)");
        }
        catch(SQLException e) {
            AlertUtils.showError("Classes",e.getMessage());
        }
    }
    @FXML private void save() {
        try {
            Classe o=selected==null?new Classe():selected;
            o.setNom(ValidationUtils.required(nomField.getText(),"Le nom"));
            if(niveauCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le niveau.");
            if(sectionCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez la section.");
            if(statusCombo.getValue()==null)throw new IllegalArgumentException("Sélectionnez le statut.");
            o.setNiveau(niveauCombo.getValue());
            o.setSection(sectionCombo.getValue());
            o.setOption(ValidationUtils.optional(optionField.getText()));
            o.setCapaciteMax(ValidationUtils.positive(Integer.parseInt(capacityField.getText()),"La capacité"));
            o.setStatut(statusCombo.getValue());
            long id;
            if(selected==null) {
                id=dao.insert(o);
                AuditService.getInstance().log("CREATION","CLASSE",id,o.getNom());
            }
            else {
                dao.update(o);
                id=o.getId();
                AuditService.getInstance().log("MODIFICATION","CLASSE",id,o.getNom());
            }
            clear();
            load();
        }
        catch(NumberFormatException e) {
            AlertUtils.showError("Validation","La capacité doit être un nombre entier.");
        }
        catch(Exception e) {
            AlertUtils.showError("Enregistrement",e.getMessage());
        }
    }
    @FXML private void delete() {
        Classe o=table.getSelectionModel().getSelectedItem();
        if(o!=null&&AlertUtils.confirmDelete(o.getNom()))try {
            dao.delete(o.getId());
            AuditService.getInstance().log("SUPPRESSION","CLASSE",o.getId(),o.getNom());
            clear();
            load();
        }
        catch(SQLException e) {
            AlertUtils.showError("Suppression",e.getMessage());
        }
    }
    @FXML private void clear() {
        selected=null;
        table.getSelectionModel().clearSelection();
        formTitle.setText("Nouvelle classe");
        nomField.clear();
        optionField.clear();
        capacityField.setText("35");
        niveauCombo.setValue(null);
        sectionCombo.setValue(null);
        statusCombo.setValue("ACTIVE");
    }
    @FXML private void export() {
        ExportUtils.exportTable(table,"classes");
    }
    private void populate(Classe o) {
        if(o==null)return;
        selected=o;
        formTitle.setText("Modifier la classe");
        nomField.setText(o.getNom());
        niveauCombo.setValue(o.getNiveau());
        sectionCombo.setValue(o.getSection());
        optionField.setText(o.getOption());
        capacityField.setText(String.valueOf(o.getCapaciteMax()));
        statusCombo.setValue(o.getStatut());
    }
}

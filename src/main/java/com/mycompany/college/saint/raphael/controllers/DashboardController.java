package com.mycompany.college.saint.raphael.controllers;
import com.mycompany.college.saint.raphael.dao.*;
import com.mycompany.college.saint.raphael.models.*;
import com.mycompany.college.saint.raphael.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.*;
public class DashboardController {
    @FXML private Label totalStudents,totalBoys,totalGirls,totalClasses,totalEnrollments,totalUsers,yearLabel;
    @FXML private PieChart genderChart;
    @FXML private BarChart<String,Number> classChart;
    @FXML private TableView<Eleve> recentTable;
    @FXML private TableColumn<Eleve,String> recentMatricule,recentName,recentClass;
    private final DashboardDAO dao=new DashboardDAO();
    @FXML public void initialize() {
        recentMatricule.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        recentName.setCellValueFactory(new PropertyValueFactory<>("nomComplet"));
        recentClass.setCellValueFactory(new PropertyValueFactory<>("classeNom"));
        refresh();
    }
    @FXML private void refresh() {
        try {
            Map<String,Integer>s=dao.stats();
            totalStudents.setText(String.valueOf(s.getOrDefault("eleves",0)));
            totalBoys.setText(String.valueOf(s.getOrDefault("garcons",0)));
            totalGirls.setText(String.valueOf(s.getOrDefault("filles",0)));
            totalClasses.setText(String.valueOf(s.getOrDefault("classes",0)));
            totalEnrollments.setText(String.valueOf(s.getOrDefault("inscriptions",0)));
            totalUsers.setText(String.valueOf(s.getOrDefault("utilisateurs",0)));
            genderChart.setData(FXCollections.observableArrayList(new PieChart.Data("Garçons",s.getOrDefault("garcons",0)),new PieChart.Data("Filles",s.getOrDefault("filles",0))));
            classChart.getData().clear();
            XYChart.Series<String,Number>series=new XYChart.Series<>();
            series.setName("Élèves inscrits");
            dao.enrollmentByClass().forEach((k,v)->series.getData().add(new XYChart.Data<>(k,v)));
            classChart.getData().add(series);
            recentTable.setItems(FXCollections.observableArrayList(dao.recentStudents()));
            AnneeScolaire y=new AnneeScolaireDAO().findActive();
            yearLabel.setText(y==null?"Aucune année scolaire active":y.getLibelle());
        }
        catch(Exception e) {
            AlertUtils.showError("Tableau de bord",e.getMessage());
        }
    }
}

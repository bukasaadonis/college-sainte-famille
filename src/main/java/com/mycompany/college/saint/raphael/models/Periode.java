package com.mycompany.college.saint.raphael.models;
import java.time.LocalDate;
public class Periode {
    private Long id;
    private Long anneeScolaireId;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String type;
    private String anneeScolaire;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public Long getAnneeScolaireId() {
        return anneeScolaireId;
    }
    public void setAnneeScolaireId(Long v) {
        anneeScolaireId=v;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String v) {
        nom=v;
    }
    public LocalDate getDateDebut() {
        return dateDebut;
    }
    public void setDateDebut(LocalDate v) {
        dateDebut=v;
    }
    public LocalDate getDateFin() {
        return dateFin;
    }
    public void setDateFin(LocalDate v) {
        dateFin=v;
    }
    public String getType() {
        return type;
    }
    public void setType(String v) {
        type=v;
    }
    public String getAnneeScolaire() {
        return anneeScolaire;
    }
    public void setAnneeScolaire(String v) {
        anneeScolaire=v;
    }
    @Override public String toString() {
        return nom+" — "+anneeScolaire;
    }
}

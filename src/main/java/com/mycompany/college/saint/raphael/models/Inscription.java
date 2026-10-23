package com.mycompany.college.saint.raphael.models;
import java.time.LocalDate;
public class Inscription {
    private Long id;
    private Long eleveId;
    private Long classeId;
    private Long anneeScolaireId;
    private String numInscription;
    private LocalDate dateInscription;
    private String statut;
    private String observations;
    private String eleveNom;
    private String matricule;
    private String classeNom;
    private String anneeScolaire;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public Long getEleveId() {
        return eleveId;
    }
    public void setEleveId(Long v) {
        eleveId=v;
    }
    public Long getClasseId() {
        return classeId;
    }
    public void setClasseId(Long v) {
        classeId=v;
    }
    public Long getAnneeScolaireId() {
        return anneeScolaireId;
    }
    public void setAnneeScolaireId(Long v) {
        anneeScolaireId=v;
    }
    public String getNumInscription() {
        return numInscription;
    }
    public void setNumInscription(String v) {
        numInscription=v;
    }
    public LocalDate getDateInscription() {
        return dateInscription;
    }
    public void setDateInscription(LocalDate v) {
        dateInscription=v;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String v) {
        statut=v;
    }
    public String getObservations() {
        return observations;
    }
    public void setObservations(String v) {
        observations=v;
    }
    public String getEleveNom() {
        return eleveNom;
    }
    public void setEleveNom(String v) {
        eleveNom=v;
    }
    public String getMatricule() {
        return matricule;
    }
    public void setMatricule(String v) {
        matricule=v;
    }
    public String getClasseNom() {
        return classeNom;
    }
    public void setClasseNom(String v) {
        classeNom=v;
    }
    public String getAnneeScolaire() {
        return anneeScolaire;
    }
    public void setAnneeScolaire(String v) {
        anneeScolaire=v;
    }
}

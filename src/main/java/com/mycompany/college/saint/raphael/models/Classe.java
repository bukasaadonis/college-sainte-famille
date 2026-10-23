package com.mycompany.college.saint.raphael.models;
import java.time.LocalDateTime;
public class Classe {
    private Long id;
    private String nom;
    private String niveau;
    private String section;
    private String option;
    private int capaciteMax;
    private String statut;
    private LocalDateTime dateCreation;
    private int effectif;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String v) {
        nom=v;
    }
    public String getNiveau() {
        return niveau;
    }
    public void setNiveau(String v) {
        niveau=v;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String v) {
        section=v;
    }
    public String getOption() {
        return option;
    }
    public void setOption(String v) {
        option=v;
    }
    public int getCapaciteMax() {
        return capaciteMax;
    }
    public void setCapaciteMax(int v) {
        capaciteMax=v;
    }
    public String getStatut() {
        return statut;
    }
    public void setStatut(String v) {
        statut=v;
    }
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    public void setDateCreation(LocalDateTime v) {
        dateCreation=v;
    }
    public int getEffectif() {
        return effectif;
    }
    public void setEffectif(int v) {
        effectif=v;
    }
    public String getOccupation() {
        return effectif+" / "+capaciteMax;
    }
    @Override public String toString() {
        return nom+" — "+section;
    }
}

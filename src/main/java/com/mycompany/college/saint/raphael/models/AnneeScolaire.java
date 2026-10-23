package com.mycompany.college.saint.raphael.models;
import java.time.LocalDate;
public class AnneeScolaire {
    private Long id;
    private String libelle;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String v) {
        libelle=v;
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
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean v) {
        active=v;
    }
    public String getEtat() {
        return active?"ACTIVE":"ARCHIVÉE";
    }
    @Override public String toString() {
        return libelle+(active?" (active)":"");
    }
}

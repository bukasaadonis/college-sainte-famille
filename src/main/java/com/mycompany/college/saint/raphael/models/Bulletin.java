package com.mycompany.college.saint.raphael.models;
import java.time.LocalDateTime;
public class Bulletin {
    private Long id;
    private Long eleveId;
    private Long periodeId;
    private String matricule;
    private String eleveNom;
    private double moyenneGenerale;
    private int rang;
    private String decision;
    private LocalDateTime dateEmission;
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
    public Long getPeriodeId() {
        return periodeId;
    }
    public void setPeriodeId(Long v) {
        periodeId=v;
    }
    public String getMatricule() {
        return matricule;
    }
    public void setMatricule(String v) {
        matricule=v;
    }
    public String getEleveNom() {
        return eleveNom;
    }
    public void setEleveNom(String v) {
        eleveNom=v;
    }
    public double getMoyenneGenerale() {
        return moyenneGenerale;
    }
    public void setMoyenneGenerale(double v) {
        moyenneGenerale=v;
    }
    public int getRang() {
        return rang;
    }
    public void setRang(int v) {
        rang=v;
    }
    public String getDecision() {
        return decision;
    }
    public void setDecision(String v) {
        decision=v;
    }
    public LocalDateTime getDateEmission() {
        return dateEmission;
    }
    public void setDateEmission(LocalDateTime v) {
        dateEmission=v;
    }
    public String getMoyenneAffichee() {
        return String.format("%.2f / 20",moyenneGenerale);
    }
}

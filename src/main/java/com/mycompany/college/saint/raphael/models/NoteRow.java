package com.mycompany.college.saint.raphael.models;
public class NoteRow {
    private Long eleveId;
    private String matricule;
    private String eleveNom;
    private Double valeur;
    private double maximum=20.0;
    private double coefficient=1.0;
    public Long getEleveId() {
        return eleveId;
    }
    public void setEleveId(Long v) {
        eleveId=v;
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
    public Double getValeur() {
        return valeur;
    }
    public void setValeur(Double v) {
        valeur=v;
    }
    public double getMaximum() {
        return maximum;
    }
    public void setMaximum(double v) {
        maximum=v;
    }
    public double getCoefficient() {
        return coefficient;
    }
    public void setCoefficient(double v) {
        coefficient=v;
    }
    public String getPourcentage() {
        return valeur==null?"—":String.format("%.1f %%",valeur/maximum*100);
    }
}

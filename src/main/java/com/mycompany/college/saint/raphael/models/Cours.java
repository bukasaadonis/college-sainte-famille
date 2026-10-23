package com.mycompany.college.saint.raphael.models;
public class Cours {
    private Long id;
    private Long enseignantId;
    private Long classeId;
    private String nom;
    private String code;
    private double coefficient;
    private String description;
    private String enseignantNom;
    private String classeNom;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public Long getEnseignantId() {
        return enseignantId;
    }
    public void setEnseignantId(Long v) {
        enseignantId=v;
    }
    public Long getClasseId() {
        return classeId;
    }
    public void setClasseId(Long v) {
        classeId=v;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String v) {
        nom=v;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String v) {
        code=v;
    }
    public double getCoefficient() {
        return coefficient;
    }
    public void setCoefficient(double v) {
        coefficient=v;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String v) {
        description=v;
    }
    public String getEnseignantNom() {
        return enseignantNom;
    }
    public void setEnseignantNom(String v) {
        enseignantNom=v;
    }
    public String getClasseNom() {
        return classeNom;
    }
    public void setClasseNom(String v) {
        classeNom=v;
    }
    @Override public String toString() {
        return code+" — "+nom;
    }
}

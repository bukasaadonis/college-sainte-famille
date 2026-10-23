package com.mycompany.college.saint.raphael.models;
public class Role {
    private Long id;
    private String nom;
    private String description;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String v) {
        description=v;
    }
    @Override public String toString() {
        return nom;
    }
}

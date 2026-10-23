package com.mycompany.college.saint.raphael.models;
public class Enseignant {
    private Long id;
    private String nom;
    private String postnom;
    private String prenom;
    private String specialite;
    private String email;
    private String telephone;
    private int nombreCours;
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
    public String getPostnom() {
        return postnom;
    }
    public void setPostnom(String v) {
        postnom=v;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String v) {
        prenom=v;
    }
    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String v) {
        specialite=v;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String v) {
        email=v;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String v) {
        telephone=v;
    }
    public int getNombreCours() {
        return nombreCours;
    }
    public void setNombreCours(int v) {
        nombreCours=v;
    }
    public String getNomComplet() {
        return String.join(" ",nom==null?"":nom,postnom==null?"":postnom,prenom==null?"":prenom).replaceAll("\\s+"," ").trim();
    }
    @Override public String toString() {
        return getNomComplet();
    }
}

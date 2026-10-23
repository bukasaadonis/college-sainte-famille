package com.mycompany.college.saint.raphael.models;
public class Responsable {
    private Long id;
    private String nom;
    private String postnom;
    private String prenom;
    private String lienAvecEleve;
    private String profession;
    private String adresse;
    private String telephone;
    private String email;
    private int nombreEleves;
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
    public String getLienAvecEleve() {
        return lienAvecEleve;
    }
    public void setLienAvecEleve(String v) {
        lienAvecEleve=v;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String v) {
        profession=v;
    }
    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String v) {
        adresse=v;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String v) {
        telephone=v;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String v) {
        email=v;
    }
    public int getNombreEleves() {
        return nombreEleves;
    }
    public void setNombreEleves(int v) {
        nombreEleves=v;
    }
    public String getNomComplet() {
        return String.join(" ",nom==null?"":nom,postnom==null?"":postnom,prenom==null?"":prenom).replaceAll("\\s+"," ").trim();
    }
    @Override public String toString() {
        return getNomComplet()+" — "+telephone;
    }
}

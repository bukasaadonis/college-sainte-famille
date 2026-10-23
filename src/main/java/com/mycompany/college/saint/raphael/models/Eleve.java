package com.mycompany.college.saint.raphael.models;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
public class Eleve {
    private Long id;
    private String matricule;
    private String nom;
    private String postnom;
    private String prenom;
    private String sexe;
    private LocalDate dateNaissance;
    private String lieuNaissance;
    private String nationalite;
    private String adresse;
    private String telephone;
    private String email;
    private byte[] photo;
    private String nomPere;
    private String nomMere;
    private String etatCivilParents;
    private String etablissementProvenance;
    private String statut;
    private LocalDateTime dateCreation;
    private boolean supprime;
    private String classeNom;
    private String anneeScolaire;
    private String numInscription;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public String getMatricule() {
        return matricule;
    }
    public void setMatricule(String v) {
        matricule=v;
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
    public String getSexe() {
        return sexe;
    }
    public void setSexe(String v) {
        sexe=v;
    }
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    public void setDateNaissance(LocalDate v) {
        dateNaissance=v;
    }
    public String getLieuNaissance() {
        return lieuNaissance;
    }
    public void setLieuNaissance(String v) {
        lieuNaissance=v;
    }
    public String getNationalite() {
        return nationalite;
    }
    public void setNationalite(String v) {
        nationalite=v;
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
    public byte[] getPhoto() {
        return photo;
    }
    public void setPhoto(byte[] v) {
        photo=v;
    }
    public String getNomPere() {
        return nomPere;
    }
    public void setNomPere(String v) {
        nomPere=v;
    }
    public String getNomMere() {
        return nomMere;
    }
    public void setNomMere(String v) {
        nomMere=v;
    }
    public String getEtatCivilParents() {
        return etatCivilParents;
    }
    public void setEtatCivilParents(String v) {
        etatCivilParents=v;
    }
    public String getEtablissementProvenance() {
        return etablissementProvenance;
    }
    public void setEtablissementProvenance(String v) {
        etablissementProvenance=v;
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
    public boolean isSupprime() {
        return supprime;
    }
    public void setSupprime(boolean v) {
        supprime=v;
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
    public String getNumInscription() {
        return numInscription;
    }
    public void setNumInscription(String v) {
        numInscription=v;
    }
    public int getAge() {
        return dateNaissance==null?0: Period.between(dateNaissance,LocalDate.now()).getYears();
    }
    public String getNomComplet() {
        return String.join(" ",nom==null?"":nom,postnom==null?"":postnom,prenom==null?"":prenom).replaceAll("\\s+"," ").trim();
    }
    @Override public String toString() {
        return getNomComplet()+" — "+matricule;
    }
}

package com.mycompany.college.saint.raphael.models;
import java.time.LocalDateTime;
public class Utilisateur {
    private Long id;
    private Long roleId;
    private String roleNom;
    private String login;
    private String motDePasse;
    private String nom;
    private String postnom;
    private String prenom;
    private String email;
    private String telephone;
    private String statut;
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;
    private int tentativeEchec;
    private boolean bloque;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public Long getRoleId() {
        return roleId;
    }
    public void setRoleId(Long v) {
        roleId=v;
    }
    public String getRoleNom() {
        return roleNom;
    }
    public void setRoleNom(String v) {
        roleNom=v;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String v) {
        login=v;
    }
    public String getMotDePasse() {
        return motDePasse;
    }
    public void setMotDePasse(String v) {
        motDePasse=v;
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
    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }
    public void setDerniereConnexion(LocalDateTime v) {
        derniereConnexion=v;
    }
    public int getTentativeEchec() {
        return tentativeEchec;
    }
    public void setTentativeEchec(int v) {
        tentativeEchec=v;
    }
    public boolean isBloque() {
        return bloque;
    }
    public void setBloque(boolean v) {
        bloque=v;
    }
    public String getNomComplet() {
        return String.join(" ", nom==null?"":nom, postnom==null?"":postnom, prenom==null?"":prenom).replaceAll("\\s+"," ").trim();
    }
    @Override public String toString() {
        return getNomComplet()+" ("+login+")";
    }
}

package com.mycompany.college.saint.raphael.models;
import java.time.LocalDateTime;
public class Historique {
    private Long id;
    private Long utilisateurId;
    private String utilisateurNom;
    private String action;
    private String entite;
    private Long entiteId;
    private String details;
    private LocalDateTime dateAction;
    private String adresseIp;
    public Long getId() {
        return id;
    }
    public void setId(Long v) {
        id=v;
    }
    public Long getUtilisateurId() {
        return utilisateurId;
    }
    public void setUtilisateurId(Long v) {
        utilisateurId=v;
    }
    public String getUtilisateurNom() {
        return utilisateurNom;
    }
    public void setUtilisateurNom(String v) {
        utilisateurNom=v;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String v) {
        action=v;
    }
    public String getEntite() {
        return entite;
    }
    public void setEntite(String v) {
        entite=v;
    }
    public Long getEntiteId() {
        return entiteId;
    }
    public void setEntiteId(Long v) {
        entiteId=v;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String v) {
        details=v;
    }
    public LocalDateTime getDateAction() {
        return dateAction;
    }
    public void setDateAction(LocalDateTime v) {
        dateAction=v;
    }
    public String getAdresseIp() {
        return adresseIp;
    }
    public void setAdresseIp(String v) {
        adresseIp=v;
    }
}

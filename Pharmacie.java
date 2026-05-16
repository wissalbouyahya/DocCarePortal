package com.projet.DocCarePortal.Entity;

import jakarta.persistence.*;

@Entity
public class Pharmacie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPharmacie;

    private String nom;
    private String adresse;
    private String numeroTel;
    private String email;
    private String mdp;
    private String servicelivraison;
    private String status;
    @Column(nullable = false)
    private Boolean etat = true;

    public Boolean getEtat() {
        return etat;
    }

    public void setEtat(Boolean etat) {
        this.etat = etat;
    }

    public Pharmacie() {}

    public Long getIdPharmacie() { return idPharmacie; }
    public void setIdPharmacie(Long idPharmacie) { this.idPharmacie = idPharmacie; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getNumeroTel() { return numeroTel; }
    public void setNumeroTel(String numeroTel) { this.numeroTel = numeroTel; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public String getServicelivraison() { return servicelivraison; }
    public void setServicelivraison(String servicelivraison) { this.servicelivraison = servicelivraison; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

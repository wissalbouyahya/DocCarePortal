package com.projet.DocCarePortal.Entity;

import java.time.LocalDateTime;

public class CreneauDTO {
    private Long id;
    private LocalDateTime dateHeure;
    private boolean reserve;
    private StatutRendezVous statutRendezVous;
    private Long patientId;
    private String nomPatient;
    private String prenomPatient;


    public CreneauDTO() {}

    
    public CreneauDTO(LocalDateTime dateHeure, boolean reserve, StatutRendezVous statut) {
        this.dateHeure = dateHeure;
        this.reserve = reserve;
        this.statutRendezVous = statut;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public boolean isReserve() {
        return reserve;
    }

    public void setReserve(boolean reserve) {
        this.reserve = reserve;
    }

    public StatutRendezVous getStatutRendezVous() {
        return statutRendezVous;
    }

    public void setStatutRendezVous(StatutRendezVous statutRendezVous) {
        this.statutRendezVous = statutRendezVous;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getNomPatient() {
        return nomPatient;
    }

    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }

    public String getPrenomPatient() {
        return prenomPatient;
    }

    public void setPrenomPatient(String prenomPatient) {
        this.prenomPatient = prenomPatient;
    }
}

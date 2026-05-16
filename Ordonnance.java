package com.projet.DocCarePortal.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "ordonnance")
public class Ordonnance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOrdonnance;

    private String reference;

    
    @Column(name = "medecin_id", insertable = false, updatable = false)
    private Long medecinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;

   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Transient
    private Long patientIdInput;

   
    private String nomPatient;
    private Long pharmacieId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateNaissance;

    private String sexe;
    private String telephonePatient;

    @Column(name = "email_patient")
    private String emailPatient;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateConsultation;

    private String diagnostic;
    private String allergies;

    @Column(length = 2000)
    private String medicaments;

    @Column(length = 1000)
    private String instructions;

    private String validite;
    private String renouvellement;
    private Date dateSignature;

 
    private String statut;


    public Long getIdOrdonnance() { return idOrdonnance; }
    public void setIdOrdonnance(Long idOrdonnance) { this.idOrdonnance = idOrdonnance; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public Long getMedecinId() { return medecinId; }
    public void setMedecinId(Long medecinId) { this.medecinId = medecinId; }

    public Medecin getMedecin() { return medecin; }
    public void setMedecin(Medecin medecin) { this.medecin = medecin; }

    @JsonProperty("nomMedecin")
    public String getNomMedecin() {
        if (medecin != null) {
            String nom  = medecin.getNom()    != null ? medecin.getNom()    : "";
            String pren = medecin.getPrenom() != null ? medecin.getPrenom() : "";
            return ("Dr " + nom + " " + pren).trim();
        }
        return null;
    }


    @JsonProperty("specialiteMedecin")
    public String getSpecialiteMedecin() {
        if (medecin != null) return medecin.getSpecialite();
        return null;
    }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    @JsonProperty("patientId")
    public Long getPatientId() {
        if (patient != null) return patient.getIdPatient();
        return patientIdInput;
    }

    @JsonProperty("patientId")
    public void setPatientId(Long patientId) {
        this.patientIdInput = patientId;
    }

    public Long getPatientIdInput() { return patientIdInput; }

    public String getNomPatient() { return nomPatient; }
    public void setNomPatient(String nomPatient) { this.nomPatient = nomPatient; }

    public Long getPharmacieId() { return pharmacieId; }
    public void setPharmacieId(Long pharmacieId) { this.pharmacieId = pharmacieId; }

    public Date getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(Date dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }

    public String getTelephonePatient() { return telephonePatient; }
    public void setTelephonePatient(String telephonePatient) { this.telephonePatient = telephonePatient; }

    public String getEmailPatient() { return emailPatient; }
    public void setEmailPatient(String emailPatient) { this.emailPatient = emailPatient; }

    public Date getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(Date dateConsultation) { this.dateConsultation = dateConsultation; }

    public String getDiagnostic() { return diagnostic; }
    public void setDiagnostic(String diagnostic) { this.diagnostic = diagnostic; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getMedicaments() { return medicaments; }
    public void setMedicaments(String medicaments) { this.medicaments = medicaments; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getValidite() { return validite; }
    public void setValidite(String validite) { this.validite = validite; }

    public String getRenouvellement() { return renouvellement; }
    public void setRenouvellement(String renouvellement) { this.renouvellement = renouvellement; }

    public Date getDateSignature() { return dateSignature; }
    public void setDateSignature(Date dateSignature) { this.dateSignature = dateSignature; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}

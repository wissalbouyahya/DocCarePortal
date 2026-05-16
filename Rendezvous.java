package com.projet.DocCarePortal.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rendezvous")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rendezvous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_heure")
    private LocalDateTime dateHeure;

    @Column(name = "en_ligne")
    private Boolean enLigne = false;

    @Column(name = "lien_visio")
    private String lienVisio;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_rendez_vous")   // ← ajout pour correspondre au nom SQL
    private StatutRendezVous statutRendezVous;

    @Column(name = "est_reserve", nullable = false)
    private boolean estReserve = false;

    @Column(name = "motif")                // ← ajout du champ manquant
    private String motif;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medecin_id")
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    private Patient patient;
}

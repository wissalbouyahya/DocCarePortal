package com.projet.DocCarePortal.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "paiement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendezvous_id", nullable = false)
    private Rendezvous rendezvous;

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", length = 30)
    private StatutPaiement statutPaiement; 

    @Column(name = "methode_paiement")
    private String methodePaiement; 

    @Column(name = "reference_transaction")
    private String referenceTransaction;

    @Column(name = "lien_paiement")
    private String lienPaiement; 
}

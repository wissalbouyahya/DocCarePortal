package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.*;
import com.projet.DocCarePortal.Repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    public Paiement initierPaiement(Rendezvous rdv) {
        
        paiementRepository.findByRendezvous(rdv).ifPresent(p -> {
            if (p.getStatutPaiement() == StatutPaiement.SUCCESS) {
                throw new IllegalStateException("Ce rendez-vous est déjà payé");
            }
        });

        Paiement paiement = new Paiement();
        paiement.setRendezvous(rdv);
        paiement.setMontant(50.0); 
        paiement.setStatutPaiement(StatutPaiement.PENDING);
        paiement.setMethodePaiement("CARTE");

        String ref = UUID.randomUUID().toString();
        paiement.setReferenceTransaction(ref);
        paiement.setLienPaiement("https://votre-passerelle.com/pay?ref=" + ref + "&rdv=" + rdv.getId());

        return paiementRepository.save(paiement);
    }

    public void enregistrerSuccesPaiement(Rendezvous rdv, String referenceTransaction) {
        Paiement paiement = paiementRepository.findByRendezvous(rdv)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        paiement.setStatutPaiement(StatutPaiement.SUCCESS);
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setReferenceTransaction(referenceTransaction);
        paiementRepository.save(paiement);
    }

    public void enregistrerEchecPaiement(Rendezvous rdv) {
        Paiement paiement = paiementRepository.findByRendezvous(rdv)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        paiement.setStatutPaiement(StatutPaiement.FAILED);
        paiementRepository.save(paiement);
    }

    public boolean payerAvecStripe(Rendezvous rdv, String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        Paiement paiement = paiementRepository.findByRendezvous(rdv)
                .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

        paiement.setStatutPaiement(StatutPaiement.SUCCESS);
        paiement.setDatePaiement(LocalDateTime.now());
        paiement.setReferenceTransaction(token);

        paiementRepository.save(paiement);
        return true;
    }
}

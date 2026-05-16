package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Paiement;
import com.projet.DocCarePortal.Entity.Rendezvous;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    Optional<Paiement> findByRendezvous(Rendezvous rendezvous);

}

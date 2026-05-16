package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Pharmacie;

import java.util.List;
import java.util.Optional;

public interface PharmacieService {
    List<Pharmacie> afficherPharmacies();
    Optional<Pharmacie> afficherPharmacieParId(Long id);
    Pharmacie ajouterPharmacie(Pharmacie pharmacie);
    Pharmacie modifierPharmacie(Pharmacie pharmacie);
    void supprimerPharmacie(Long id);
}

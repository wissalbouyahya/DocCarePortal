package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Laboratoire;

import java.util.List;
import java.util.Optional;

public interface LaboratoireService {
    List<Laboratoire> afficherLaboratoires();
    Optional<Laboratoire> afficherLaboratoireParId(Long id);
    Laboratoire ajouterLaboratoire(Laboratoire labo);
    Laboratoire modifierLaboratoire(Laboratoire labo);
    void supprimerLaboratoire(Long id);
}

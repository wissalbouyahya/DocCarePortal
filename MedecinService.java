package com.projet.DocCarePortal.Service;


import com.projet.DocCarePortal.Entity.Medecin;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public interface MedecinService {

    Medecin ajoutermedecin(Medecin medecin);

    Medecin  modifiermedecin(Medecin medecin);

    List<Medecin> affichiermedecin();

    void supprimermedecin(Long idMedecin);

    Optional<Medecin> affichiermedecinparid(Long idMedecin);
}

package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.CreneauDTO;
import com.projet.DocCarePortal.Entity.Rendezvous;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RendezvousService {

    
    Rendezvous creerRendezVous(Long medecinId, Long patientId, LocalDateTime dateHeure, boolean enLigne);

    List<CreneauDTO> genererCreneauxPourMedecin(Long medecinId, LocalDate date);

    List<CreneauDTO> listerCreneauxDisponiblesDTO(Long medecinId, LocalDate date);

    List<CreneauDTO> listerTousCreneaux(Long medecinId, LocalDate date);

    void annulerRendezVous(Long id);

    void bloquerCreneauParDate(Long medecinId, LocalDateTime dateHeure);

    void debloquerCreneau(Long medecinId, LocalDateTime dateHeure);

    void bloquerJournee(Long medecinId, LocalDate date);

    void debloquerJournee(Long medecinId, LocalDate date);

    List<Rendezvous> listerCreneauxDisponibles(Long medecinId, LocalDate date);

    List<Rendezvous> afficherRendezvousByMedecinId(Long medecinId);

    List<Rendezvous> listerRendezVousParMedecin(Long medecinId, LocalDate date);

    List<Rendezvous> listerRendezVousParPatient(Long patientId);

    List<Rendezvous> getRendezVousByMedecinAndStatut(Long medecinId, String status);

    Rendezvous modifierRendezVous(Long id, LocalDateTime nouvelleDate, String modeConsultation);

    Rendezvous confirmerRendezVous(Long rdvId);

    Rendezvous validerPaiement(Long rdvId, String referenceTransaction);

    Rendezvous echecPaiement(Long rdvId);

    Rendezvous demarrerConsultation(Long rdvId);

    Rendezvous terminerConsultation(Long rdvId);
}

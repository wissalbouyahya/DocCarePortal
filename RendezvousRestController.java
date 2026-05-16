package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.*;
import com.projet.DocCarePortal.Exception.ResourceConflictException;
import com.projet.DocCarePortal.Repository.RendezvousRepository;
import com.projet.DocCarePortal.Services.PaiementService;
import com.projet.DocCarePortal.Services.RendezvousService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rendezvous")
@CrossOrigin("*")
public class RendezvousRestController {

    @Autowired
    private RendezvousService rendezvousService;

    @Autowired
    private RendezvousRepository rendezvousRepository;

    @Autowired
    private PaiementService paiementService;

   
    @GetMapping("/medecin/{medecinId}/creneaux/{date}")
    public ResponseEntity<?> getCreneauxDisponibles(
            @PathVariable Long medecinId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(
                    rendezvousService.listerCreneauxDisponiblesDTO(medecinId, date)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur chargement créneaux: " + e.getMessage());
        }
    }

   
    @PostMapping("/creer")
    public ResponseEntity<?> creerRendezVous(
            @RequestParam Long medecinId,
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure,
            @RequestParam(defaultValue = "PHYSIQUE") String modeConsultation) {
        try {
            boolean enLigne = "EN_LIGNE".equalsIgnoreCase(modeConsultation);
            Rendezvous rdv = rendezvousService.creerRendezVous(medecinId, patientId, dateHeure, enLigne);

            Map<String, Object> response = new HashMap<>();
            response.put("id", rdv.getId());
            response.put("dateHeure", rdv.getDateHeure());
            response.put("statut", rdv.getStatutRendezVous());
            response.put("modeConsultation", rdv.getEnLigne() != null && rdv.getEnLigne()
                    ? "EN_LIGNE" : "PHYSIQUE");

            return ResponseEntity.ok(response);

        } catch (ResourceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur lors de la réservation: " + e.getMessage());
        }
    }

   
    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerRendezVous(@PathVariable Long id) {
        try {
            rendezvousService.annulerRendezVous(id);
            return ResponseEntity.ok("Rendez-vous annulé");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur annulation: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<?> confirmerRDV(@PathVariable Long id) {
        try {
            Rendezvous rdv = rendezvousService.confirmerRendezVous(id);
            Map<String, Object> response = new HashMap<>();
            response.put("id", rdv.getId());
            response.put("statut", rdv.getStatutRendezVous());
            response.put("lienVisio", rdv.getLienVisio());
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur confirmation: " + e.getMessage());
        }
    }

 
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierRendezVous(
            @PathVariable Long id,
            @RequestBody ModificationRdvDTO dto) {
        try {
            Rendezvous rdv = rendezvousService.modifierRendezVous(id, dto.getDateHeure(),
                    dto.getModeConsultation());
            Map<String, Object> response = new HashMap<>();
            response.put("id", rdv.getId());
            response.put("statut", rdv.getStatutRendezVous());
            return ResponseEntity.ok(response);
        } catch (ResourceConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

  
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<?> getRendezVousParMedecin(@PathVariable Long medecinId) {
        try {
            List<Rendezvous> rdvs = rendezvousService.afficherRendezvousByMedecinId(medecinId);
            List<Map<String, Object>> result = rdvs.stream().map(rdv -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rdv.getId());
                map.put("dateHeure", rdv.getDateHeure());
                map.put("statutRendezVous", rdv.getStatutRendezVous().name());
                map.put("reserve", rdv.isEstReserve());
                boolean enLigne = rdv.getEnLigne() != null && rdv.getEnLigne();
                map.put("modeConsultation", enLigne ? "EN_LIGNE" : "PHYSIQUE");
                map.put("lienVisio", rdv.getLienVisio());
                if (rdv.getPatient() != null) {
                    map.put("prenomPatient", rdv.getPatient().getPrenom());
                    map.put("nomPatient", rdv.getPatient().getNom());
                    map.put("patientId", rdv.getPatient().getIdPatient());
                    map.put("telephonePatient", rdv.getPatient().getNumeroTel());
                } else {
                    map.put("prenomPatient", null);
                    map.put("nomPatient", null);
                    map.put("patientId", null);
                    map.put("telephonePatient", null);
                }
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur récupération: " + e.getMessage());
        }
    }

   
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getRendezVousParPatient(@PathVariable Long patientId) {
        try {
            List<Rendezvous> rdvs = rendezvousService.listerRendezVousParPatient(patientId);
            List<Map<String, Object>> result = rdvs.stream().map(rdv -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rdv.getId());
                map.put("dateHeure", rdv.getDateHeure());
                map.put("statutRendezVous", rdv.getStatutRendezVous() != null
                        ? rdv.getStatutRendezVous().name() : null);
                map.put("reserve", rdv.isEstReserve());
                boolean enLigne = rdv.getEnLigne() != null && rdv.getEnLigne();
                map.put("modeConsultation", enLigne ? "EN_LIGNE" : "PHYSIQUE");
                map.put("lienVisio", rdv.getLienVisio());
                if (rdv.getMedecin() != null) {
                    Map<String, Object> medMap = new HashMap<>();
                    medMap.put("idMedecin", rdv.getMedecin().getIdMedecin());
                    medMap.put("nom", rdv.getMedecin().getNom());
                    medMap.put("prenom", rdv.getMedecin().getPrenom());
                    medMap.put("specialite", rdv.getMedecin().getSpecialite());
                    map.put("medecin", medMap);
                    
                    map.put("idMedecin", rdv.getMedecin().getIdMedecin());
                } else {
                    map.put("medecin", null);
                    map.put("idMedecin", null);
                }
                return map;
            }).collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erreur récupération: " + e.getMessage());
        }
    }

  
    public ResponseEntity<?> bloquerJournee(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            rendezvousService.bloquerJournee(medecinId, date);
            return ResponseEntity.ok("Journée bloquée");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

   
    @PostMapping("/{medecinId}/bloquer")
    public ResponseEntity<?> bloquerCreneau(
            @PathVariable Long medecinId,
            @RequestBody String dateHeure) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateHeure.trim());
            rendezvousService.bloquerCreneauParDate(medecinId, dt);
            return ResponseEntity.ok("Créneau bloqué");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur blocage: " + e.getMessage());
        }
    }

   
    @PutMapping("/{medecinId}/debloquer")
    public ResponseEntity<?> debloquerCreneau(
            @PathVariable Long medecinId,
            @RequestBody String dateHeure) {
        try {
            LocalDateTime dt = LocalDateTime.parse(dateHeure.trim());
            rendezvousService.debloquerCreneau(medecinId, dt);
            return ResponseEntity.ok("Créneau débloqué");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur déblocage: " + e.getMessage());
        }
    }

    
    @PutMapping("/debloquer-journee")
    public ResponseEntity<?> debloquerJournee(
            @RequestParam Long medecinId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            rendezvousService.debloquerJournee(medecinId, date);
            return ResponseEntity.ok("Journée débloquée");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    @Transactional
    @PostMapping("/{id}/paiement")
    public ResponseEntity<?> payer(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String token = body.get("stripeToken");
        if (token == null || token.isEmpty())
            return ResponseEntity.badRequest().body("Token manquant");

        Rendezvous rdv = rendezvousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RDV introuvable"));

        
        if (rdv.getStatutRendezVous() == StatutRendezVous.PAYE) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Ce rendez-vous est déjà payé");
        }

        if (rdv.getStatutRendezVous() != StatutRendezVous.EN_ATTENTE_PAIEMENT
                && rdv.getStatutRendezVous() != StatutRendezVous.PAIEMENT_ECHOUE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Ce rendez-vous ne peut pas être payé dans son état actuel : "
                            + rdv.getStatutRendezVous()
                            + ". Le médecin doit d'abord confirmer le rendez-vous.");
        }

        paiementService.initierPaiement(rdv);
        boolean success = paiementService.payerAvecStripe(rdv, token);

        if (!success) {
            rendezvousService.echecPaiement(id);
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Paiement échoué");
        }

        Rendezvous rdvPaye = rendezvousService.validerPaiement(id, token);
        Map<String, Object> response = new HashMap<>();
        response.put("statut", rdvPaye.getStatutRendezVous());
        response.put("lienVisio", rdvPaye.getLienVisio());
        return ResponseEntity.ok(response);
    }

   
    @PutMapping("/{id}/demarrer")
    public ResponseEntity<?> demarrerConsultation(@PathVariable Long id) {
        try {
            Rendezvous rdv = rendezvousService.demarrerConsultation(id);
            Map<String, Object> response = new HashMap<>();
            response.put("statut", rdv.getStatutRendezVous());
            response.put("lienVisio", rdv.getLienVisio());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminerConsultation(@PathVariable Long id) {
        try {
            Rendezvous rdv = rendezvousService.terminerConsultation(id);
            Map<String, Object> response = new HashMap<>();
            response.put("statut", rdv.getStatutRendezVous());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

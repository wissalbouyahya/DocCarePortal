package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.EmailRequest;
import com.projet.DocCarePortal.Entity.Medecin;
import com.projet.DocCarePortal.Entity.Ordonnance;
import com.projet.DocCarePortal.Repository.OrdonnanceRepository;
import com.projet.DocCarePortal.Services.OrdonnanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ordonnances")
@CrossOrigin(origins = "http://localhost:4200")
public class OrdonnanceRestController {

    @Autowired
    private OrdonnanceService ordonnanceService;

    @Autowired
    private OrdonnanceRepository ordonnanceRepository;

  
    static class OrdonnanceDTO {
        private Long idOrdonnance;
        private String reference;
        private Long medecinId;
        private Long patientId;
        private String nomPatient;
        private Long pharmacieId;
        private Date dateNaissance;
        private String sexe;
        private String telephonePatient;
        private String emailPatient;
        private Date dateConsultation;
        private String diagnostic;
        private String allergies;
        private String medicaments;
        private String instructions;
        private String validite;
        private String renouvellement;
        private Date dateSignature;
        private String statut;
        private String nomMedecin;
        private String specialiteMedecin;

        public OrdonnanceDTO(Ordonnance o) {
            this.idOrdonnance = o.getIdOrdonnance();
            this.reference = o.getReference();
            this.medecinId = o.getMedecinId();
            this.patientId = o.getPatient() != null ? o.getPatient().getIdPatient() : o.getPatientIdInput();
            this.nomPatient = o.getNomPatient();
            this.pharmacieId = o.getPharmacieId();
            this.dateNaissance = o.getDateNaissance();
            this.sexe = o.getSexe();
            this.telephonePatient = o.getTelephonePatient();
            this.emailPatient = o.getEmailPatient();
            this.dateConsultation = o.getDateConsultation();
            this.diagnostic = o.getDiagnostic();
            this.allergies = o.getAllergies();
            this.medicaments = o.getMedicaments();
            this.instructions = o.getInstructions();
            this.validite = o.getValidite();
            this.renouvellement = o.getRenouvellement();
            this.dateSignature = o.getDateSignature();
            this.statut = o.getStatut();
            Medecin med = o.getMedecin();
            if (med != null) {
                this.nomMedecin = med.getNom() + " " + med.getPrenom();
                this.specialiteMedecin = med.getSpecialite();
            } else {
                this.nomMedecin = null;
                this.specialiteMedecin = null;
            }
        }

       
        public Long getIdOrdonnance() { return idOrdonnance; }
        public String getReference() { return reference; }
        public Long getMedecinId() { return medecinId; }
        public Long getPatientId() { return patientId; }
        public String getNomPatient() { return nomPatient; }
        public Long getPharmacieId() { return pharmacieId; }
        public Date getDateNaissance() { return dateNaissance; }
        public String getSexe() { return sexe; }
        public String getTelephonePatient() { return telephonePatient; }
        public String getEmailPatient() { return emailPatient; }
        public Date getDateConsultation() { return dateConsultation; }
        public String getDiagnostic() { return diagnostic; }
        public String getAllergies() { return allergies; }
        public String getMedicaments() { return medicaments; }
        public String getInstructions() { return instructions; }
        public String getValidite() { return validite; }
        public String getRenouvellement() { return renouvellement; }
        public Date getDateSignature() { return dateSignature; }
        public String getStatut() { return statut; }
        public String getNomMedecin() { return nomMedecin; }
        public String getSpecialiteMedecin() { return specialiteMedecin; }
    }

   
    @PostMapping
    public ResponseEntity<?> ajouterOrdonnance(@RequestBody Ordonnance ordonnance) {
        try {
            Ordonnance saved = ordonnanceService.ajouterOrdonnance(ordonnance);
            saved.setPatient(null);
            saved.setMedecin(null);
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrdonnanceDTO(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création : " + e.getMessage());
        }
    }

    
    @GetMapping
    public ResponseEntity<List<Ordonnance>> afficherOrdonnances() {
        return ResponseEntity.ok(ordonnanceService.affichierOrdonnance());
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ordonnanceService.affichierOrdonnanceparid(id)
                .<ResponseEntity<?>>map(ord -> ResponseEntity.ok(new OrdonnanceDTO(ord)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ordonnance introuvable pour l'id : " + id));
    }

  
    @GetMapping("/reference/{reference}")
    public ResponseEntity<?> getByReference(@PathVariable String reference) {
        return ordonnanceRepository.findByReference(reference)
                .<ResponseEntity<?>>map(ord -> ResponseEntity.ok(new OrdonnanceDTO(ord)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Ordonnance introuvable : " + reference));
    }


  
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<OrdonnanceDTO>> getByMedecin(@PathVariable Long medecinId) {
        List<Ordonnance> ordonnances = ordonnanceRepository.findByMedecinId(medecinId);
        List<OrdonnanceDTO> dtos = ordonnances.stream()
                .map(OrdonnanceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

   
    @GetMapping("/pharmacie/{pharmacieId}")
    public ResponseEntity<List<OrdonnanceDTO>> getByPharmacie(@PathVariable Long pharmacieId) {
        List<Ordonnance> ordonnances = ordonnanceRepository.findByPharmacieId(pharmacieId);
        if (ordonnances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<OrdonnanceDTO> dtos = ordonnances.stream()
                .map(OrdonnanceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

   
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<OrdonnanceDTO>> getByPatient(@PathVariable Long patientId) {
        List<Ordonnance> ordonnances = ordonnanceRepository.findByPatient_IdPatient(patientId);
        if (ordonnances.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<OrdonnanceDTO> dtos = ordonnances.stream()
                .map(OrdonnanceDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

   
    @PatchMapping("/{id}/pharmacie")
    public ResponseEntity<?> updatePharmacie(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        Ordonnance ord = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ordonnance introuvable : " + id));

        if (body.containsKey("statut")) {
            ord.setStatut(body.get("statut"));
        }
        if (body.containsKey("traitementPharmacie")) {
            ord.setInstructions(body.get("traitementPharmacie"));
        }

        Ordonnance saved = ordonnanceRepository.save(ord);
        return ResponseEntity.ok(new OrdonnanceDTO(saved));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrdonnance(
            @PathVariable Long id,
            @RequestBody Ordonnance ordonnanceDetails) {

        Ordonnance existing = ordonnanceRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ordonnance introuvable pour l'id : " + id);
        }

        // Mise à jour uniquement des champs "plats" – ne pas toucher à medecin, patient, reference
        existing.setNomPatient(ordonnanceDetails.getNomPatient());
        existing.setDateNaissance(ordonnanceDetails.getDateNaissance());
        existing.setSexe(ordonnanceDetails.getSexe());
        existing.setTelephonePatient(ordonnanceDetails.getTelephonePatient());
        existing.setEmailPatient(ordonnanceDetails.getEmailPatient());
        existing.setDateConsultation(ordonnanceDetails.getDateConsultation());
        existing.setDiagnostic(ordonnanceDetails.getDiagnostic());
        existing.setAllergies(ordonnanceDetails.getAllergies());
        existing.setMedicaments(ordonnanceDetails.getMedicaments());
        existing.setInstructions(ordonnanceDetails.getInstructions());
        existing.setValidite(ordonnanceDetails.getValidite());
        existing.setRenouvellement(ordonnanceDetails.getRenouvellement());
        existing.setStatut(ordonnanceDetails.getStatut());
        existing.setDateSignature(ordonnanceDetails.getDateSignature());

        Ordonnance saved = ordonnanceRepository.save(existing);
        return ResponseEntity.ok(new OrdonnanceDTO(saved));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!ordonnanceRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ordonnance introuvable pour l'id : " + id);
        }
        ordonnanceRepository.deleteById(id);
        return ResponseEntity.ok("Ordonnance supprimée avec succès");
    }

    
    @PostMapping(value = "/email", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> envoyerParEmail(@RequestBody EmailRequest request) {
        try {
            ordonnanceService.envoyerParEmail(request);
            return ResponseEntity.ok("Email envoyé avec succès à " + request.getEmail());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur envoi email : " + e.getMessage());
        }
    }

   
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        try {
            byte[] pdf = ordonnanceService.generatePdf(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=ordonnance.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

  
    @PostMapping("/{id}/envoyer-pharmacie")
    public ResponseEntity<?> envoyerOrdonnancePharmacie(
            @PathVariable Long id,
            @RequestParam Long pharmacieId,
            @RequestParam Long patientId) {

        Ordonnance ordonnance = ordonnanceService.affichierOrdonnanceparid(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ordonnance non trouvée"));

        ordonnance.setPharmacieId(pharmacieId);
        ordonnance.setStatut("en cours");
        ordonnanceRepository.save(ordonnance);
        return ResponseEntity.ok("Ordonnance envoyée avec succès à la pharmacie " + pharmacieId);
    }
}

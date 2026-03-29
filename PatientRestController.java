package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.Patient;
import com.projet.DocCarePortal.Repository.PatientRepository;
import com.projet.DocCarePortal.Services.PatientService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.*;

@RestController
@RequestMapping("/patient")
@CrossOrigin("*")
public class PatientRestController {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private final Key jwtKey = Keys.hmacShaKeyFor(
            "doccareportal_secret_key_minimum_256bits_ok".getBytes()
    );

    @Autowired
    PatientRepository patientRepository;

    @Autowired
    PatientService patientService;

    
    @PostMapping
    public ResponseEntity<?> ajouterPatient(@RequestBody Patient patient) {
        return patientService.ajouterPatient(patient);
    }

    @GetMapping
    public List<Patient> afficherPatients() {
        return patientService.afficherPatients();
    }

    @GetMapping("/{id}")
    public Optional<Patient> getPatientById(@PathVariable("id") Long id) {
        return patientRepository.findById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginPatient(@RequestBody Patient patient) {
        HashMap<String, Object> response = new HashMap<>();

        
        Patient userFromDB = patientRepository.findPatientByEmail(patient.getEmail());

        if (userFromDB == null) {
            response.put("message", "Patient non trouvé !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        boolean compare = this.bCryptPasswordEncoder.matches(
                patient.getMdp(),
                userFromDB.getMdp()
        );

        if (!compare) {
            response.put("message", "Mot de passe incorrect !");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        if (!"ACTIF".equalsIgnoreCase(userFromDB.getStatus())) {
            response.put("message", "Compte inactif, contactez l'administrateur");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        String token = Jwts.builder()
                .claim("data", userFromDB)
                .signWith(jwtKey)
                .compact();

        response.put("token", token);
        response.put("id", userFromDB.getIdPatient());
        response.put("nom", userFromDB.getNom());
        response.put("prenom", userFromDB.getPrenom());
        response.put("email", userFromDB.getEmail());
        response.put("adresseP", userFromDB.getAdresseP());
        response.put("numeroTel", userFromDB.getNumeroTel());
        response.put("qrcode", userFromDB.getQrcode());
        response.put("status", userFromDB.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> modifierPatient(@PathVariable("id") Long id,
                                                   @RequestBody Patient patient) {
        if (patient.getMdp() != null && !patient.getMdp().isEmpty()) {
            patient.setMdp(this.bCryptPasswordEncoder.encode(patient.getMdp()));
        }
        Patient updatedPatient = patientService.modifierPatient(id, patient);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerPatient(@PathVariable("id") Long id) {
        try {
            patientService.supprimerPatient(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace(); // voir dans les logs backend
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur: " + e.getMessage());
        }
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String token) {
        return patientService.confirmationemail(token);
    }
}

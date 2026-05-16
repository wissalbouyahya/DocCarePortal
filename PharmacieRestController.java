package com.projet.DocCarePortal.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.DocCarePortal.Entity.Pharmacie;
import com.projet.DocCarePortal.Repository.PharmacieRepository;
import com.projet.DocCarePortal.Services.PharmacieService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Key;
import java.util.*;

@RestController
@RequestMapping(value = "/pharmacie")
@CrossOrigin("*")
public class PharmacieRestController {
    @Autowired
    PharmacieRepository pharmacieRepository;

    @Autowired
    PharmacieService pharmacieService;

    @PostMapping
    public ResponseEntity<?> ajouterPharmacie(@RequestBody Pharmacie pharmacie) {
        HashMap<String, Object> response = new HashMap<>();

        try {

            if (pharmacie.getEmail() != null && !pharmacie.getEmail().isEmpty()) {
                if (pharmacieRepository.existsByEmail(pharmacie.getEmail())) {
                    response.put("message", "Email déjà utilisé !");
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }


            if (pharmacie.getStatus() == null) {
                pharmacie.setStatus("ACTIF");
            }


            if (pharmacie.getServicelivraison() == null) {
                pharmacie.setServicelivraison("NON");
            }

            Pharmacie savedPharmacie = pharmacieRepository.save(pharmacie);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPharmacie);
        } catch (Exception e) {
            response.put("message", "Erreur lors de l'ajout de la pharmacie");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public List<Pharmacie> afficherPharmacies() {
        return pharmacieService.afficherPharmacies();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerPharmacie(@PathVariable("id") Long id) {
        HashMap<String, Object> response = new HashMap<>();

        try {
            pharmacieService.supprimerPharmacie(id);
            response.put("message", "Pharmacie supprimée avec succès");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            response.put("message", "Erreur lors de la suppression");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPharmacieById(@PathVariable("id") Long id) {
        Optional<Pharmacie> pharmacie = pharmacieService.afficherPharmacieParId(id);

        if (pharmacie.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(pharmacie.get());
        } else {
            HashMap<String, Object> response = new HashMap<>();
            response.put("message", "Pharmacie non trouvée");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifierPharmacie(@PathVariable("id") Long id, @RequestBody Pharmacie pharmacie) {
        HashMap<String, Object> response = new HashMap<>();

        if (!pharmacieRepository.existsById(id)) {
            response.put("message", "Pharmacie non trouvée");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        pharmacie.setIdPharmacie(id);
        Pharmacie updatedPharmacie = pharmacieService.modifierPharmacie(pharmacie);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPharmacie);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginPharmacie(@RequestBody Pharmacie pharmacie) {
        HashMap<String, Object> response = new HashMap<>();

        Pharmacie pharmacieFromDB = pharmacieRepository.findByEmail(pharmacie.getEmail());

        if (pharmacieFromDB == null) {
            response.put("message", "Email non trouvé !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            response.put("message", "Login réussi");
            response.put("id", pharmacieFromDB.getIdPharmacie());
            response.put("nom", pharmacieFromDB.getNom());
            response.put("email", pharmacieFromDB.getEmail());
            response.put("adressePh", pharmacieFromDB.getAdresse());
            response.put("numeroTel", pharmacieFromDB.getNumeroTel());
            response.put("serviceLivraison", pharmacieFromDB.getServicelivraison());
            response.put("status", pharmacieFromDB.getStatus());

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    }

    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @PostMapping("/login-google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> body) {

        String idToken = body.get("id_token");
        System.out.println("TOKEN = " + idToken);

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            String response = restTemplate.getForObject(url, String.class);
            System.out.println("GOOGLE RESPONSE = " + response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode userInfo = mapper.readTree(response);

            String email = userInfo.get("email").asText();

            Map<String, Object> res = new HashMap<>();
            res.put("email", email);

            return ResponseEntity.ok(res);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erreur Google");
        }
    }
    private String validateGoogleToken(String idToken) {
        String url = GOOGLE_TOKEN_URL + idToken;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    private String generateToken(Pharmacie pharmacie) {
        return Jwts.builder()
                .claim("data", pharmacie)
                .signWith(SignatureAlgorithm.HS256, "SECRET_KEY")
                .compact();
    }
}

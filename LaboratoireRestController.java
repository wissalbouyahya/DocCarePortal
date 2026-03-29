package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.Laboratoire;
import com.projet.DocCarePortal.Repository.LaboratoireRepository;
import com.projet.DocCarePortal.Services.LaboratoireService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.*;

@RestController
@RequestMapping("/laboratoire")   
@CrossOrigin(origins = "http://localhost:4200")
public class LaboratoireRestController {

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private final Key jwtKey = Keys.hmacShaKeyFor(
            "doccareportal_secret_key_minimum_256bits_ok".getBytes()
    );

    @Autowired
    private LaboratoireRepository laboratoireRepository;

    @Autowired
    private LaboratoireService laboratoireService;

    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Laboratoire loginRequest) {
        try {
            Optional<Laboratoire> laboOpt = laboratoireRepository.findByEmail(loginRequest.getEmail());

            if (laboOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Email introuvable"));
            }

            Laboratoire labo = laboOpt.get();

            if (!bCryptPasswordEncoder.matches(loginRequest.getMdp(), labo.getMdp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Mot de passe incorrect"));
            }

           
            String token = Jwts.builder()
                    .setSubject(labo.getEmail())
                    .claim("idLaboratoire", labo.getIdLaboratoire())
                    .claim("nom",           labo.getNom())
                    .claim("email",         labo.getEmail())
                    .claim("userType",      "laboratoire")
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86_400_000)) // 24h
                    .signWith(jwtKey)
                    .compact();

            
            Map<String, Object> response = new HashMap<>();
            response.put("token",         token);
            response.put("idLaboratoire", labo.getIdLaboratoire());
            response.put("nom",           labo.getNom());
            response.put("email",         labo.getEmail());
            response.put("numeroTel",     labo.getNumeroTel());
            response.put("adresse",       labo.getAdresse());
            response.put("type",          labo.getType());
            response.put("status",        labo.getStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur serveur : " + e.getMessage()));
        }
    }

    
    @PostMapping
    public ResponseEntity<?> ajouterLaboratoire(@RequestBody Laboratoire labo) {
        try {
            if (laboratoireRepository.existsByEmail(labo.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Email existe déjà !"));
            }

            if (labo.getMdp() != null && !labo.getMdp().isEmpty()) {
                labo.setMdp(bCryptPasswordEncoder.encode(labo.getMdp()));
            }

            if (labo.getStatus() == null) {
                labo.setStatus("ACTIF");
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(laboratoireRepository.save(labo));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }

   
    @GetMapping
    public List<Laboratoire> afficherLaboratoires() {
        return laboratoireService.afficherLaboratoires();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getLaboratoireById(@PathVariable Long id) {
        Optional<Laboratoire> labo = laboratoireService.afficherLaboratoireParId(id);
        if (labo.isPresent()) {
            return ResponseEntity.ok(labo.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Laboratoire non trouvé !"));
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> modifierLaboratoire(@PathVariable Long id,
                                                 @RequestBody Laboratoire labo) {
        if (labo.getMdp() != null && !labo.getMdp().isEmpty()) {
            labo.setMdp(bCryptPasswordEncoder.encode(labo.getMdp()));
        }
        labo.setIdLaboratoire(id);
        return ResponseEntity.ok(laboratoireService.modifierLaboratoire(labo));
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerLaboratoire(@PathVariable Long id) {
        laboratoireService.supprimerLaboratoire(id);
        return ResponseEntity.ok(Map.of("message", "Laboratoire supprimé avec succès"));
    }
}

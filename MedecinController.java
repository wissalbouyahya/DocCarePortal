package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.Medecin;
import com.projet.DocCarePortal.Repository.MedecinRepository;
import com.projet.DocCarePortal.Service.EmailService;
import com.projet.DocCarePortal.Service.MedecinService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping(value = "/medecin")
@RestController
@CrossOrigin("*")
public class MedecinController {

    
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    MedecinRepository medecinRepository;

    @Autowired
    MedecinService medecinService;

    @Autowired
    EmailService emailService;

    @Autowired
    private JavaMailSender emailServices;

    @PostMapping
    public ResponseEntity<?> ajouterMedecin(@RequestBody Medecin medecin) {
        HashMap<String, Object> response = new HashMap<>();

        if (medecinRepository.existsByEmail(medecin.getEmail())) {
            response.put("message", "Email existe déjà !");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            medecin.setMdp(this.bCryptPasswordEncoder.encode(medecin.getMdp()));

            if (medecin.getStatus() == null) {
                medecin.setStatus("ACTIF");
            }

            Medecin savedMedecin = medecinRepository.save(medecin);
            String subject = "Bienvenue - Vérification de votre compte";
            String text = "Votre compte Medecin a été créé avec succès!\n\n"
                    + "Email: " + savedMedecin.getEmail() + "\n"
                    + "Nom: " + savedMedecin.getNom() + "\n\n"
                    + "Veuillez attendre la validation de votre compte par l'administrateur.";
            emailService.SendSimpleMessage(savedMedecin.getEmail(), subject, text);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedMedecin);
        }
    }

    @GetMapping
    public List<Medecin> afficherMedecins() {
        return medecinService.affichiermedecin();
    }

    @DeleteMapping("/{id}")
    public void supprimerMedecin(@PathVariable("id") Long id) {
        medecinService.supprimermedecin(id);
    }

    @GetMapping("/{id}")
    public Optional<Medecin> getMedecinById(@PathVariable("id") Long id) {
        return medecinService.affichiermedecinparid(id);
    }

    @PutMapping("/{id}")
    public Medecin modifierMedecin(@PathVariable("id") Long id, @RequestBody Medecin medecin) {
        medecin.setIdMedecin(id);
        return medecinService.modifiermedecin(medecin);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginMedecin(@RequestBody Medecin medecin) {
        HashMap<String, Object> response = new HashMap<>();

        Medecin userFromDB = medecinRepository.findByEmail(medecin.getEmail());
        if (userFromDB == null) {
            response.put("message", "Médecin non trouvé !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } else {
            boolean compare = this.bCryptPasswordEncoder.matches(medecin.getMdp(), userFromDB.getMdp());
            if (!compare) {
                response.put("message", "Mot de passe incorrect !");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            } else {
               
                String token = Jwts.builder()
                        .claim("data", userFromDB)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                        .signWith(SECRET_KEY)
                        .compact();

                response.put("token", token);
                response.put("id", userFromDB.getIdMedecin());
                response.put("nom", userFromDB.getNom());
                response.put("prenom", userFromDB.getPrenom());
                response.put("email", userFromDB.getEmail());
                response.put("specialite", userFromDB.getSpecialite());
                response.put("adresseM", userFromDB.getAdresseM());
                response.put("numeroTel", userFromDB.getNumeroTel());
                response.put("status", userFromDB.getStatus());

                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        }
    }

    @PutMapping(value = "/updateetat/{id}")
    public ResponseEntity<Map<String, Object>> modifieretatMedecin(@RequestBody Medecin medecin, @PathVariable("id") Long id) {
        HashMap<String, Object> response = new HashMap<>();

        if (medecinRepository.findById(id).isPresent()) {
            Medecin existingMedecin = medecinRepository.findById(id).get();
            String oldStatus = existingMedecin.getStatus();

            existingMedecin.setIdMedecin(medecin.getIdMedecin());
            existingMedecin.setNom(medecin.getNom());
            existingMedecin.setPrenom(medecin.getPrenom());
            existingMedecin.setEmail(medecin.getEmail());
            existingMedecin.setMdp(medecin.getMdp());
            existingMedecin.setNumeroTel(medecin.getNumeroTel());
            existingMedecin.setAdresseM(medecin.getAdresseM());
            existingMedecin.setSpecialite(medecin.getSpecialite());
            existingMedecin.setStatus(medecin.getStatus());

            if (oldStatus != null && !oldStatus.equals(medecin.getStatus())) {
                String etat = medecin.getStatus().equals("BLOQUE")
                        ? "<strong><span style=\"color: red;\">Bloqué</span></strong>"
                        : "<strong><span style=\"color: green;\">Accepté</span></strong>";

                String messageHTML =
                        "<!DOCTYPE html>" +
                                "<html><head><style>" +
                                ".card { background-color: #f9f9f9; border-radius: 10px; padding: 20px; margin: 20px auto; width: 400px; box-shadow: 0px 4px 8px rgba(0,0,0,0.1); }" +
                                ".logo { text-align: center; margin-bottom: 20px; }" +
                                ".logo img { max-width: 200px; }" +
                                ".button { display: block; width: 200px; margin: 0 auto; padding: 10px 20px; background-color: #b615ae; color: white; text-decoration: none; text-align: center; border-radius: 5px; font-size: 16px; }" +
                                "</style></head><body>" +
                                "<div class=\"card\">" +
                                "<div class=\"logo\"><img src=\"cid:logoImage\" alt=\"DocCare Logo\"></div>" +
                                "<p>Salut <strong>" + existingMedecin.getNom() + " " + existingMedecin.getPrenom() + "</strong>,</p>" +
                                "<h2>État de votre compte</h2>" +
                                "<h4>Votre compte a été " + etat + "</h4>";

                if (medecin.getStatus().equals("ACTIF")) {
                    messageHTML += "<p>Cliquez ci-dessous pour revenir à la page de connexion :</p>" +
                            "<a href=\"http://localhost:4200/login\"><button class=\"button\">Connexion</button></a>";
                }

                messageHTML += "</div></body></html>";

                try {
                    MimeMessage message = emailServices.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true);
                    helper.setTo(existingMedecin.getEmail());
                    helper.setSubject("Mise à jour de votre compte DocCare");
                    helper.setText(messageHTML, true);
                    helper.addInline("logoImage", new ClassPathResource("static/images/Logoo.png"));
                    emailServices.send(message);
                } catch (MessagingException e) {
                    System.err.println("Erreur envoi email: " + e.getMessage());
                }
            }

            Medecin savedMedecin = medecinRepository.save(existingMedecin);

           
            String token = Jwts.builder()
                    .claim("data", savedMedecin)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                    .signWith(SECRET_KEY)
                    .compact();

            response.put("medecin", savedMedecin);
            response.put("token", token);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } else {
            response.put("message", "Médecin non trouvé !");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}

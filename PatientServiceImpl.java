package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.ConfirmationToken;
import com.projet.DocCarePortal.Entity.Patient;
import com.projet.DocCarePortal.Repository.ConfirmationTokenRepository;
import com.projet.DocCarePortal.Repository.PatientRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private EmailPatientService emailPatientService;


  
    @Override
    public ResponseEntity<Object> ajouterPatient(Patient patient) {

        
        Patient existingUser = patientRepository.findPatientByEmail(patient.getEmail());
        if (existingUser != null) {
            
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error: Email is already in use!");
            return ResponseEntity.badRequest().body(error);
        }

       
        patient.setMdp(passwordEncoder.encode(patient.getMdp()));

       
        patientRepository.save(patient);

       
        ConfirmationToken confirmationToken = new ConfirmationToken(patient);
        confirmationTokenRepository.save(confirmationToken);

       
        String confirmationLink = "http://localhost:8081/api/patient/confirm-account?token="
                + confirmationToken.getConfirmationToken();

        
        String htmlEmail = buildConfirmationEmailHtml(confirmationLink);

       
        MimeMessage message = emailPatientService.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(patient.getEmail());
            helper.setSubject("Complétez votre inscription !");
            helper.setText(htmlEmail, true);

            
            ClassPathResource logo = new ClassPathResource("static/images/logo.png");
            if (logo.exists()) {
                helper.addInline("logoImage", logo);
            }

        } catch (MessagingException e) {
            e.printStackTrace();
            
            Map<String, String> error = new HashMap<>();
            error.put("message", "Échec de l'envoi de l'email de confirmation.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        emailPatientService.SendEmail(message);

        System.out.println("Confirmation Token: " + confirmationToken.getConfirmationToken());

       
        Map<String, String> response = new HashMap<>();
        response.put("message", "Patient ajouté avec succès ! Un email de confirmation a été envoyé.");
        response.put("email", patient.getEmail());

        return ResponseEntity.ok().body(response);
    }


    
    private String buildConfirmationEmailHtml(String confirmationLink) {
        return "<!DOCTYPE html>" +
                "<html lang='fr'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>Confirmation Email</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin:0; padding:0; display:flex;" +
                "       justify-content:center; align-items:center; height:100vh;" +
                "       background: linear-gradient(135deg,#b615ae,#1f5bc4); }" +
                ".container { background:#fff; padding:40px; border-radius:10px;" +
                "             text-align:center; box-shadow:0 5px 15px rgba(0,0,0,0.2); max-width:450px; }" +
                "h2 { color:#333; }" +
                "p  { color:#555; }" +
                ".button { display:inline-block; margin-top:20px; padding:12px 25px;" +
                "          background:#b615ae; color:white; text-decoration:none;" +
                "          border-radius:6px; font-size:16px; transition:0.3s; }" +
                ".button:hover { background:#8f108a; }" +
                ".icon { font-size:60px; color:#b615ae; margin-bottom:15px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='icon'>✉</div>" +
                "<h2>Confirmez votre inscription</h2>" +
                "<p>Merci de vous être inscrit sur DocCarePortal.</p>" +
                "<p>Cliquez sur le bouton ci-dessous pour activer votre compte.</p>" +
                "<a href='" + confirmationLink + "' class='button'>Confirmer mon email</a>" +
                "</div>" +
                "</body>" +
                "</html>";
    }


    
    @Override
    public Patient modifierPatient(Long id, Patient patient) {
        Optional<Patient> existingPatientOpt = patientRepository.findById(id);

        if (existingPatientOpt.isPresent()) {
            Patient existingPatient = existingPatientOpt.get();

            existingPatient.setNom(patient.getNom());
            existingPatient.setPrenom(patient.getPrenom());
            existingPatient.setEmail(patient.getEmail());
            existingPatient.setAdresseP(patient.getAdresseP());
            existingPatient.setNumeroTel(patient.getNumeroTel());
            existingPatient.setDateNaissance(patient.getDateNaissance());
            existingPatient.setQrcode(patient.getQrcode());
            existingPatient.setStatus(patient.getStatus());
            existingPatient.setEtat(patient.getEtat());

            
            if (patient.getMdp() != null && !patient.getMdp().isEmpty()) {
                existingPatient.setMdp(passwordEncoder.encode(patient.getMdp()));
            }

            return patientRepository.save(existingPatient);
        }

        return null; 
    }


   
    @Override
    public List<Patient> afficherPatients() {
        return patientRepository.findAll();
    }
    
    @Override
    public Optional<Patient> afficherPatientParId(Long id) {
        return patientRepository.findById(id);
    }


    @Override
    public void supprimerPatient(Long id) {
        Optional<Patient> patientOpt = patientRepository.findById(id);

        if (patientOpt.isPresent()) {
            Patient patient = patientOpt.get();

            
            ConfirmationToken token = confirmationTokenRepository.findByPatient(patient);
            if (token != null) {
                confirmationTokenRepository.delete(token);
            }

            
            patientRepository.deleteById(id);
        }
        
    }


    @Override
    public ResponseEntity<?> confirmationemail(String confirmationemail) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationemail);

        if (token == null) {
            
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur : token invalide ou expiré.");
            return ResponseEntity.badRequest().body(error);
        }

        Patient patient = patientRepository.findPatientByEmail(token.getPatient().getEmail());

        if (patient == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erreur : patient introuvable.");
            return ResponseEntity.badRequest().body(error);
        }

        
        patient.setEtat(true);
        patientRepository.save(patient);

        
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/loginpatient")
                .build();
    }
}

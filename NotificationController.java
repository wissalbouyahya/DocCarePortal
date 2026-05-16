package com.projet.DocCarePortal.RestController;

import com.projet.DocCarePortal.Entity.Notification;
import com.projet.DocCarePortal.Services.NotificationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/patient/{patientId}")
    public List<Notification> getByPatient(@PathVariable Long patientId) {
        return notificationService.findByPatientIdOrderByDateDesc(patientId);
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Notification> getByMedecin(@PathVariable Long medecinId) {
        return notificationService.findByMedecinIdOrderByDateDesc(medecinId);
    }

    public void marquerLuePatient(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

   
    @PutMapping("/medecin/{id}/read")
    public void marquerLueMedecin(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }


    @PostMapping
    public Notification creer(@RequestBody Notification notif) {
        return notificationService.save(notif);
    }
}

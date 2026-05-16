package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Notification;
import com.projet.DocCarePortal.Repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

   
    public List<Notification> findByPatientIdOrderByDateDesc(Long patientId) {
        return notificationRepository
                .findByPatient_IdPatientAndDestinataireOrderByDateCreationDesc(
                        patientId, "PATIENT");
    }

    
    public List<Notification> findByMedecinIdOrderByDateDesc(Long medecinId) {
        return notificationRepository
                .findByMedecin_IdMedecinAndDestinataireOrderByDateCreationDesc(
                        medecinId, "MEDECIN");
    }

    public void markAsRead(Long id) {
        Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notif.setLu(true);
        notificationRepository.save(notif);
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public long getUnreadCount(Long patientId) {
        return notificationRepository
                .findByPatient_IdPatientAndDestinataireOrderByDateCreationDesc(patientId, "PATIENT")
                .stream()
                .filter(n -> !n.isLu())
                .count();
    }
}

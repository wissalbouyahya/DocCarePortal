package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByPatient_IdPatientAndDestinataireOrderByDateCreationDesc(
            Long idPatient, String destinataire);

    List<Notification> findByMedecin_IdMedecinAndDestinataireOrderByDateCreationDesc(
            Long idMedecin, String destinataire);

    
    List<Notification> findByPatient_IdPatientOrderByDateCreationDesc(Long idPatient);
    List<Notification> findByMedecin_IdMedecinOrderByDateCreationDesc(Long idMedecin);
}

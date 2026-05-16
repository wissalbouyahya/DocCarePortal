package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Medecin;
import com.projet.DocCarePortal.Entity.Patient;
import com.projet.DocCarePortal.Entity.Rendezvous;
import com.projet.DocCarePortal.Entity.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RendezvousRepository extends JpaRepository<Rendezvous, Long> {

    
    @Query("SELECT COUNT(r) > 0 FROM Rendezvous r " +
            "WHERE r.medecin = :medecin " +
            "AND r.dateHeure = :dateHeure " +
            "AND r.estReserve = true")
    boolean existsByMedecinAndDateHeureAndEstReserveTrue(
            @Param("medecin") Medecin medecin,
            @Param("dateHeure") LocalDateTime dateHeure);

    
    @Query("SELECT COUNT(r) > 0 FROM Rendezvous r " +
            "WHERE r.medecin.idMedecin = :medecinId " +
            "AND r.dateHeure = :dateHeure")
    boolean existsByMedecinIdAndDateHeure(
            @Param("medecinId") Long medecinId,
            @Param("dateHeure") LocalDateTime dateHeure);

   
    Optional<Rendezvous> findByMedecinAndDateHeure(Medecin medecin, LocalDateTime dateHeure);

    
    @Query("SELECT r FROM Rendezvous r " +
            "WHERE r.medecin.idMedecin = :medecinId " +
            "AND r.dateHeure BETWEEN :debut AND :fin")
    List<Rendezvous> findByMedecinIdAndDateHeureBetween(
            @Param("medecinId") Long medecinId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

   
    @Query("SELECT r FROM Rendezvous r WHERE r.medecin.idMedecin = :medecinId")
    List<Rendezvous> findByMedecinId(@Param("medecinId") Long medecinId);

    
    @Query("SELECT r FROM Rendezvous r WHERE r.patient.idPatient = :patientId")
    List<Rendezvous> findByPatientId(@Param("patientId") Long patientId);

    
    @Query("SELECT r FROM Rendezvous r " +
            "WHERE r.medecin.idMedecin = :medecinId " +
            "AND r.statutRendezVous = :statut")
    List<Rendezvous> findByMedecinIdAndStatutRendezVous(
            @Param("medecinId") Long medecinId,
            @Param("statut") StatutRendezVous statut);

    
    @Query("SELECT r FROM Rendezvous r " +
            "WHERE r.medecin.idMedecin = :medecinId " +
            "AND CAST(r.statutRendezVous AS string) = :statut")
    List<Rendezvous> findByMedecinIdAndStatut(
            @Param("medecinId") Long medecinId,
            @Param("statut") String statut);

    
    @Query("SELECT DISTINCT r.patient FROM Rendezvous r " +
            "WHERE r.medecin.idMedecin = :medecinId")
    List<Patient> findDistinctPatientsByMedecinId(@Param("medecinId") Long medecinId);

   
    @Query("SELECT DISTINCT p.idPatient, p.nom, p.prenom, p.email, p.numeroTel " +
            "FROM Rendezvous r JOIN r.patient p WHERE r.medecin.idMedecin = :medecinId")
    List<Object[]> findPatientsByMedecinId(@Param("medecinId") Long medecinId);

   
    @Query(value = """
        SELECT DISTINCT 
            m.id_medecin, 
            m.nom, 
            m.prenom, 
            m.email, 
            m.numero_tel, 
            m.specialite
        FROM medecin m
        INNER JOIN rendezvous r ON r.medecin_id = m.id_medecin
        WHERE r.patient_id = :patientId
          AND r.statut_rendez_vous IN ('CONFIRME', 'EN_COURS', 'TERMINE', 'VALIDE')
        ORDER BY m.nom, m.prenom
    """, nativeQuery = true)
    List<Object[]> findMedecinsByPatientId(@Param("patientId") Long patientId);

    
    @Query(value = """
        SELECT 
            r.id,
            r.date_heure,
            r.statut_rendez_vous,
            r.motif,
            m.id_medecin,
            m.nom,
            m.prenom,
            m.specialite
        FROM rendezvous r
        INNER JOIN medecin m ON m.id_medecin = r.medecin_id
        WHERE r.patient_id = :patientId
          AND r.statut_rendez_vous IN ('CONFIRME', 'EN_COURS', 'TERMINE', 'VALIDE')
        ORDER BY r.date_heure DESC
    """, nativeQuery = true)
    List<Object[]> findRendezvousWithMedecinByPatientId(@Param("patientId") Long patientId);
}

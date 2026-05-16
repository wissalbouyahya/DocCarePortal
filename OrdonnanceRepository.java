package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {

    Optional<Ordonnance> findByReference(String reference);

    @Query("SELECT o FROM Ordonnance o WHERE o.medecin.idMedecin = :medecinId")
    List<Ordonnance> findByMedecinId(@Param("medecinId") Long medecinId);

  
    @Query("SELECT o FROM Ordonnance o " +
            "LEFT JOIN FETCH o.medecin " +
            "LEFT JOIN FETCH o.patient " +
            "WHERE o.pharmacieId = :pharmacieId")
    List<Ordonnance> findByPharmacieId(@Param("pharmacieId") Long pharmacieId);

    List<Ordonnance> findByPatient_IdPatient(Long patientId);
}

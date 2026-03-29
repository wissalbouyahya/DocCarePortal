package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    
    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByNomAndPrenom(String nom, String prenom);

    Optional<Patient> findByNumeroTel(String numeroTel);

    List<Patient> findByStatus(String status);

    List<Patient> findByEtat(Boolean etat);

    Patient findPatientByEmail(String email);
}

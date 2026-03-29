package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Patient;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface PatientService {

    List<Patient> afficherPatients();
    ResponseEntity<Object> ajouterPatient(Patient patient);
    Optional<Patient> afficherPatientParId(Long id);
    Patient modifierPatient(Long id, Patient patient);
    void supprimerPatient(Long id);

    ResponseEntity<?> confirmationemail(String confirmationemail);
}

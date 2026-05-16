package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Pharmacie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {


    Pharmacie findByEmail(String email);

    boolean existsByEmail(String email);
}

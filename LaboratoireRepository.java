package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Laboratoire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {


    Optional<Laboratoire> findByEmail(String email);
    boolean existsByEmail(String email);
}

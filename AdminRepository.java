package com.projet.DocCarePortal.Repository;

import com.projet.DocCarePortal.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByEmail(String email);
    Admin findAdminByEmail(String email);
}

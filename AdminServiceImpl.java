package com.projet.DocCarePortal.Service;

import com.projet.DocCarePortal.Entity.Admin;
import com.projet.DocCarePortal.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public Admin ajouteradmin(Admin admin) {
        admin.setMdp(passwordEncoder.encode(admin.getMdp()));
        return adminRepository.save(admin);
    }

    @Override
    public Admin modifieradmin(Admin admin) {
        if (admin.getMdp() != null && !admin.getMdp().isEmpty()) {
            admin.setMdp(passwordEncoder.encode(admin.getMdp()));
        }
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> affichieradmin() {
        return adminRepository.findAll();
    }

    @Override
    public void supprimeradmin(Long id) {
        adminRepository.deleteById(id);
    }

    @Override
    public Optional<Admin> affichieradminparid(Long id) {
        return adminRepository.findById(id);
    }
}


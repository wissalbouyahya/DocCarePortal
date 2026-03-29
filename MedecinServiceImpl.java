package com.projet.DocCarePortal.Service;

import com.projet.DocCarePortal.Entity.Medecin;
import com.projet.DocCarePortal.Repository.MedecinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class MedecinServiceImpl implements MedecinService {

    @Autowired
    private MedecinRepository medecinRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Medecin ajoutermedecin(Medecin medecin) {
        medecin.setMdp(passwordEncoder.encode(medecin.getMdp()));
        return medecinRepository.save(medecin);
    }

    @Override
    public Medecin modifiermedecin(Medecin medecin) {
        if (medecin.getMdp() != null && !medecin.getMdp().isEmpty()) {
            medecin.setMdp(passwordEncoder.encode(medecin.getMdp()));
        }
        return medecinRepository.save(medecin);
    }

    @Override
    public List<Medecin> affichiermedecin() {
        return medecinRepository.findAll();
    }

    @Override
    public void supprimermedecin(Long idMedecin) {
        medecinRepository.deleteById(idMedecin);
    }

    @Override
    public Optional<Medecin> affichiermedecinparid(Long idMedecin) {
        return medecinRepository.findById(idMedecin);
    }
}

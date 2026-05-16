package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Pharmacie;
import com.projet.DocCarePortal.Repository.PharmacieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PharmacieServiceImpl implements PharmacieService {

    @Autowired
    private PharmacieRepository pharmacieRepository;

    @Override
    public Pharmacie ajouterPharmacie(Pharmacie pharmacie) {
        return pharmacieRepository.save(pharmacie);
    }

    @Override
    public Pharmacie modifierPharmacie(Pharmacie pharmacie) {
        Optional<Pharmacie> existingOpt = pharmacieRepository.findById(pharmacie.getIdPharmacie());
        if (existingOpt.isPresent()) {
            Pharmacie existing = existingOpt.get();
            existing.setNom(pharmacie.getNom());
            existing.setAdresse(pharmacie.getAdresse());
            existing.setNumeroTel(pharmacie.getNumeroTel());
            existing.setEmail(pharmacie.getEmail());
            existing.setServicelivraison(pharmacie.getServicelivraison());
            existing.setStatus(pharmacie.getStatus());


            if (pharmacie.getMdp() != null && !pharmacie.getMdp().isEmpty()) {
                existing.setMdp(pharmacie.getMdp());
            }

            return pharmacieRepository.save(existing);
        }
        return null;
    }

    @Override
    public List<Pharmacie> afficherPharmacies() {
        return pharmacieRepository.findAll();
    }

    @Override
    public Optional<Pharmacie> afficherPharmacieParId(Long id) {
        return pharmacieRepository.findById(id);
    }

    @Override
    public void supprimerPharmacie(Long id) {
        pharmacieRepository.deleteById(id);
    }
}

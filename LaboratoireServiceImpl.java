package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.Laboratoire;
import com.projet.DocCarePortal.Repository.LaboratoireRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratoireServiceImpl implements LaboratoireService {

    @Autowired
    private LaboratoireRepository laboratoireRepository;

    @Override
    public Laboratoire ajouterLaboratoire(Laboratoire labo) {
        return laboratoireRepository.save(labo);
    }

    @Override
    public Laboratoire modifierLaboratoire(Laboratoire labo) {
        Optional<Laboratoire> existingLaboOpt = laboratoireRepository.findById(labo.getIdLaboratoire());
        if (existingLaboOpt.isPresent()) {
            Laboratoire existingLabo = existingLaboOpt.get();
            existingLabo.setNom(labo.getNom());
            existingLabo.setAdresse(labo.getAdresse());
            existingLabo.setNumeroTel(labo.getNumeroTel());
            existingLabo.setEmail(labo.getEmail());
            existingLabo.setType(labo.getType());
            existingLabo.setStatus(labo.getStatus());

            
            if (labo.getMdp() != null && !labo.getMdp().isEmpty()) {
                existingLabo.setMdp(labo.getMdp());
            }

            return laboratoireRepository.save(existingLabo);
        } else {
            return null;
        }
    }

    @Override
    public List<Laboratoire> afficherLaboratoires() {
        return laboratoireRepository.findAll();
    }

    @Override
    public Optional<Laboratoire> afficherLaboratoireParId(Long id) {
        return laboratoireRepository.findById(id);
    }

    @Override
    public void supprimerLaboratoire(Long id) {
        laboratoireRepository.deleteById(id);
    }
}

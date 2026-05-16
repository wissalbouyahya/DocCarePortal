package com.projet.DocCarePortal.Services;

import com.projet.DocCarePortal.Entity.*;
import com.projet.DocCarePortal.Exception.ResourceConflictException;
import com.projet.DocCarePortal.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RendezvousServiceImpl implements RendezvousService {

    @Autowired private RendezvousRepository rendezvousRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private MedecinRepository medecinRepository;
    @Autowired private JourOffRepository jourOffRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private PaiementService paiementService;
    @Autowired private EmailServices emailServices;

    

    @Override
    @Transactional
    public Rendezvous creerRendezVous(Long medecinId, Long patientId,
                                      LocalDateTime dateHeure, boolean enLigne) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin introuvable"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        boolean dejaReserve = rendezvousRepository
                .existsByMedecinAndDateHeureAndEstReserveTrue(medecin, dateHeure);
        if (dejaReserve)
            throw new ResourceConflictException("Ce créneau est déjà réservé");

        Rendezvous rdv = rendezvousRepository
                .findByMedecinAndDateHeure(medecin, dateHeure)
                .orElse(new Rendezvous());

        rdv.setMedecin(medecin);
        rdv.setPatient(patient);
        rdv.setDateHeure(dateHeure);
        rdv.setEstReserve(true);
        rdv.setEnLigne(enLigne);

        
        rdv.setStatutRendezVous(StatutRendezVous.EN_ATTENTE);

        Rendezvous saved = rendezvousRepository.save(rdv);

       
        creerNotificationMedecin(saved,
                "Nouveau rendez-vous demandé par "
                        + patient.getPrenom() + " " + patient.getNom()
                        + " pour le "
                        + dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                        + (enLigne ? " (visio)" : " (présentiel)"));

        return saved;
    }

   

    @Override
    public List<CreneauDTO> genererCreneauxPourMedecin(Long medecinId, LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            return new ArrayList<>();
        if (jourOffRepository.existsByMedecin_IdMedecinAndDateOff(medecinId, date))
            return new ArrayList<>();

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        List<CreneauDTO> creneaux = new ArrayList<>();
        creneaux.addAll(genererCreneauxParPeriode(medecin, date, LocalTime.of(8, 0),  LocalTime.of(12, 0)));
        creneaux.addAll(genererCreneauxParPeriode(medecin, date, LocalTime.of(14, 0), LocalTime.of(18, 0)));
        return creneaux;
    }

   

    @Override
    @Transactional
    public List<CreneauDTO> listerCreneauxDisponiblesDTO(Long medecinId, LocalDate date) {
        if (jourOffRepository.existsByMedecin_IdMedecinAndDateOff(medecinId, date))
            return new ArrayList<>();
        if (date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY)
            return new ArrayList<>();

        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        List<CreneauDTO> tousCreneaux = new ArrayList<>();
        tousCreneaux.addAll(genererCreneauxParPeriode(medecin, date, LocalTime.of(8, 0),  LocalTime.of(12, 0)));
        tousCreneaux.addAll(genererCreneauxParPeriode(medecin, date, LocalTime.of(14, 0), LocalTime.of(18, 0)));

        List<Rendezvous> rdvs = rendezvousRepository.findByMedecinIdAndDateHeureBetween(
                medecinId, date.atStartOfDay(), date.atTime(23, 59));

        for (CreneauDTO c : tousCreneaux) {
            Optional<Rendezvous> existant = rdvs.stream()
                    .filter(r -> r.getDateHeure().equals(c.getDateHeure()))
                    .findFirst();

            if (existant.isPresent()) {
                Rendezvous r = existant.get();
                c.setId(r.getId());
                c.setReserve(r.isEstReserve());
                c.setStatutRendezVous(r.getStatutRendezVous());
                if (r.getPatient() != null) {
                    c.setPatientId(r.getPatient().getIdPatient());
                    c.setNomPatient(r.getPatient().getNom());
                    c.setPrenomPatient(r.getPatient().getPrenom());
                }
            } else {
                Rendezvous newRdv = new Rendezvous();
                newRdv.setMedecin(medecin);
                newRdv.setDateHeure(c.getDateHeure());
                newRdv.setEstReserve(false);
                newRdv.setStatutRendezVous(StatutRendezVous.LIB);
                Rendezvous saved = rendezvousRepository.save(newRdv);
                c.setId(saved.getId());
                c.setReserve(false);
                c.setStatutRendezVous(StatutRendezVous.LIB);
            }
        }
        return tousCreneaux;
    }

    

    @Override
    @Transactional
    public void annulerRendezVous(Long id) {
        Rendezvous rdv = getRendezVousOrThrow(id);
        rdv.setStatutRendezVous(StatutRendezVous.ANNULE);
        rdv.setEstReserve(false);
        rdv.setPatient(null);
        rendezvousRepository.save(rdv);
    }


    @Override
    @Transactional
    public void bloquerCreneauParDate(Long medecinId, LocalDateTime dateHeure) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        Optional<Rendezvous> existant =
                rendezvousRepository.findByMedecinAndDateHeure(medecin, dateHeure);

        if (existant.isPresent()) {
            Rendezvous rdv = existant.get();
            if (rdv.getStatutRendezVous() == StatutRendezVous.CONFIRME
                    || rdv.getStatutRendezVous() == StatutRendezVous.EN_ATTENTE)
                throw new RuntimeException("Impossible de bloquer un créneau déjà réservé");
            rdv.setStatutRendezVous(StatutRendezVous.BLOQUER);
            rdv.setEstReserve(false);
            rendezvousRepository.save(rdv);
        } else {
            Rendezvous rdv = new Rendezvous();
            rdv.setMedecin(medecin);
            rdv.setDateHeure(dateHeure);
            rdv.setStatutRendezVous(StatutRendezVous.BLOQUER);
            rdv.setEstReserve(false);
            rendezvousRepository.save(rdv);
        }
    }



    @Override
    @Transactional
    public void debloquerCreneau(Long medecinId, LocalDateTime dateHeure) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        Rendezvous rdv = rendezvousRepository.findByMedecinAndDateHeure(medecin, dateHeure)
                .orElseThrow(() -> new RuntimeException("Créneau introuvable"));

        if (rdv.getStatutRendezVous() != StatutRendezVous.BLOQUER)
            throw new RuntimeException("Ce créneau n'est pas bloqué");

        rdv.setStatutRendezVous(StatutRendezVous.LIB);
        rdv.setEstReserve(false);
        rdv.setPatient(null);
        rendezvousRepository.save(rdv);
    }


    @Override
    @Transactional
    public void bloquerJournee(Long medecinId, LocalDate date) {
        Medecin medecin = medecinRepository.findById(medecinId)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));
        if (jourOffRepository.existsByMedecin_IdMedecinAndDateOff(medecinId, date))
            throw new ResourceConflictException("Journée déjà bloquée");

        JourOff j = new JourOff();
        j.setMedecin(medecin);
        j.setDateOff(date);
        jourOffRepository.save(j);
    }



    @Override
    @Transactional
    public void debloquerJournee(Long medecinId, LocalDate date) {
        JourOff jourOff = jourOffRepository.findByMedecinIdAndDateOff(medecinId, date)
                .orElseThrow(() -> new RuntimeException("Aucun jour bloqué trouvé pour cette date"));
        jourOffRepository.delete(jourOff);

        List<Rendezvous> rdvsJournee = rendezvousRepository.findByMedecinIdAndDateHeureBetween(
                medecinId, date.atStartOfDay(), date.atTime(23, 59));
        for (Rendezvous rdv : rdvsJournee) {
            if (rdv.getStatutRendezVous() == StatutRendezVous.BLOQUER) {
                rdv.setStatutRendezVous(StatutRendezVous.LIB);
                rdv.setEstReserve(false);
                rendezvousRepository.save(rdv);
            }
        }
    }



    @Override
    public List<Rendezvous> listerCreneauxDisponibles(Long medecinId, LocalDate date) {
        return rendezvousRepository.findByMedecinIdAndDateHeureBetween(
                medecinId, date.atStartOfDay(), date.atTime(23, 59));
    }

    @Override
    public List<Rendezvous> afficherRendezvousByMedecinId(Long medecinId) {
        return rendezvousRepository.findByMedecinId(medecinId);
    }

    @Override
    public List<Rendezvous> listerRendezVousParMedecin(Long medecinId, LocalDate date) {
        return rendezvousRepository.findByMedecinIdAndDateHeureBetween(
                medecinId, date.atStartOfDay(), date.atTime(23, 59));
    }

    @Override
    public List<Rendezvous> listerRendezVousParPatient(Long patientId) {
        return rendezvousRepository.findByPatientId(patientId);
    }

    @Override
    public List<Rendezvous> getRendezVousByMedecinAndStatut(Long medecinId, String status) {
        if ("TOUS".equalsIgnoreCase(status))
            return rendezvousRepository.findByMedecinId(medecinId);
        return rendezvousRepository.findByMedecinIdAndStatut(medecinId, status);
    }

    @Override
    public List<CreneauDTO> listerTousCreneaux(Long medecinId, LocalDate date) {
        return listerCreneauxDisponiblesDTO(medecinId, date);
    }

   

    @Override
    @Transactional
    public Rendezvous modifierRendezVous(Long id, LocalDateTime nouvelleDate,
                                         String modeConsultation) {
        Rendezvous rdv = getRendezVousOrThrow(id);

        if (rdv.getStatutRendezVous() != StatutRendezVous.EN_ATTENTE)
            throw new RuntimeException("RDV non modifiable");

        Optional<Rendezvous> existing =
                rendezvousRepository.findByMedecinAndDateHeure(rdv.getMedecin(), nouvelleDate);
        if (existing.isPresent()
                && !existing.get().getId().equals(id)
                && existing.get().isEstReserve())
            throw new ResourceConflictException("Ce créneau est déjà réservé");

        rdv.setDateHeure(nouvelleDate);
        if (modeConsultation != null)
            rdv.setEnLigne("EN_LIGNE".equalsIgnoreCase(modeConsultation));

        return rendezvousRepository.save(rdv);
    }

    

    @Override
    @Transactional
    public Rendezvous confirmerRendezVous(Long rdvId) {
        Rendezvous rdv = getRendezVousOrThrow(rdvId);

        if (rdv.getStatutRendezVous() != StatutRendezVous.EN_ATTENTE)
            throw new IllegalStateException("Le RDV doit être EN_ATTENTE pour être confirmé");

       
        rdv.setStatutRendezVous(StatutRendezVous.EN_ATTENTE_PAIEMENT);


        Rendezvous saved = rendezvousRepository.save(rdv);

        envoyerEmailConfirmationPatient(rdv);

        
        creerNotificationPatient(saved,
                "Votre rendez-vous du "
                        + rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                        + " a été confirmé par votre médecin. Veuillez procéder au paiement.");

        return saved;
    }

    

    @Override
    @Transactional
    public Rendezvous validerPaiement(Long rdvId, String referenceTransaction) {
        Rendezvous rdv = getRendezVousOrThrow(rdvId);

        if (rdv.getStatutRendezVous() != StatutRendezVous.EN_ATTENTE_PAIEMENT
                && rdv.getStatutRendezVous() != StatutRendezVous.PAIEMENT_ECHOUE)
            throw new IllegalStateException(
                    "Statut invalide pour paiement : " + rdv.getStatutRendezVous());

       
        if (Boolean.TRUE.equals(rdv.getEnLigne()) && rdv.getLienVisio() == null) {
            rdv.setLienVisio(generateVisioLink(rdv.getId()));
        }

        rdv.setStatutRendezVous(StatutRendezVous.PAYE);
        rendezvousRepository.save(rdv);

        paiementService.enregistrerSuccesPaiement(rdv, referenceTransaction);

       
        envoyerNotificationEtEmailAuMedecin(rdv, rdv.getLienVisio());

       
        String msgPatient = Boolean.TRUE.equals(rdv.getEnLigne())
                ? "Paiement validé ! Votre consultation vidéo du "
                + rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                + " est confirmée. Le lien de connexion est disponible."
                : "Paiement validé ! Votre consultation du "
                + rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                + " est confirmée. Rendez-vous au cabinet.";
        creerNotificationPatient(rdv, msgPatient);

        return rdv;
    }



    @Override
    @Transactional
    public Rendezvous echecPaiement(Long rdvId) {
        Rendezvous rdv = getRendezVousOrThrow(rdvId);
        rdv.setStatutRendezVous(StatutRendezVous.PAIEMENT_ECHOUE);
        rendezvousRepository.save(rdv);
        paiementService.enregistrerEchecPaiement(rdv);

        creerNotificationPatient(rdv,
                "Votre paiement pour le rendez-vous du "
                        + rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                        + " a échoué. Veuillez réessayer.");

        return rdv;
    }



    @Override
    @Transactional
    public Rendezvous demarrerConsultation(Long rdvId) {
        Rendezvous rdv = getRendezVousOrThrow(rdvId);
        if (rdv.getStatutRendezVous() != StatutRendezVous.PAYE)
            throw new IllegalStateException("Le paiement doit être validé avant de démarrer");
        rdv.setStatutRendezVous(StatutRendezVous.EN_COURS);
        return rendezvousRepository.save(rdv);
    }

    @Override
    @Transactional
    public Rendezvous terminerConsultation(Long rdvId) {
        Rendezvous rdv = getRendezVousOrThrow(rdvId);
        if (rdv.getStatutRendezVous() != StatutRendezVous.EN_COURS)
            throw new IllegalStateException("La consultation doit être EN_COURS pour être terminée");
        rdv.setStatutRendezVous(StatutRendezVous.TERMINE);
        return rendezvousRepository.save(rdv);
    }



    private Rendezvous getRendezVousOrThrow(Long id) {
        return rendezvousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
    }

    private List<CreneauDTO> genererCreneauxParPeriode(Medecin medecin, LocalDate date,
                                                       LocalTime debut, LocalTime fin) {
        List<CreneauDTO> creneaux = new ArrayList<>();
        LocalTime heure = debut;
        while (heure.isBefore(fin)) {
            creneaux.add(new CreneauDTO(LocalDateTime.of(date, heure), false, StatutRendezVous.LIB));
            heure = heure.plusMinutes(30);
        }
        return creneaux;
    }

    private String generateVisioLink(Long rdvId) {
        String roomName = "doccare_" + rdvId + "_"
                + UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/" + roomName;
    }

  
    private void creerNotificationMedecin(Rendezvous rdv, String message) {
        if (rdv.getMedecin() == null) return;
        try {
            Notification notif = new Notification();
            notif.setMedecin(rdv.getMedecin());
            notif.setPatient(rdv.getPatient());
            notif.setType("NOUVEAU_RDV");
            notif.setMessage(message);
            notif.setLu(false);
            notif.setDateCreation(LocalDateTime.now());
            notif.setDestinataire("MEDECIN");
            notificationRepository.save(notif);
        } catch (Exception e) {
            System.err.println(" Erreur notification médecin : " + e.getMessage());
        }
    }


    private void creerNotificationPatient(Rendezvous rdv, String message) {
        if (rdv.getPatient() == null) return;
        try {
            Notification notif = new Notification();
            notif.setPatient(rdv.getPatient());
            notif.setMedecin(rdv.getMedecin());
            notif.setType("CONFIRMATION");
            notif.setMessage(message);
            notif.setLu(false);
            notif.setDateCreation(LocalDateTime.now());
            notif.setDestinataire("PATIENT");
            notificationRepository.save(notif);
        } catch (Exception e) {
            System.err.println(" Erreur notification patient : " + e.getMessage());
        }
    }

    private void envoyerEmailConfirmationPatient(Rendezvous rdv) {
        if (rdv.getPatient() == null || rdv.getPatient().getEmail() == null) return;
        try {
            String nomMedecin = rdv.getMedecin() != null
                    ? "Dr. " + rdv.getMedecin().getPrenom() + " " + rdv.getMedecin().getNom()
                    : "votre médecin";
            String modeInfo = Boolean.TRUE.equals(rdv.getEnLigne())
                    ? "en visio (le lien sera disponible après paiement)"
                    : "en présentiel au cabinet";
            emailServices.envoyerEmail(
                    rdv.getPatient().getEmail(),
                    "DocCare — Rendez-vous confirmé, paiement requis",
                    "Bonjour " + rdv.getPatient().getPrenom() + ",\n\n"
                            + "Votre rendez-vous avec " + nomMedecin
                            + " prévu le "
                            + rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
                            + " (" + modeInfo + ") a été confirmé.\n\n"
                            + "Veuillez vous connecter à DocCare pour procéder au paiement.\n\n"
                            + "Cordialement,\nL'équipe DocCare"
            );
        } catch (Exception e) {
            System.err.println(" Erreur envoi email confirmation : " + e.getMessage());
        }
    }


    private void envoyerNotificationEtEmailAuMedecin(Rendezvous rdv, String visioLink) {
        Medecin medecin = rdv.getMedecin();
        if (medecin == null) return;

        Patient patient = rdv.getPatient();
        String patientNom = patient != null
                ? patient.getPrenom() + " " + patient.getNom()
                : "le patient";

        boolean enLigne = Boolean.TRUE.equals(rdv.getEnLigne());
        String infoVisio = enLigne && visioLink != null
                ? "\nLien visio : " + visioLink
                : "\nConsultation en présentiel au cabinet.";

        try {
            emailServices.envoyerEmail(
                    medecin.getEmail(),
                    "DocCare — Consultation payée",
                    String.format(
                            "Bonjour Dr %s %s,\n\n"
                                    + "Le patient %s a réglé sa consultation du %s.%s\n\n"
                                    + "Cordialement,\nL'équipe DocCare",
                            medecin.getPrenom(), medecin.getNom(), patientNom,
                            rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                            infoVisio)
            );
        } catch (Exception e) {
            System.err.println(" Erreur email médecin : " + e.getMessage());
        }

        try {
            String msgNotif = enLigne && visioLink != null
                    ? String.format("Paiement reçu de %s pour le RDV du %s. Lien : %s",
                    patientNom,
                    rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")),
                    visioLink)
                    : String.format("Paiement reçu de %s pour le RDV présentiel du %s.",
                    patientNom,
                    rdv.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")));

            Notification notif = new Notification();
            notif.setMedecin(medecin);
            notif.setPatient(patient);
            notif.setType("PAIEMENT");
            notif.setMessage(msgNotif);
            notif.setLu(false);
            notif.setDateCreation(LocalDateTime.now());
            notif.setDestinataire("MEDECIN");
            notificationRepository.save(notif);
        } catch (Exception e) {
            System.err.println(" Erreur notification médecin paiement : " + e.getMessage());
        }
    }
}

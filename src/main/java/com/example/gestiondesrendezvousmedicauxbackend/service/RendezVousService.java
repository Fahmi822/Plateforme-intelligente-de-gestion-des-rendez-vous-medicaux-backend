package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DocteurRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.PatientRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.RendezVousRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RendezVousService {

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmailService emailService;

    public RendezVous createRendezVous(RendezVousDTO rendezVousDTO) {
        // Vérifier que le patient existe
        Patient patient = patientRepository.findById(rendezVousDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        // Vérifier que le docteur existe
        Docteur docteur = docteurRepository.findById(rendezVousDTO.getDocteurId())
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));

        // Vérifier la disponibilité du docteur
        if (!isDocteurDisponible(docteur.getId(), rendezVousDTO.getDateHeure())) {
            throw new RuntimeException("Le docteur n'est pas disponible à cette date/heure");
        }

        RendezVous rendezVous = new RendezVous();
        rendezVous.setDateHeure(rendezVousDTO.getDateHeure());
        rendezVous.setMotif(rendezVousDTO.getMotif());
        rendezVous.setPatient(patient);
        rendezVous.setDocteur(docteur);
        rendezVous.setStatut(RendezVous.StatutRendezVous.PLANIFIE);

        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);

        // Envoyer un email de confirmation
        try {
            emailService.envoyerConfirmationRendezVous(patient.getEmail(), patient.getPrenom(),
                    docteur.getNom(), rendezVous.getDateHeure());
        } catch (Exception e) {
            System.out.println("Erreur envoi email: " + e.getMessage());
        }

        return savedRendezVous;
    }

    private boolean isDocteurDisponible(Long docteurId, LocalDateTime dateHeure) {
        // Vérifier s'il n'y a pas déjà un rendez-vous à cette heure
        LocalDateTime start = dateHeure.minusMinutes(29); // 30 minutes avant
        LocalDateTime end = dateHeure.plusMinutes(29);   // 30 minutes après

        List<RendezVous> conflicts = rendezVousRepository.findByDocteurIdAndDateHeureBetween(
                docteurId, start, end);

        return conflicts.isEmpty();
    }

    // Méthode pour formater les informations du docteur en toute sécurité
    private String getDocteurInfo(Docteur docteur) {
        if (docteur == null) {
            return "Docteur inconnu";
        }

        String nomComplet = docteur.getNom() + " " + docteur.getPrenom();
        String specialite = docteur.getSpecialite() != null ?
                docteur.getSpecialite().getTitre() : "Spécialité non définie";

        return nomComplet + " (" + specialite + ")";
    }

    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    public RendezVous annulerRendezVous(Long rendezVousId, Long patientId) {
        RendezVous rendezVous = rendezVousRepository.findByIdAndPatientId(rendezVousId, patientId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        if (rendezVous.getStatut() == RendezVous.StatutRendezVous.TERMINE) {
            throw new RuntimeException("Impossible d'annuler un rendez-vous terminé");
        }

        rendezVous.setStatut(RendezVous.StatutRendezVous.ANNULE);
        rendezVous.setDateModification(LocalDateTime.now());

        return rendezVousRepository.save(rendezVous);
    }

    public RendezVous confirmerRendezVous(Long rendezVousId, Long patientId) {
        RendezVous rendezVous = rendezVousRepository.findByIdAndPatientId(rendezVousId, patientId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        rendezVous.setStatut(RendezVous.StatutRendezVous.CONFIRME);
        rendezVous.setDateModification(LocalDateTime.now());

        return rendezVousRepository.save(rendezVous);
    }
}
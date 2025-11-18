package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousCreationDTO;
import com.example.gestiondesrendezvousmedicauxbackend.exception.DocteurNonDisponibleException;
import com.example.gestiondesrendezvousmedicauxbackend.mapper.EntityMapper;
import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DocteurRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.PatientRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.RendezVousRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private DisponibiliteService disponibiliteService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EntityMapper entityMapper;

    public RendezVousDTO createRendezVous(RendezVousCreationDTO rendezVousDTO) {
        Patient patient = patientRepository.findById(rendezVousDTO.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        Docteur docteur = docteurRepository.findById(rendezVousDTO.getDocteurId())
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));

        if (!disponibiliteService.estDocteurDisponible(docteur.getId(), rendezVousDTO.getDateHeure())) {
            List<LocalDateTime> creneauxDisponibles = disponibiliteService.getCreneauxDisponibles(
                    docteur.getId(),
                    rendezVousDTO.getDateHeure(),
                    rendezVousDTO.getDateHeure().plusDays(7)
            );

            throw new DocteurNonDisponibleException(
                    "Le docteur n'est pas disponible à cette date/heure",
                    creneauxDisponibles
            );
        }

        RendezVous rendezVous = new RendezVous();
        rendezVous.setDateHeure(rendezVousDTO.getDateHeure());
        rendezVous.setMotif(rendezVousDTO.getMotif());
        rendezVous.setPatient(patient);
        rendezVous.setDocteur(docteur);
        rendezVous.setStatut(RendezVous.StatutRendezVous.PLANIFIE);

        RendezVous savedRendezVous = rendezVousRepository.save(rendezVous);

        try {
            emailService.envoyerConfirmationRendezVous(
                    patient.getEmail(),
                    patient.getPrenom(),
                    docteur.getNom() + " " + docteur.getPrenom(),
                    rendezVous.getDateHeure()
            );
        } catch (Exception e) {
            System.out.println("Erreur envoi email: " + e.getMessage());
        }

        return entityMapper.toRendezVousDTO(savedRendezVous);
    }

    public List<RendezVousDTO> getRendezVousByPatient(Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByPatientId(patientId);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public RendezVousDTO annulerRendezVous(Long rendezVousId, Long patientId) {
        RendezVous rendezVous = rendezVousRepository.findByIdAndPatientId(rendezVousId, patientId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        if (rendezVous.getStatut() == RendezVous.StatutRendezVous.TERMINE) {
            throw new RuntimeException("Impossible d'annuler un rendez-vous terminé");
        }

        rendezVous.setStatut(RendezVous.StatutRendezVous.ANNULE);
        rendezVous.setDateModification(LocalDateTime.now());

        RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);
        return entityMapper.toRendezVousDTO(updatedRendezVous);
    }

    public RendezVousDTO confirmerRendezVous(Long rendezVousId, Long patientId) {
        RendezVous rendezVous = rendezVousRepository.findByIdAndPatientId(rendezVousId, patientId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        rendezVous.setStatut(RendezVous.StatutRendezVous.CONFIRME);
        rendezVous.setDateModification(LocalDateTime.now());

        RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);
        return entityMapper.toRendezVousDTO(updatedRendezVous);
    }

    public List<RendezVousDTO> getRendezVousByDocteur(Long docteurId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurId(docteurId);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public List<RendezVousDTO> getRendezVousByDocteurAndDate(Long docteurId, String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndDateHeureBetween(docteurId, startOfDay, endOfDay);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public List<RendezVousDTO> getRendezVousByDocteurAndStatut(Long docteurId, String statut) {
        RendezVous.StatutRendezVous statutEnum;
        try {
            statutEnum = RendezVous.StatutRendezVous.valueOf(statut.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Statut invalide: " + statut);
        }

        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndStatut(docteurId, statutEnum);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public RendezVousDTO terminerRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));

        rendezVous.setStatut(RendezVous.StatutRendezVous.TERMINE);
        rendezVous.setDateModification(LocalDateTime.now());

        RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);
        return entityMapper.toRendezVousDTO(updatedRendezVous);
    }
}
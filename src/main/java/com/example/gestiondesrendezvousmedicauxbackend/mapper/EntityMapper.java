package com.example.gestiondesrendezvousmedicauxbackend.mapper;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EntityMapper {

    public UtilisateurDTO toUtilisateurDTO(Utilisateur utilisateur) {
        if (utilisateur == null) return null;

        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        dto.setRole(utilisateur.getRole());
        dto.setTelephone(utilisateur.getTelephone());
        dto.setAdresse(utilisateur.getAdresse());
        dto.setActif(utilisateur.isActif());
        dto.setDateCreation(utilisateur.getDateCreation());
        dto.setDateModification(utilisateur.getDateModification());

        return dto;
    }

    public PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) return null;

        PatientDTO dto = new PatientDTO();
        // Copier les propriétés de Utilisateur
        dto.setId(patient.getId());
        dto.setNom(patient.getNom());
        dto.setPrenom(patient.getPrenom());
        dto.setEmail(patient.getEmail());
        dto.setRole(patient.getRole());
        dto.setTelephone(patient.getTelephone());
        dto.setAdresse(patient.getAdresse());
        dto.setActif(patient.isActif());
        dto.setDateCreation(patient.getDateCreation());
        dto.setDateModification(patient.getDateModification());

        // Propriétés spécifiques Patient
        dto.setDateNaissance(patient.getDateNaissance());
        dto.setGroupeSanguin(patient.getGroupeSanguin());
        dto.setAntecedentsMedicaux(patient.getAntecedentsMedicaux());
        dto.setPhoto(patient.getPhoto());

        return dto;
    }

    public DocteurDTO toDocteurDTO(Docteur docteur) {
        if (docteur == null) return null;

        DocteurDTO dto = new DocteurDTO();
        // Copier les propriétés de Utilisateur
        dto.setId(docteur.getId());
        dto.setNom(docteur.getNom());
        dto.setPrenom(docteur.getPrenom());
        dto.setEmail(docteur.getEmail());
        dto.setRole(docteur.getRole());
        dto.setTelephone(docteur.getTelephone());
        dto.setAdresse(docteur.getAdresse());
        dto.setActif(docteur.isActif());
        dto.setDateCreation(docteur.getDateCreation());
        dto.setDateModification(docteur.getDateModification());

        // Propriétés spécifiques Docteur
        dto.setSpecialite(toSpecialiteDTO(docteur.getSpecialite()));
        dto.setNumeroLicence(docteur.getNumeroLicence());
        dto.setAnneesExperience(docteur.getAnneesExperience());
        dto.setTarifConsultation(docteur.getTarifConsultation());
        dto.setLangue(docteur.getLangue());
        dto.setPhoto(docteur.getPhoto());
        dto.setNoteMoyenne(docteur.getNoteMoyenne());
        dto.setNombreAvis(docteur.getNombreAvis());

        return dto;
    }

    public SpecialiteDTO toSpecialiteDTO(Specialite specialite) {
        if (specialite == null) return null;

        SpecialiteDTO dto = new SpecialiteDTO();
        dto.setId(specialite.getId());
        dto.setTitre(specialite.getTitre());
        dto.setDescription(specialite.getDescription());

        return dto;
    }

    public RendezVousDTO toRendezVousDTO(RendezVous rendezVous) {
        if (rendezVous == null) return null;

        RendezVousDTO dto = new RendezVousDTO();
        dto.setId(rendezVous.getId());
        dto.setDateHeure(rendezVous.getDateHeure());
        dto.setStatut(rendezVous.getStatut().name());
        dto.setMotif(rendezVous.getMotif());
        dto.setNotes(rendezVous.getNotes());
        dto.setDateCreation(rendezVous.getDateCreation());
        dto.setDateModification(rendezVous.getDateModification());

        // Utiliser les DTOs pour éviter la récursion
        dto.setPatient(toPatientDTO(rendezVous.getPatient()));
        dto.setDocteur(toDocteurDTO(rendezVous.getDocteur()));

        return dto;
    }

    public DisponibiliteDTO toDisponibiliteDTO(Disponibilite disponibilite) {
        if (disponibilite == null) return null;

        DisponibiliteDTO dto = new DisponibiliteDTO();
        dto.setId(disponibilite.getId());
        dto.setDateHeureDebut(disponibilite.getDateHeureDebut());
        dto.setDateHeureFin(disponibilite.getDateHeureFin());
        dto.setDisponible(disponibilite.isDisponible());
        dto.setMotifIndisponibilite(disponibilite.getMotifIndisponibilite());
        dto.setDateCreation(disponibilite.getDateCreation());

        // Utiliser DTO pour éviter la récursion
        dto.setDocteur(toDocteurDTO(disponibilite.getDocteur()));

        return dto;
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.DisponibiliteDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.DisponibiliteCreationDTO;
import com.example.gestiondesrendezvousmedicauxbackend.mapper.EntityMapper;
import com.example.gestiondesrendezvousmedicauxbackend.model.Disponibilite;
import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DisponibiliteRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DocteurRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DisponibiliteService {

    @Autowired
    private DisponibiliteRepository disponibiliteRepository;

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private EntityMapper entityMapper;

    public boolean estDocteurDisponible(Long docteurId, LocalDateTime dateHeure) {
        List<Disponibilite> disponibilites = disponibiliteRepository
                .findDisponibilitesContenantDate(docteurId, dateHeure);

        if (disponibilites.isEmpty()) {
            return false;
        }

        LocalDateTime debutRecherche = dateHeure.minusMinutes(29);
        LocalDateTime finRecherche = dateHeure.plusMinutes(29);

        List<RendezVous> rendezVousConflict = rendezVousRepository
                .findByDocteurIdAndDateHeureBetween(docteurId, debutRecherche, finRecherche);

        return rendezVousConflict.isEmpty();
    }

    public List<LocalDateTime> getCreneauxDisponibles(Long docteurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<LocalDateTime> creneauxDisponibles = new ArrayList<>();

        List<Disponibilite> disponibilites = disponibiliteRepository
                .findDisponibilitesByDocteurAndPeriode(docteurId, dateDebut, dateFin);

        for (Disponibilite dispo : disponibilites) {
            LocalDateTime current = dispo.getDateHeureDebut();

            while (current.isBefore(dispo.getDateHeureFin())) {
                if (estDocteurDisponible(docteurId, current)) {
                    creneauxDisponibles.add(current);
                }

                current = current.plusMinutes(30);

                if (current.isAfter(dispo.getDateHeureFin())) {
                    break;
                }
            }
        }

        return creneauxDisponibles;
    }

    public List<DisponibiliteDTO> getProchainesDisponibilites(Long docteurId, int jours) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime dateLimite = maintenant.plusDays(jours);

        List<Disponibilite> disponibilites = disponibiliteRepository.findProchainesDisponibilites(docteurId, maintenant)
                .stream()
                .limit(jours * 2) // Limiter le nombre de résultats
                .collect(Collectors.toList());

        return disponibilites.stream()
                .map(entityMapper::toDisponibiliteDTO)
                .collect(Collectors.toList());
    }

    public DisponibiliteDTO ajouterDisponibilite(DisponibiliteCreationDTO disponibiliteDTO) {
        Docteur docteur = docteurRepository.findById(disponibiliteDTO.getDocteurId())
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));

        List<Disponibilite> chevauchements = disponibiliteRepository
                .findDisponibilitesChevauchantes(
                        docteur.getId(),
                        disponibiliteDTO.getDateHeureDebut(),
                        disponibiliteDTO.getDateHeureFin()
                );

        if (!chevauchements.isEmpty()) {
            throw new RuntimeException("Cette disponibilité chevauche avec une disponibilité existante");
        }

        Disponibilite disponibilite = new Disponibilite();
        disponibilite.setDateHeureDebut(disponibiliteDTO.getDateHeureDebut());
        disponibilite.setDateHeureFin(disponibiliteDTO.getDateHeureFin());
        disponibilite.setDocteur(docteur);
        disponibilite.setDisponible(true);

        Disponibilite savedDisponibilite = disponibiliteRepository.save(disponibilite);
        return entityMapper.toDisponibiliteDTO(savedDisponibilite);
    }

    public DisponibiliteDTO ajouterIndisponibilite(DisponibiliteCreationDTO disponibiliteDTO) {
        Docteur docteur = docteurRepository.findById(disponibiliteDTO.getDocteurId())
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));

        List<RendezVous> rendezVous = rendezVousRepository
                .findByDocteurIdAndDateHeureBetween(
                        docteur.getId(),
                        disponibiliteDTO.getDateHeureDebut(),
                        disponibiliteDTO.getDateHeureFin()
                );

        if (!rendezVous.isEmpty()) {
            throw new RuntimeException("Impossible de marquer comme indisponible : il y a des rendez-vous planifiés");
        }

        Disponibilite indisponibilite = new Disponibilite();
        indisponibilite.setDateHeureDebut(disponibiliteDTO.getDateHeureDebut());
        indisponibilite.setDateHeureFin(disponibiliteDTO.getDateHeureFin());
        indisponibilite.setDocteur(docteur);
        indisponibilite.setDisponible(false);
        indisponibilite.setMotifIndisponibilite(disponibiliteDTO.getMotifIndisponibilite());

        Disponibilite savedIndisponibilite = disponibiliteRepository.save(indisponibilite);
        return entityMapper.toDisponibiliteDTO(savedIndisponibilite);
    }

    public List<DisponibiliteDTO> genererDisponibilitesSemaine(Long docteurId, LocalDateTime dateDebutSemaine) {
        Docteur docteur = docteurRepository.findById(docteurId)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));

        List<Disponibilite> disponibilites = new ArrayList<>();

        for (int jour = 0; jour < 5; jour++) {
            LocalDateTime dateJour = dateDebutSemaine.plusDays(jour);

            LocalDateTime debutMatin = dateJour.with(LocalTime.of(9, 0));
            LocalDateTime finMatin = dateJour.with(LocalTime.of(12, 0));

            Disponibilite dispoMatin = new Disponibilite(debutMatin, finMatin, docteur);
            disponibilites.add(disponibiliteRepository.save(dispoMatin));

            LocalDateTime debutApresMidi = dateJour.with(LocalTime.of(14, 0));
            LocalDateTime finApresMidi = dateJour.with(LocalTime.of(18, 0));

            Disponibilite dispoApresMidi = new Disponibilite(debutApresMidi, finApresMidi, docteur);
            disponibilites.add(disponibiliteRepository.save(dispoApresMidi));
        }

        return disponibilites.stream()
                .map(entityMapper::toDisponibiliteDTO)
                .collect(Collectors.toList());
    }

    public void supprimerDisponibilite(Long disponibiliteId) {
        Disponibilite disponibilite = disponibiliteRepository.findById(disponibiliteId)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        if (disponibilite.isDisponible()) {
            List<RendezVous> rendezVous = rendezVousRepository
                    .findByDocteurIdAndDateHeureBetween(
                            disponibilite.getDocteur().getId(),
                            disponibilite.getDateHeureDebut(),
                            disponibilite.getDateHeureFin()
                    );

            if (!rendezVous.isEmpty()) {
                throw new RuntimeException("Impossible de supprimer : il y a des rendez-vous planifiés");
            }
        }

        disponibiliteRepository.delete(disponibilite);
    }
}
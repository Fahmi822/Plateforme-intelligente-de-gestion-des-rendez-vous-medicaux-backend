package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByPatientId(Long patientId);
    List<RendezVous> findByPatientIdOrderByDateHeureDesc(Long patientId);

    List<RendezVous> findByDocteurId(Long docteurId);
    List<RendezVous> findByDocteurIdAndPatientId(Long docteurId, Long patientId);

    List<RendezVous> findByDocteurIdAndDateHeureBetween(Long docteurId, LocalDateTime start, LocalDateTime end);
    List<RendezVous> findByDocteurIdAndStatut(Long docteurId, RendezVous.StatutRendezVous statut);

    List<RendezVous> findByPatientIdAndDateHeureAfterAndStatutNot(Long patientId, LocalDateTime date, RendezVous.StatutRendezVous statut);
    List<RendezVous> findByPatientIdAndDateHeureBefore(Long patientId, LocalDateTime date);

    Optional<RendezVous> findByIdAndPatientId(Long id, Long patientId);

    long countByDateHeureBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT r.statut, COUNT(r) FROM RendezVous r GROUP BY r.statut")
    List<Object[]> countByStatutGroupByStatut();

    @Query("SELECT r.docteur, COUNT(r) FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin GROUP BY r.docteur ORDER BY COUNT(r) DESC")
    List<Object[]> findDocteursPlusActifs(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r WHERE r.dateCreation > :date")
    List<RendezVous> findByDateCreationAfter(@Param("date") LocalDateTime date);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin")
    List<RendezVous> findByPeriode(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT r FROM RendezVous r WHERE LOWER(r.motif) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.patient.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(r.docteur.nom) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<RendezVous> searchRendezVous(@Param("q") String q);

    @Query("SELECT FUNCTION('YEAR', r.dateHeure), FUNCTION('MONTH', r.dateHeure), r.statut, COUNT(r) FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin GROUP BY FUNCTION('YEAR', r.dateHeure), FUNCTION('MONTH', r.dateHeure), r.statut ORDER BY FUNCTION('YEAR', r.dateHeure), FUNCTION('MONTH', r.dateHeure)")
    List<Object[]> findStatistiquesMensuelles(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
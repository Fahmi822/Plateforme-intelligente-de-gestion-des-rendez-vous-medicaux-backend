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
    List<RendezVous> findByDocteurId(Long docteurId);
    List<RendezVous> findByDocteurIdAndDateHeureBetween(Long docteurId, LocalDateTime start, LocalDateTime end);
    List<RendezVous> findByPatientIdAndStatut(Long patientId, RendezVous.StatutRendezVous statut);
    List<RendezVous> findByDocteurIdAndStatut(Long docteurId, RendezVous.StatutRendezVous statut);
    Optional<RendezVous> findByIdAndPatientId(Long id, Long patientId);

    // Nouvelles méthodes pour l'admin
    @Query("SELECT COUNT(r) FROM RendezVous r WHERE r.dateHeure BETWEEN :start AND :end")
    long countByDateHeureBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    long countByStatut(RendezVous.StatutRendezVous statut);

    @Query("SELECT r FROM RendezVous r WHERE r.dateCreation >= :dateCreation")
    List<RendezVous> findByDateCreationAfter(@Param("dateCreation") LocalDateTime dateCreation);

    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure >= :dateDebut AND r.dateHeure <= :dateFin")
    List<RendezVous> findByPeriode(@Param("dateDebut") LocalDateTime dateDebut,
                                   @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r FROM RendezVous r WHERE r.docteur.id = :docteurId AND r.dateHeure >= :dateDebut AND r.dateHeure <= :dateFin")
    List<RendezVous> findByDocteurAndPeriode(@Param("docteurId") Long docteurId,
                                             @Param("dateDebut") LocalDateTime dateDebut,
                                             @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r FROM RendezVous r WHERE r.patient.id = :patientId AND r.dateHeure >= :dateDebut AND r.dateHeure <= :dateFin")
    List<RendezVous> findByPatientAndPeriode(@Param("patientId") Long patientId,
                                             @Param("dateDebut") LocalDateTime dateDebut,
                                             @Param("dateFin") LocalDateTime dateFin);

    //@Query("SELECT r FROM RendezVous r ORDER BY r.dateCreation DESC LIMIT :limit")
    //List<RendezVous> findTopNByOrderByDateCreationDesc(@Param("limit") int limit);

    @Query("SELECT r.statut, COUNT(r) FROM RendezVous r GROUP BY r.statut")
    List<Object[]> countByStatutGroupByStatut();

    @Query("SELECT MONTH(r.dateHeure), YEAR(r.dateHeure), COUNT(r) FROM RendezVous r WHERE r.dateHeure >= :dateDebut AND r.dateHeure <= :dateFin GROUP BY MONTH(r.dateHeure), YEAR(r.dateHeure) ORDER BY YEAR(r.dateHeure), MONTH(r.dateHeure)")
    List<Object[]> findStatistiquesMensuelles(@Param("dateDebut") LocalDateTime dateDebut,
                                              @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r.docteur, COUNT(r) FROM RendezVous r WHERE r.dateHeure >= :dateDebut AND r.dateHeure <= :dateFin GROUP BY r.docteur ORDER BY COUNT(r) DESC")
    List<Object[]> findDocteursPlusActifs(@Param("dateDebut") LocalDateTime dateDebut,
                                          @Param("dateFin") LocalDateTime dateFin);

    @Query("SELECT r FROM RendezVous r WHERE LOWER(r.motif) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.patient.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.patient.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.docteur.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(r.docteur.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<RendezVous> searchRendezVous(@Param("searchTerm") String searchTerm);
}
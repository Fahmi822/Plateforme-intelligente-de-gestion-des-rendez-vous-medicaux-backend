package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {

    List<Disponibilite> findByDocteurId(Long docteurId);

    @Query("SELECT d FROM Disponibilite d WHERE d.docteur.id = :docteurId AND d.dateHeureDebut <= :dateHeure AND d.dateHeureFin >= :dateHeure AND d.disponible = true")
    List<Disponibilite> findDisponibilitesContenantDate(@Param("docteurId") Long docteurId, @Param("dateHeure") LocalDateTime dateHeure);

    @Query("SELECT d FROM Disponibilite d WHERE d.docteur.id = :docteurId AND d.dateHeureDebut >= :debut AND d.dateHeureFin <= :fin")
    List<Disponibilite> findDisponibilitesByDocteurAndPeriode(@Param("docteurId") Long docteurId, @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);

    @Query("SELECT d FROM Disponibilite d WHERE d.docteur.id = :docteurId AND d.dateHeureDebut >= :maintenant ORDER BY d.dateHeureDebut")
    List<Disponibilite> findProchainesDisponibilites(@Param("docteurId") Long docteurId, @Param("maintenant") LocalDateTime maintenant);

    @Query("SELECT d FROM Disponibilite d WHERE d.docteur.id = :docteurId AND ((d.dateHeureDebut BETWEEN :debut AND :fin) OR (d.dateHeureFin BETWEEN :debut AND :fin) OR (d.dateHeureDebut <= :debut AND d.dateHeureFin >= :fin))")
    List<Disponibilite> findDisponibilitesChevauchantes(@Param("docteurId") Long docteurId, @Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
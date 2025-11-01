package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocteurRepository extends JpaRepository<Docteur, Long> {
        Optional<Docteur> findByEmail(String email);
        List<Docteur> findBySpecialiteId(Long specialiteId);
        List<Docteur> findByActifTrue();

        @Query("SELECT d FROM Docteur d WHERE d.specialite.id = :specialiteId AND d.actif = true")
        List<Docteur> findDocteursActifsBySpecialite(@Param("specialiteId") Long specialiteId);

        List<Docteur> findByActifFalse();

        @Query("SELECT COUNT(d) FROM Docteur d WHERE d.actif = true")
        long countByActifTrue();

        @Query("SELECT d FROM Docteur d WHERE d.dateCreation >= :dateDebut")
        List<Docteur> findDocteursRecents(@Param("dateDebut") LocalDateTime dateDebut);

        @Query("SELECT d FROM Docteur d WHERE LOWER(d.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(d.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(d.specialite.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
        List<Docteur> searchDocteurs(@Param("searchTerm") String searchTerm);

        @Query("SELECT d FROM Docteur d WHERE d.anneesExperience >= :anneesExperience")
        List<Docteur> findByAnneesExperienceGreaterThanEqual(@Param("anneesExperience") Integer anneesExperience);

        @Query("SELECT AVG(d.noteMoyenne) FROM Docteur d WHERE d.actif = true")
        Double findMoyenneNotesDocteurs();

        @Query("SELECT d FROM Docteur d WHERE d.noteMoyenne >= :noteMin")
        List<Docteur> findByNoteMoyenneGreaterThanEqual(@Param("noteMin") Double noteMin);
}
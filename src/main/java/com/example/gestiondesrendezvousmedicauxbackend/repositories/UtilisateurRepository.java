package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByEmailAndActifTrue(String email);
    boolean existsByEmail(String email);

    List<Utilisateur> findByRole(String role);
    List<Utilisateur> findByActifTrue();
    List<Utilisateur> findByActifFalse();

    long countByActifTrue();
    long countByActifFalse();

    @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation >= :date")
    List<Utilisateur> findUtilisateursRecents(@Param("date") LocalDateTime date);

    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Utilisateur> searchUtilisateurs(@Param("q") String q);
}
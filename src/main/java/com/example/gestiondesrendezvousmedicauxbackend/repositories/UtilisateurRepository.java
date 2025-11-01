package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM Utilisateur u WHERE u.email = :email AND u.actif = true")
    Optional<Utilisateur> findByEmailAndActifTrue(@Param("email") String email);

    List<Utilisateur> findByRole(String role);

    List<Utilisateur> findByActifTrue();

    List<Utilisateur> findByActifFalse();

    @Query("SELECT u FROM Utilisateur u WHERE u.role = :role AND u.actif = true")
    List<Utilisateur> findByRoleAndActifTrue(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.role = :role")
    long countByRole(@Param("role") String role);

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = true")
    long countByActifTrue();

    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.actif = false")
    long countByActifFalse();

    @Query("SELECT u FROM Utilisateur u WHERE LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> searchUtilisateurs(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM Utilisateur u WHERE u.dateCreation >= :dateDebut")
    List<Utilisateur> findUtilisateursRecents(@Param("dateDebut") java.time.LocalDateTime dateDebut);
}
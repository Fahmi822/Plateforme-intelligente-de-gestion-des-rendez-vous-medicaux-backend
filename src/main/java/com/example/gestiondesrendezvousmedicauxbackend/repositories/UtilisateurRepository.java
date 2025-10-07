package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
}


package com.example.gestiondesrendezvousmedicauxbackend.repositories;


import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
}

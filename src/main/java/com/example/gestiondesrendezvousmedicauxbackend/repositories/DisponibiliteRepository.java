package com.example.gestiondesrendezvousmedicauxbackend.repositories;



import com.example.gestiondesrendezvousmedicauxbackend.model.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
}

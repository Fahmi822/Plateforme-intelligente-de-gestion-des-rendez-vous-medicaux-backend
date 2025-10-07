package com.example.gestiondesrendezvousmedicauxbackend.repositories;



import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocteurRepository extends JpaRepository<Docteur, Long> {
}

package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByEmail(String email);
    List<Patient> findByActifTrue();

    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(p.email) LIKE LOWER(CONCAT('%', :q, '%'))")
    List<Patient> searchPatients(@Param("q") String q);
}
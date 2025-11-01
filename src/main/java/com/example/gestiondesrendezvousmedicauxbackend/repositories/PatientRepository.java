package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Patient> findByActifTrue();

    List<Patient> findByActifFalse();

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.actif = true")
    long countByActifTrue();

    @Query("SELECT p FROM Patient p WHERE p.dateCreation >= :dateDebut")
    List<Patient> findPatientsRecents(@Param("dateDebut") LocalDateTime dateDebut);

    @Query("SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Patient> searchPatients(@Param("searchTerm") String searchTerm);

    @Query("SELECT p FROM Patient p WHERE p.dateNaissance BETWEEN :dateDebut AND :dateFin")
    List<Patient> findByDateNaissanceBetween(@Param("dateDebut") java.time.LocalDate dateDebut,
                                             @Param("dateFin") java.time.LocalDate dateFin);

}
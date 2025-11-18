package com.example.gestiondesrendezvousmedicauxbackend.repositories;

import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocteurRepository extends JpaRepository<Docteur, Long> {

        Optional<Docteur> findByEmail(String email);
        List<Docteur> findByActifTrue();
        List<Docteur> findBySpecialiteId(Long specialiteId);

        @Query("SELECT d FROM Docteur d WHERE d.actif = true AND d.specialite.id = :specialiteId")
        List<Docteur> findDocteursActifsBySpecialite(@Param("specialiteId") Long specialiteId);

        List<Docteur> findByNoteMoyenneGreaterThanEqual(Double note);

        @Query("SELECT d FROM Docteur d WHERE LOWER(d.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(d.prenom) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(d.email) LIKE LOWER(CONCAT('%', :q, '%'))")
        List<Docteur> searchDocteurs(@Param("q") String q);

        long countByActifTrue();
}
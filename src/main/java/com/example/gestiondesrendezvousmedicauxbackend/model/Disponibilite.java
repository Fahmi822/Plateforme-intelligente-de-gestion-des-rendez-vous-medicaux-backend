package com.example.gestiondesrendezvousmedicauxbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateHeureDebut;

    @Column(nullable = false)
    private LocalDateTime dateHeureFin;

    @Column(nullable = false)
    private boolean disponible = true;

    private String motifIndisponibilite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docteur_id", nullable = false)
    @JsonIgnore
    private Docteur docteur;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    public Disponibilite() {
        this.dateCreation = LocalDateTime.now();
    }

    public Disponibilite(LocalDateTime dateHeureDebut, LocalDateTime dateHeureFin, Docteur docteur) {
        this();
        this.dateHeureDebut = dateHeureDebut;
        this.dateHeureFin = dateHeureFin;
        this.docteur = docteur;
        this.disponible = true;
    }

    public boolean chevauche(LocalDateTime debut, LocalDateTime fin) {
        return (this.dateHeureDebut.isBefore(fin) && this.dateHeureFin.isAfter(debut));
    }

    public boolean contient(LocalDateTime dateHeure) {
        return (dateHeure.isEqual(dateHeureDebut) || dateHeure.isAfter(dateHeureDebut)) &&
                (dateHeure.isEqual(dateHeureFin) || dateHeure.isBefore(dateHeureFin));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateHeureDebut() {
        return dateHeureDebut;
    }

    public void setDateHeureDebut(LocalDateTime dateHeureDebut) {
        this.dateHeureDebut = dateHeureDebut;
    }

    public LocalDateTime getDateHeureFin() {
        return dateHeureFin;
    }

    public void setDateHeureFin(LocalDateTime dateHeureFin) {
        this.dateHeureFin = dateHeureFin;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getMotifIndisponibilite() {
        return motifIndisponibilite;
    }

    public void setMotifIndisponibilite(String motifIndisponibilite) {
        this.motifIndisponibilite = motifIndisponibilite;
    }

    public Docteur getDocteur() {
        return docteur;
    }

    public void setDocteur(Docteur docteur) {
        this.docteur = docteur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
}
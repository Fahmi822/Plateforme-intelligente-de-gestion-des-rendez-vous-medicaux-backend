package com.example.gestiondesrendezvousmedicauxbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Docteur extends Utilisateur {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    private String numeroLicence;
    private Integer anneesExperience;
    private Double tarifConsultation;
    private String langue;
    private String photo;

    @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Disponibilite> disponibilites = new ArrayList<>();

    @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RendezVous> rendezVous = new ArrayList<>();

    private Double noteMoyenne = 0.0;
    private Integer nombreAvis = 0;

    public Docteur() {
        super();
    }

    public Docteur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse, "DOCTEUR");
    }

    public Docteur(String nom, String prenom, String email, String motDePasse,
                   Specialite specialite, String numeroLicence) {
        super(nom, prenom, email, motDePasse, "DOCTEUR");
        this.specialite = specialite;
        this.numeroLicence = numeroLicence;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public String getNumeroLicence() {
        return numeroLicence;
    }

    public void setNumeroLicence(String numeroLicence) {
        this.numeroLicence = numeroLicence;
    }

    public Integer getAnneesExperience() {
        return anneesExperience;
    }

    public void setAnneesExperience(Integer anneesExperience) {
        this.anneesExperience = anneesExperience;
    }

    public Double getTarifConsultation() {
        return tarifConsultation;
    }

    public void setTarifConsultation(Double tarifConsultation) {
        this.tarifConsultation = tarifConsultation;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<Disponibilite> getDisponibilites() {
        return disponibilites;
    }

    public void setDisponibilites(List<Disponibilite> disponibilites) {
        this.disponibilites = disponibilites;
    }

    public List<RendezVous> getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(List<RendezVous> rendezVous) {
        this.rendezVous = rendezVous;
    }

    public Double getNoteMoyenne() {
        return noteMoyenne;
    }

    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }

    public Integer getNombreAvis() {
        return nombreAvis;
    }

    public void setNombreAvis(Integer nombreAvis) {
        this.nombreAvis = nombreAvis;
    }
}
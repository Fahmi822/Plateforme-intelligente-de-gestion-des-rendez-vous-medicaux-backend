package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Docteur extends Utilisateur {
    @ManyToOne
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    private String numeroLicence;
    private Integer anneesExperience;
    private Double tarifConsultation;
    private String langue;
    private String photo;

    @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Disponibilite> disponibilites = new ArrayList<>();

    @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "docteur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avis> avis = new ArrayList<>();

    private Double noteMoyenne = 0.0;
    private Integer nombreAvis = 0;

    // Constructeur par défaut OBLIGATOIRE
    public Docteur() {
        super();
    }

    // Constructeurs avec paramètres
    public Docteur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse, "DOCTEUR");
    }

    public Docteur(String nom, String prenom, String email, String motDePasse,
                   Specialite specialite, String numeroLicence) {
        super(nom, prenom, email, motDePasse, "DOCTEUR");
        this.specialite = specialite;
        this.numeroLicence = numeroLicence;
    }

    // Getters et Setters explicites (au cas où Lombok ne fonctionne pas)
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
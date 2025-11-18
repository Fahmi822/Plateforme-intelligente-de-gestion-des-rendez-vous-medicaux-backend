package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocteurDTO extends UtilisateurDTO {
    private SpecialiteDTO specialite;
    private String numeroLicence;
    private Integer anneesExperience;
    private Double tarifConsultation;
    private String langue;
    private String photo;
    private Double noteMoyenne;
    private Integer nombreAvis;

    public SpecialiteDTO getSpecialite() {
        return specialite;
    }

    public void setSpecialite(SpecialiteDTO specialite) {
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
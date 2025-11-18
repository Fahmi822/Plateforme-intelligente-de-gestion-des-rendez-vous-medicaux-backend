package com.example.gestiondesrendezvousmedicauxbackend.dto;

public class DocteurSignupRequest extends SignupRequest {
    private Long specialiteId;
    private String numeroLicence;
    private Integer anneesExperience;
    private Double tarifConsultation;
    private String langue;

    public DocteurSignupRequest() {
        super();
    }

    // Getters et Setters
    public Long getSpecialiteId() { return specialiteId; }
    public void setSpecialiteId(Long specialiteId) { this.specialiteId = specialiteId; }

    public String getNumeroLicence() { return numeroLicence; }
    public void setNumeroLicence(String numeroLicence) { this.numeroLicence = numeroLicence; }

    public Integer getAnneesExperience() { return anneesExperience; }
    public void setAnneesExperience(Integer anneesExperience) { this.anneesExperience = anneesExperience; }

    public Double getTarifConsultation() { return tarifConsultation; }
    public void setTarifConsultation(Double tarifConsultation) { this.tarifConsultation = tarifConsultation; }

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
}
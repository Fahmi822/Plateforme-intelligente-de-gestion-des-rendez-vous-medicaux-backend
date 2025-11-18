package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RendezVousCreationDTO {
    private LocalDateTime dateHeure;
    private String motif;
    private Long patientId;
    private Long docteurId;

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDocteurId() {
        return docteurId;
    }

    public void setDocteurId(Long docteurId) {
        this.docteurId = docteurId;
    }
}
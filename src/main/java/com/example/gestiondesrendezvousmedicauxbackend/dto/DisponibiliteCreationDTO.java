package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DisponibiliteCreationDTO {
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private Long docteurId;
    private String motifIndisponibilite;

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

    public Long getDocteurId() {
        return docteurId;
    }

    public void setDocteurId(Long docteurId) {
        this.docteurId = docteurId;
    }

    public String getMotifIndisponibilite() {
        return motifIndisponibilite;
    }

    public void setMotifIndisponibilite(String motifIndisponibilite) {
        this.motifIndisponibilite = motifIndisponibilite;
    }
}
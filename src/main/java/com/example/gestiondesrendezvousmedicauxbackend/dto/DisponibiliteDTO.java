package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class DisponibiliteDTO extends BaseDTO {
    private LocalDateTime dateHeureDebut;
    private LocalDateTime dateHeureFin;
    private boolean disponible;
    private String motifIndisponibilite;
    private DocteurDTO docteur;

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

    public DocteurDTO getDocteur() {
        return docteur;
    }

    public void setDocteur(DocteurDTO docteur) {
        this.docteur = docteur;
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class RendezVousDTO extends BaseDTO {
    private LocalDateTime dateHeure;
    private String statut;
    private String motif;
    private String notes;
    private PatientDTO patient;
    private DocteurDTO docteur;

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public PatientDTO getPatient() {
        return patient;
    }

    public void setPatient(PatientDTO patient) {
        this.patient = patient;
    }

    public DocteurDTO getDocteur() {
        return docteur;
    }

    public void setDocteur(DocteurDTO docteur) {
        this.docteur = docteur;
    }
}
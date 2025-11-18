package com.example.gestiondesrendezvousmedicauxbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Patient extends Utilisateur {

    private LocalDate dateNaissance;
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String photo;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RendezVous> rendezVous = new ArrayList<>();

    public Patient() {
        super();
    }

    public Patient(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email, motDePasse, "PATIENT");
    }

    public Patient(String nom, String prenom, String email, String motDePasse,
                   LocalDate dateNaissance, String groupeSanguin, String antecedentsMedicaux) {
        super(nom, prenom, email, motDePasse, "PATIENT");
        this.dateNaissance = dateNaissance;
        this.groupeSanguin = groupeSanguin;
        this.antecedentsMedicaux = antecedentsMedicaux;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getGroupeSanguin() {
        return groupeSanguin;
    }

    public void setGroupeSanguin(String groupeSanguin) {
        this.groupeSanguin = groupeSanguin;
    }

    public String getAntecedentsMedicaux() {
        return antecedentsMedicaux;
    }

    public void setAntecedentsMedicaux(String antecedentsMedicaux) {
        this.antecedentsMedicaux = antecedentsMedicaux;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<RendezVous> getRendezVous() {
        return rendezVous;
    }

    public void setRendezVous(List<RendezVous> rendezVous) {
        this.rendezVous = rendezVous;
    }
}
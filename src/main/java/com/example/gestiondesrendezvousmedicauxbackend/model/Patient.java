package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Patient extends Utilisateur {
    private LocalDate dateNaissance;
    private String groupeSanguin;
    private String antecedentsMedicaux;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Avis> avis = new ArrayList<>();

    // Constructeur par défaut OBLIGATOIRE
    public Patient() {
        super();
    }

    // Constructeurs avec paramètres
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
}
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
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String titre;

    private String description;

    @OneToMany(mappedBy = "specialite", fetch = FetchType.LAZY)
    private List<Docteur> docteurs = new ArrayList<>();

    // Constructeur par défaut
    public Specialite() {
    }

    // Constructeur avec paramètres
    public Specialite(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    // Getters et Setters explicites
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Docteur> getDocteurs() {
        return docteurs;
    }

    public void setDocteurs(List<Docteur> docteurs) {
        this.docteurs = docteurs;
    }
}
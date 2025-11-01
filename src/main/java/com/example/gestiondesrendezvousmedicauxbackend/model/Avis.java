package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Avis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer note;

    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private boolean approuve = false;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "docteur_id", nullable = false)
    private Docteur docteur;

    @ManyToOne
    @JoinColumn(name = "rendezvous_id")
    private RendezVous rendezVous;

    // Constructeurs
    public Avis(Integer note, String commentaire, Patient patient, Docteur docteur) {
        this.note = note;
        this.commentaire = commentaire;
        this.patient = patient;
        this.docteur = docteur;
        this.dateCreation = LocalDateTime.now();
    }
}
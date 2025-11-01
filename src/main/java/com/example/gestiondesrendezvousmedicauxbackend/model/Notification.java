package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeNotification type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutNotification statut = StatutNotification.NON_LU;

    @Column(nullable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateLecture;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "rendezvous_id")
    private RendezVous rendezVous;

    public enum TypeNotification {
        NOUVEAU_RENDEZVOUS,
        CONFIRMATION_RENDEZVOUS,
        ANNULATION_RENDEZVOUS,
        RAPPEL_RENDEZVOUS,
        NOUVEL_AVIS,
        NOUVELLE_DISPONIBILITE,
        URGENCE
    }

    public enum StatutNotification {
        NON_LU,
        LU
    }
}
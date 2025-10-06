package com.example.gestiondesrendezvousmedicauxbackend.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVous {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateHeure;
    private String statut;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Docteur docteur;
}

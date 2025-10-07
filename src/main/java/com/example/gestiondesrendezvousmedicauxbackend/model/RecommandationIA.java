package com.example.gestiondesrendezvousmedicauxbackend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommandationIA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String description;
    private String resultats;

    @ManyToOne
    private Patient patient;
}

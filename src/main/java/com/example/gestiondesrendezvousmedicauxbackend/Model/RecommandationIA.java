package com.example.gestiondesrendezvousmedicauxbackend.Model;


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

package com.example.gestiondesrendezvousmedicauxbackend.Model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;

    @OneToMany(mappedBy = "specialite")
    private List<Docteur> docteurs;
}

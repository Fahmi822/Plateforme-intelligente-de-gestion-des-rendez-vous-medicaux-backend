package com.example.gestiondesrendezvousmedicauxbackend.Model;


import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Docteur extends Utilisateur {
    @ManyToOne
    private Specialite specialite;

    @OneToMany(mappedBy = "docteur")
    private List<Disponibilite> disponibilites;
}

package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends Utilisateur {
    private String niveauAcces;

    // Constructeur par défaut OBLIGATOIRE pour JPA
    public Admin() {
        super();
    }

    // Constructeur avec paramètres
    public Admin(String nom, String prenom, String email, String motDePasse, String niveauAcces) {
        super(nom, prenom, email, motDePasse, "ADMIN");
        this.niveauAcces = niveauAcces;
    }
}
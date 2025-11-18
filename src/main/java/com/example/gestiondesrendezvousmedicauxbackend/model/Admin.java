package com.example.gestiondesrendezvousmedicauxbackend.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Admin extends Utilisateur {
    private String niveauAcces;

    public Admin() {
        super();
    }

    public Admin(String nom, String prenom, String email, String motDePasse, String niveauAcces) {
        super(nom, prenom, email, motDePasse, "ADMIN");
        this.niveauAcces = niveauAcces;
    }
}
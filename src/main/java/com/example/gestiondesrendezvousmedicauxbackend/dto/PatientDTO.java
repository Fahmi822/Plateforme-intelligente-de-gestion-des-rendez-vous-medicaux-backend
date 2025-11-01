package com.example.gestiondesrendezvousmedicauxbackend.dto;

import java.time.LocalDate;

public class PatientDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
    private String numeroSecuriteSociale;
}
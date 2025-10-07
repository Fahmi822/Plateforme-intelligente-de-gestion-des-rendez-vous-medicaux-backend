package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.model.*;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.*;
import com.example.gestiondesrendezvousmedicauxbackend.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
        );

        Utilisateur user = utilisateurRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(
                User.builder()
                        .username(user.getEmail())
                        .password(user.getMotDePasse())
                        .roles(user.getRole())
                        .build()
        );

        return new LoginResponse(token, user.getRole(), "Connexion réussie !");
    }

    @Transactional
    public String signup(SignupRequest request) {
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé !");
        }


        switch (request.getRole().toUpperCase()) {
            case "PATIENT":
                Patient patient = new Patient();
                patient.setNom(request.getNom());
                patient.setPrenom(request.getPrenom());
                patient.setEmail(request.getEmail());
                patient.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
                patient.setRole("PATIENT");

                patientRepository.save(patient);
                return "Inscription réussie pour le patient : " + patient.getNom();

            case "DOCTEUR":
                Docteur docteur = new Docteur();
                docteur.setNom(request.getNom());
                docteur.setPrenom(request.getPrenom());
                docteur.setEmail(request.getEmail());
                docteur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
                docteur.setRole("DOCTEUR");
                // La spécialité pourra être ajoutée ultérieurement

                docteurRepository.save(docteur);
                return "Inscription réussie pour le docteur : " + docteur.getNom();

            case "ADMIN":
                // Pour ADMIN, créer directement un Utilisateur
                Utilisateur admin = new Utilisateur();
                admin.setNom(request.getNom());
                admin.setPrenom(request.getPrenom());
                admin.setEmail(request.getEmail());
                admin.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
                admin.setRole("ADMIN");

                utilisateurRepository.save(admin);
                return "Inscription réussie pour l'admin : " + admin.getNom();

            default:
                throw new RuntimeException("Rôle non reconnu: " + request.getRole());
        }
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.model.*;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.*;
import com.example.gestiondesrendezvousmedicauxbackend.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse login(LoginRequest request) {
        try {
            System.out.println("Tentative de connexion pour: " + request.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenUtil.generateToken(userDetails);

            Utilisateur user = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            System.out.println("Connexion réussie pour: " + user.getEmail() + ", rôle: " + user.getRole());

            return new LoginResponse(token, user.getRole(), "Connexion réussie !");

        } catch (BadCredentialsException e) {
            System.out.println("Bad credentials pour: " + request.getEmail());
            throw new RuntimeException("Email ou mot de passe incorrect");
        } catch (Exception e) {
            System.out.println("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion: " + e.getMessage());
        }
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
                return "Patient inscrit avec succès: " + patient.getNom();

            case "DOCTEUR":
                Docteur docteur = new Docteur();
                docteur.setNom(request.getNom());
                docteur.setPrenom(request.getPrenom());
                docteur.setEmail(request.getEmail());
                docteur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
                docteur.setRole("DOCTEUR");
                docteurRepository.save(docteur);
                return "Docteur inscrit avec succès: " + docteur.getNom();

            case "ADMIN":
                Utilisateur admin = new Utilisateur();
                admin.setNom(request.getNom());
                admin.setPrenom(request.getPrenom());
                admin.setEmail(request.getEmail());
                admin.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
                admin.setRole("ADMIN");
                utilisateurRepository.save(admin);
                return "Admin inscrit avec succès: " + admin.getNom();

            default:
                throw new RuntimeException("Rôle non reconnu: " + request.getRole());
        }
    }
}
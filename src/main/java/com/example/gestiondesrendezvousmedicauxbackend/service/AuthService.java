package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.LoginRequest;
import com.example.gestiondesrendezvousmedicauxbackend.dto.LoginResponse;
import com.example.gestiondesrendezvousmedicauxbackend.dto.SignupRequest;
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
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    public LoginResponse login(LoginRequest request) {
        try {
            System.out.println("Tentative de connexion pour: " + request.getEmail());

            // Vérifier si l'utilisateur existe et est actif
            Utilisateur user = utilisateurRepository.findByEmailAndActifTrue(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé ou compte désactivé"));

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMotDePasse())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Récupérer l'ID de l'utilisateur selon son type
            Long userId = getUserIdByRole(user);

            String token = jwtTokenUtil.generateToken(userDetails, userId);

            System.out.println("Connexion réussie pour: " + user.getEmail() + ", rôle: " + user.getRole() + ", ID: " + userId);

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

    private Long getUserIdByRole(Utilisateur user) {
        switch (user.getRole()) {
            case "PATIENT":
                Patient patient = patientRepository.findByEmail(user.getEmail())
                        .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
                return patient.getId();
            case "DOCTEUR":
                Docteur docteur = docteurRepository.findByEmail(user.getEmail())
                        .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
                return docteur.getId();
            case "ADMIN":
                return user.getId();
            default:
                throw new RuntimeException("Rôle non reconnu: " + user.getRole());
        }
    }

    @Transactional
    public String signup(SignupRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }

        String result;

        switch (request.getRole().toUpperCase()) {
            case "PATIENT":
                Patient patient = new Patient(
                        request.getNom(),
                        request.getPrenom(),
                        request.getEmail(),
                        passwordEncoder.encode(request.getMotDePasse())
                );
                patientRepository.save(patient);
                result = "Patient inscrit avec succès: " + patient.getNom();
                break;

            case "DOCTEUR":
                Docteur docteur = new Docteur(
                        request.getNom(),
                        request.getPrenom(),
                        request.getEmail(),
                        passwordEncoder.encode(request.getMotDePasse())
                );
                docteurRepository.save(docteur);
                result = "Docteur inscrit avec succès: " + docteur.getNom();
                break;

            case "ADMIN":
                Admin admin = new Admin(
                        request.getNom(),
                        request.getPrenom(),
                        request.getEmail(),
                        passwordEncoder.encode(request.getMotDePasse()),
                        "SUPER_ADMIN"
                );
                adminRepository.save(admin);
                result = "Admin inscrit avec succès: " + admin.getNom();
                break;

            default:
                throw new RuntimeException("Rôle non reconnu: " + request.getRole());
        }

        // Envoyer un email de confirmation
        try {
            emailService.envoyerEmailConfirmation(request.getEmail(), request.getNom(), request.getRole());
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            // Ne pas bloquer l'inscription si l'email échoue
        }

        return result;
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.config;

import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (utilisateurRepository.findByEmail("admin@clinique.com").isEmpty()) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setPrenom("System");
            admin.setEmail("admin@clinique.com");
            admin.setMotDePasse(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            utilisateurRepository.save(admin);
            System.out.println("✅ Compte administrateur créé avec succès !");
        }
    }
}
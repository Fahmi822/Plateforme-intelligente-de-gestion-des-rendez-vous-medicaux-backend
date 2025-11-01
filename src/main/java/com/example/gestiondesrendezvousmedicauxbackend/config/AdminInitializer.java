package com.example.gestiondesrendezvousmedicauxbackend.config;

import com.example.gestiondesrendezvousmedicauxbackend.model.Admin;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.findByEmail("admin@clinique.com").isEmpty()) {
            Admin admin = new Admin(
                    "Admin",
                    "System",
                    "admin@clinique.com",
                    passwordEncoder.encode("admin123"),
                    "SUPER_ADMIN"
            );

            adminRepository.save(admin);
            System.out.println("✅ Compte administrateur créé avec succès !");
        }
    }
}
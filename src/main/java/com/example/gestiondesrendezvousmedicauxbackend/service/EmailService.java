package com.example.gestiondesrendezvousmedicauxbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerEmailConfirmation(String email, String nom, String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Confirmation d'inscription - Système de Rendez-vous Médicaux");

            String contenu = String.format(
                    "Bonjour %s,\n\n" +
                            "Votre inscription en tant que %s a été effectuée avec succès.\n" +
                            "Vous pouvez maintenant vous connecter à votre compte.\n\n" +
                            "Cordialement,\nL'équipe de gestion des rendez-vous médicaux",
                    nom, role
            );

            message.setText(contenu);
            message.setFrom("noreply@cliniquemedicale.com");

            mailSender.send(message);
            System.out.println("Email de confirmation envoyé à: " + email);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email de confirmation");
        }
    }

    public void envoyerConfirmationRendezVous(String email, String prenom, String nom, LocalDateTime dateHeure) {
    }
}
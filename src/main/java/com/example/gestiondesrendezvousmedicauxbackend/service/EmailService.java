package com.example.gestiondesrendezvousmedicauxbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void envoyerEmailConfirmation(String email, String nom, String role) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Confirmation d'inscription - Plateforme Médicale");

            String contenu = String.format(
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background: #4CAF50; color: white; padding: 10px; text-align: center; }
                            .content { padding: 20px; background: #f9f9f9; }
                            .footer { text-align: center; padding: 10px; font-size: 12px; color: #666; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>Plateforme Médicale</h2>
                            </div>
                            <div class="content">
                                <h3>Bonjour %s,</h3>
                                <p>Votre inscription en tant que <strong>%s</strong> a été effectuée avec succès.</p>
                                <p>Vous pouvez maintenant vous connecter à votre compte et accéder à toutes les fonctionnalités.</p>
                                <p><strong>Date d'inscription :</strong> %s</p>
                            </div>
                            <div class="footer">
                                <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                    nom, role, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
            );

            helper.setText(contenu, true);
            helper.setFrom("fahmihlel822@gmail.com");

            mailSender.send(message);
            System.out.println("Email de confirmation envoyé à: " + email);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            envoyerEmailSimple(email, nom, role);
        }
    }

    private void envoyerEmailSimple(String email, String nom, String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Confirmation d'inscription - Plateforme Médicale");

            String contenu = String.format(
                    "Bonjour %s,\n\n" +
                            "Votre inscription en tant que %s a été effectuée avec succès.\n" +
                            "Vous pouvez maintenant vous connecter à votre compte.\n\n" +
                            "Date d'inscription : %s\n\n" +
                            "Cordialement,\nL'équipe de la Plateforme Médicale",
                    nom, role, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
            );

            message.setText(contenu);
            message.setFrom("fahmihlel822@gmail.com");

            mailSender.send(message);
            System.out.println("Email simple de confirmation envoyé à: " + email);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email simple: " + e.getMessage());
        }
    }

    @Async
    public void envoyerConfirmationRendezVous(String email, String prenomPatient, String nomDocteur, LocalDateTime dateHeure) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Confirmation de rendez-vous médical");

            String contenu = String.format(
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <style>
                            body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                            .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                            .header { background: #2196F3; color: white; padding: 10px; text-align: center; }
                            .content { padding: 20px; background: #f9f9f9; }
                            .info-box { background: white; padding: 15px; border-left: 4px solid #2196F3; margin: 10px 0; }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <h2>Confirmation de Rendez-vous</h2>
                            </div>
                            <div class="content">
                                <h3>Bonjour %s,</h3>
                                <p>Votre rendez-vous médical a été confirmé avec succès.</p>
                                
                                <div class="info-box">
                                    <p><strong>Docteur :</strong> %s</p>
                                    <p><strong>Date et heure :</strong> %s</p>
                                    <p><strong>Statut :</strong> Confirmé</p>
                                </div>
                                
                                <p>Merci de vous présenter 10 minutes avant l'heure du rendez-vous.</p>
                            </div>
                        </div>
                    </body>
                    </html>
                    """,
                    prenomPatient,
                    nomDocteur,
                    dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"))
            );

            helper.setText(contenu, true);
            helper.setFrom("fahmihlel822@gmail.com");

            mailSender.send(message);
            System.out.println("Email de confirmation de rendez-vous envoyé à: " + email);
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de confirmation de rendez-vous: " + e.getMessage());
        }
    }
}
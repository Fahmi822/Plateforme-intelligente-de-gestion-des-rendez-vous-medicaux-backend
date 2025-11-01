package com.example.gestiondesrendezvousmedicauxbackend.config;

import com.example.gestiondesrendezvousmedicauxbackend.model.Specialite;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Override
    public void run(String... args) throws Exception {
        // Créer des spécialités par défaut si elles n'existent pas
        if (specialiteRepository.count() == 0) {
            Specialite[] specialites = {
                    new Specialite("Cardiologie", "Spécialiste des maladies du cœur et des vaisseaux sanguins"),
                    new Specialite("Dermatologie", "Spécialiste des maladies de la peau"),
                    new Specialite("Pédiatrie", "Spécialiste des enfants et des adolescents"),
                    new Specialite("Gynécologie", "Spécialiste de la santé féminine"),
                    new Specialite("Neurologie", "Spécialiste des maladies du système nerveux"),
                    new Specialite("Ophtalmologie", "Spécialiste des yeux et de la vision"),
                    new Specialite("Orthopédie", "Spécialiste des problèmes musculo-squelettiques"),
                    new Specialite("Psychiatrie", "Spécialiste des troubles mentaux"),
                    new Specialite("Radiologie", "Spécialiste de l'imagerie médicale"),
                    new Specialite("Chirurgie", "Spécialiste des interventions chirurgicales")
            };

            for (Specialite specialite : specialites) {
                specialiteRepository.save(specialite);
            }

            System.out.println("✅ Spécialités médicales créées avec succès !");
        }
    }
}
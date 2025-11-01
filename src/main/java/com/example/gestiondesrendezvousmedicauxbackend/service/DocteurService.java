package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.model.Specialite;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DocteurRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocteurService {

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    public List<Docteur> getAllDocteurs() {
        return docteurRepository.findByActifTrue();
    }

    public List<Docteur> getDocteursBySpecialite(Long specialiteId) {
        return docteurRepository.findDocteursActifsBySpecialite(specialiteId);
    }

    public Docteur getDocteurById(Long id) {
        return docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
    }

    public List<Docteur> searchDocteurs(String searchTerm) {
        return docteurRepository.findByActifTrue().stream()
                .filter(docteur -> {
                    // Vérifier si le docteur a une spécialité avant d'y accéder
                    Specialite specialite = docteur.getSpecialite();
                    String specialiteTitre = specialite != null ? specialite.getTitre() : "";

                    return docteur.getNom().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            docteur.getPrenom().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            specialiteTitre.toLowerCase().contains(searchTerm.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public Docteur updateDocteur(Long id, Docteur docteurDetails) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'id: " + id));

        // Mettre à jour les champs de base
        docteur.setNom(docteurDetails.getNom());
        docteur.setPrenom(docteurDetails.getPrenom());
        docteur.setTelephone(docteurDetails.getTelephone());
        docteur.setAdresse(docteurDetails.getAdresse());

        // Mettre à jour les champs spécifiques au docteur
        docteur.setNumeroLicence(docteurDetails.getNumeroLicence());
        docteur.setAnneesExperience(docteurDetails.getAnneesExperience());
        docteur.setTarifConsultation(docteurDetails.getTarifConsultation());
        docteur.setLangue(docteurDetails.getLangue());
        docteur.setPhoto(docteurDetails.getPhoto());

        // Mettre à jour la spécialité si fournie
        if (docteurDetails.getSpecialite() != null) {
            Specialite specialite = specialiteRepository.findById(docteurDetails.getSpecialite().getId())
                    .orElseThrow(() -> new RuntimeException("Spécialité non trouvée"));
            docteur.setSpecialite(specialite);
        }

        return docteurRepository.save(docteur);
    }

    public void deleteDocteur(Long id) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'id: " + id));
        docteur.setActif(false);
        docteurRepository.save(docteur);
    }

    public List<Docteur> getDocteursActifs() {
        return docteurRepository.findByActifTrue();
    }
}
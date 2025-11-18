package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.mapper.EntityMapper;
import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.model.Specialite;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.DocteurRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.PatientRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.RendezVousRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.SpecialiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocteurService {

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public List<DocteurDTO> getAllDocteurs() {
        List<Docteur> docteurs = docteurRepository.findByActifTrue();
        return docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
    }

    public List<DocteurDTO> getDocteursBySpecialite(Long specialiteId) {
        List<Docteur> docteurs = docteurRepository.findDocteursActifsBySpecialite(specialiteId);
        return docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
    }

    public DocteurDTO getDocteurById(Long id) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        return entityMapper.toDocteurDTO(docteur);
    }

    public List<DocteurDTO> searchDocteurs(String searchTerm) {
        return docteurRepository.findByActifTrue().stream()
                .filter(docteur -> {
                    Specialite specialite = docteur.getSpecialite();
                    String specialiteTitre = specialite != null ? specialite.getTitre() : "";

                    return docteur.getNom().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            docteur.getPrenom().toLowerCase().contains(searchTerm.toLowerCase()) ||
                            specialiteTitre.toLowerCase().contains(searchTerm.toLowerCase());
                })
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
    }

    public DocteurDTO updateDocteur(Long id, DocteurUpdateDTO docteurDetails) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'id: " + id));

        // Mettre à jour uniquement les champs fournis
        if (docteurDetails.getNom() != null) docteur.setNom(docteurDetails.getNom());
        if (docteurDetails.getPrenom() != null) docteur.setPrenom(docteurDetails.getPrenom());
        if (docteurDetails.getTelephone() != null) docteur.setTelephone(docteurDetails.getTelephone());
        if (docteurDetails.getAdresse() != null) docteur.setAdresse(docteurDetails.getAdresse());
        if (docteurDetails.getNumeroLicence() != null) docteur.setNumeroLicence(docteurDetails.getNumeroLicence());
        if (docteurDetails.getAnneesExperience() != null) docteur.setAnneesExperience(docteurDetails.getAnneesExperience());
        if (docteurDetails.getTarifConsultation() != null) docteur.setTarifConsultation(docteurDetails.getTarifConsultation());
        if (docteurDetails.getLangue() != null) docteur.setLangue(docteurDetails.getLangue());

        if (docteurDetails.getSpecialiteId() != null) {
            Specialite specialite = specialiteRepository.findById(docteurDetails.getSpecialiteId())
                    .orElseThrow(() -> new RuntimeException("Spécialité non trouvée"));
            docteur.setSpecialite(specialite);
        }

        docteur.setDateModification(LocalDateTime.now());
        Docteur updatedDocteur = docteurRepository.save(docteur);
        return entityMapper.toDocteurDTO(updatedDocteur);
    }

    public void deleteDocteur(Long id) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'id: " + id));
        docteur.setActif(false);
        docteur.setDateModification(LocalDateTime.now());
        docteurRepository.save(docteur);
    }

    public List<DocteurDTO> getDocteursActifs() {
        List<Docteur> docteurs = docteurRepository.findByActifTrue();
        return docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
    }

    public DocteurDTO updatePhotoProfil(Long id, MultipartFile photo) {
        Docteur docteur = docteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé avec l'ID: " + id));

        if (photo == null || photo.isEmpty()) {
            throw new RuntimeException("Le fichier photo est vide");
        }

        String contentType = photo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Le fichier doit être une image (JPG, PNG, etc.)");
        }

        if (photo.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("La taille de l'image ne doit pas dépasser 5MB");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFileName = StringUtils.cleanPath(photo.getOriginalFilename());
            String fileExtension = "";
            if (originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String fileName = "docteur_" + id + "_" + UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(fileName);

            // Supprimer l'ancienne photo si elle existe
            if (docteur.getPhoto() != null && !docteur.getPhoto().isEmpty()) {
                String oldFileName = docteur.getPhoto();
                if (oldFileName.contains("/")) {
                    oldFileName = oldFileName.substring(oldFileName.lastIndexOf("/") + 1);
                }
                Path oldFilePath = uploadPath.resolve(oldFileName);
                try {
                    Files.deleteIfExists(oldFilePath);
                    System.out.println("Ancienne photo supprimée: " + oldFileName);
                } catch (IOException e) {
                    System.err.println("Impossible de supprimer l'ancienne photo: " + e.getMessage());
                }
            }

            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Nouvelle photo sauvegardée: " + fileName);

            String photoUrl = "/api/uploads/" + fileName;
            docteur.setPhoto(photoUrl);
            docteur.setDateModification(LocalDateTime.now());

            Docteur savedDocteur = docteurRepository.save(docteur);
            System.out.println("Photo mise à jour en base: " + photoUrl);

            return entityMapper.toDocteurDTO(savedDocteur);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement de la photo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la photo de profil: " + e.getMessage(), e);
        }
    }

    public List<PatientDTO> getPatientsByDocteur(Long docteurId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurId(docteurId);

        return rendezVous.stream()
                .map(RendezVous::getPatient)
                .distinct()
                .map(entityMapper::toPatientDTO)
                .collect(Collectors.toList());
    }

    public List<PatientDTO> getNouveauxPatients(Long docteurId) {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(30);

        List<RendezVous> rendezVousRecents = rendezVousRepository.findByDocteurId(docteurId)
                .stream()
                .filter(rdv -> rdv.getDateCreation().isAfter(dateLimite))
                .collect(Collectors.toList());

        return rendezVousRecents.stream()
                .map(RendezVous::getPatient)
                .distinct()
                .map(entityMapper::toPatientDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientDetails(Long docteurId, Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndPatientId(docteurId, patientId);
        if (rendezVous.isEmpty()) {
            throw new RuntimeException("Patient non trouvé pour ce docteur");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        return entityMapper.toPatientDTO(patient);
    }

    public List<RendezVousDTO> getHistoriquePatient(Long docteurId, Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndPatientId(docteurId, patientId)
                .stream()
                .sorted((r1, r2) -> r2.getDateHeure().compareTo(r1.getDateHeure()))
                .collect(Collectors.toList());

        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public List<PatientDTO> rechercherPatients(Long docteurId, String query) {
        return getPatientsByDocteur(docteurId).stream()
                .filter(patient ->
                        patient.getNom().toLowerCase().contains(query.toLowerCase()) ||
                                patient.getPrenom().toLowerCase().contains(query.toLowerCase()) ||
                                patient.getEmail().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStatistiquesDocteur(Long docteurId) {
        Map<String, Object> statistiques = new HashMap<>();

        List<RendezVous> tousRendezVous = rendezVousRepository.findByDocteurId(docteurId);
        statistiques.put("totalRendezVous", tousRendezVous.size());

        long rendezVousConfirmes = tousRendezVous.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.CONFIRME)
                .count();
        statistiques.put("rendezVousConfirmes", rendezVousConfirmes);

        long nouveauxPatients = getPatientsByDocteur(docteurId).size();
        statistiques.put("nouveauxPatients", nouveauxPatients);

        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        long rdvMoisTermines = tousRendezVous.stream()
                .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.TERMINE)
                .filter(rdv -> rdv.getDateHeure().isAfter(debutMois) && rdv.getDateHeure().isBefore(finMois))
                .count();

        Docteur docteur = docteurRepository.findById(docteurId)
                .orElseThrow(() -> new RuntimeException("Docteur non trouvé"));
        double revenuMensuel = rdvMoisTermines * (docteur.getTarifConsultation() != null ? docteur.getTarifConsultation() : 0);
        statistiques.put("revenuMensuel", revenuMensuel);

        double tauxOccupation = calculerTauxOccupation(docteurId, debutMois, finMois);
        statistiques.put("tauxOccupation", tauxOccupation);

        statistiques.put("noteMoyenne", docteur.getNoteMoyenne() != null ? docteur.getNoteMoyenne() : 0.0);

        return statistiques;
    }

    public Map<String, Object> getStatistiquesMensuelles(Long docteurId, int annee, int mois) {
        Map<String, Object> statistiques = new HashMap<>();

        LocalDateTime debutMois = LocalDateTime.of(annee, mois, 1, 0, 0);
        LocalDateTime finMois = debutMois.plusMonths(1).minusSeconds(1);

        List<RendezVous> rendezVousMois = rendezVousRepository.findByDocteurId(docteurId)
                .stream()
                .filter(rdv -> rdv.getDateHeure().isAfter(debutMois) && rdv.getDateHeure().isBefore(finMois))
                .collect(Collectors.toList());

        statistiques.put("totalRendezVous", rendezVousMois.size());

        Map<String, Long> parStatut = rendezVousMois.stream()
                .collect(Collectors.groupingBy(
                        rdv -> rdv.getStatut().name(),
                        Collectors.counting()
                ));
        statistiques.put("parStatut", parStatut);

        return statistiques;
    }

    public Map<String, Object> getEvolutionRendezVous(Long docteurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        Map<String, Object> evolution = new HashMap<>();

        List<RendezVous> rendezVousPeriode = rendezVousRepository.findByDocteurId(docteurId)
                .stream()
                .filter(rdv -> rdv.getDateHeure().isAfter(dateDebut) && rdv.getDateHeure().isBefore(dateFin))
                .collect(Collectors.toList());

        Map<LocalDate, Long> parJour = rendezVousPeriode.stream()
                .collect(Collectors.groupingBy(
                        rdv -> rdv.getDateHeure().toLocalDate(),
                        Collectors.counting()
                ));

        evolution.put("parJour", parJour);
        evolution.put("totalPeriode", rendezVousPeriode.size());

        return evolution;
    }

    public Map<String, Object> getAgenda(Long docteurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        Map<String, Object> agenda = new HashMap<>();

        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndDateHeureBetween(docteurId, dateDebut, dateFin);
        List<RendezVousDTO> rendezVousDTOs = rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());

        agenda.put("rendezVous", rendezVousDTOs);

        return agenda;
    }

    public List<RendezVousDTO> getEvenementsAgenda(Long docteurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        List<RendezVous> rendezVous = rendezVousRepository.findByDocteurIdAndDateHeureBetween(docteurId, dateDebut, dateFin);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public byte[] genererRapportActivite(Long docteurId, LocalDateTime dateDebut, LocalDateTime dateFin) {
        String rapportContent = "Rapport d'activité pour le docteur " + docteurId +
                "\nPériode: " + dateDebut + " à " + dateFin;

        return rapportContent.getBytes();
    }

    public byte[] genererRapportFinancier(Long docteurId, int annee, int mois) {
        String rapportContent = "Rapport financier pour le docteur " + docteurId +
                "\nMois: " + mois + "/" + annee;

        return rapportContent.getBytes();
    }

    public boolean verifierConflitRendezVous(Long docteurId, LocalDateTime dateHeure, int duree) {
        LocalDateTime debutRecherche = dateHeure.minusMinutes(duree - 1);
        LocalDateTime finRecherche = dateHeure.plusMinutes(duree - 1);

        List<RendezVous> conflits = rendezVousRepository.findByDocteurIdAndDateHeureBetween(
                docteurId, debutRecherche, finRecherche);

        return !conflits.isEmpty();
    }

    private double calculerTauxOccupation(Long docteurId, LocalDateTime debut, LocalDateTime fin) {
        long heuresTravailMois = 8 * 20;
        long rdvMois = rendezVousRepository.findByDocteurIdAndDateHeureBetween(docteurId, debut, fin).size();

        double heuresOccupees = rdvMois * 0.5;

        return heuresTravailMois > 0 ? (heuresOccupees / heuresTravailMois) * 100 : 0;
    }
}
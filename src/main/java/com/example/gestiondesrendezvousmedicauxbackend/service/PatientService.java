package com.example.gestiondesrendezvousmedicauxbackend.service;

import com.example.gestiondesrendezvousmedicauxbackend.dto.PatientDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.mapper.EntityMapper;
import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.PatientRepository;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.RendezVousRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private EntityMapper entityMapper;

    @Value("${upload.dir}")
    private String uploadDir;

    public Map<String, Object> getDashboardData(Long patientId) {
        Map<String, Object> dashboard = new HashMap<>();

        List<RendezVous> prochainsRendezVous = rendezVousRepository
                .findByPatientIdAndDateHeureAfterAndStatutNot(
                        patientId,
                        LocalDateTime.now(),
                        RendezVous.StatutRendezVous.ANNULE
                );

        // Convertir en DTOs
        List<RendezVousDTO> prochainsRendezVousDTO = prochainsRendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());

        List<RendezVous> tousRendezVous = rendezVousRepository
                .findByPatientIdOrderByDateHeureDesc(patientId);

        List<RendezVousDTO> rendezVousRecentsDTO = tousRendezVous.stream()
                .limit(5)
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRendezVous", tousRendezVous.size());
        stats.put("rendezVousConfirmes", tousRendezVous.stream()
                .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.CONFIRME).count());
        stats.put("rendezVousAnnules", tousRendezVous.stream()
                .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.ANNULE).count());

        Optional<RendezVous> prochainRdv = prochainsRendezVous.stream()
                .min(Comparator.comparing(RendezVous::getDateHeure));

        if (prochainRdv.isPresent()) {
            stats.put("prochainRendezVous", prochainRdv.get().getDateHeure().toString());
        }

        dashboard.put("prochainsRendezVous", prochainsRendezVousDTO);
        dashboard.put("rendezVousRecents", rendezVousRecentsDTO);
        dashboard.put("statistiques", stats);

        return dashboard;
    }

    public List<RendezVousDTO> getRendezVousByPatient(Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByPatientIdOrderByDateHeureDesc(patientId);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public List<RendezVousDTO> getProchainsRendezVous(Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByPatientIdAndDateHeureAfterAndStatutNot(
                patientId, LocalDateTime.now(), RendezVous.StatutRendezVous.ANNULE);
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public List<RendezVousDTO> getRendezVousPasses(Long patientId) {
        List<RendezVous> rendezVous = rendezVousRepository.findByPatientIdAndDateHeureBefore(patientId, LocalDateTime.now());
        return rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientProfil(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
        return entityMapper.toPatientDTO(patient);
    }

    public PatientDTO updatePatient(Long id, Patient patientDetails) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

        if (patientDetails.getNom() != null) {
            patient.setNom(patientDetails.getNom());
        }
        if (patientDetails.getPrenom() != null) {
            patient.setPrenom(patientDetails.getPrenom());
        }
        if (patientDetails.getTelephone() != null) {
            patient.setTelephone(patientDetails.getTelephone());
        }
        if (patientDetails.getAdresse() != null) {
            patient.setAdresse(patientDetails.getAdresse());
        }
        if (patientDetails.getDateNaissance() != null) {
            patient.setDateNaissance(patientDetails.getDateNaissance());
        }
        if (patientDetails.getGroupeSanguin() != null) {
            patient.setGroupeSanguin(patientDetails.getGroupeSanguin());
        }
        if (patientDetails.getAntecedentsMedicaux() != null) {
            patient.setAntecedentsMedicaux(patientDetails.getAntecedentsMedicaux());
        }

        Patient updatedPatient = patientRepository.save(patient);
        return entityMapper.toPatientDTO(updatedPatient);
    }

    public PatientDTO updatePhotoProfil(Long id, MultipartFile photo) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));

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
            String fileName = "patient_" + id + "_" + UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(fileName);

            if (patient.getPhoto() != null && !patient.getPhoto().isEmpty()) {
                String oldFileName = patient.getPhoto();
                if (oldFileName.contains("/")) {
                    oldFileName = oldFileName.substring(oldFileName.lastIndexOf("/") + 1);
                }
                Path oldFilePath = uploadPath.resolve(oldFileName);
                try {
                    Files.deleteIfExists(oldFilePath);
                    System.out.println("Ancienne photo patient supprimée: " + oldFileName);
                } catch (IOException e) {
                    System.err.println("Impossible de supprimer l'ancienne photo: " + e.getMessage());
                }
            }

            Files.copy(photo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Nouvelle photo patient sauvegardée: " + fileName);

            String photoUrl = "/api/uploads/" + fileName;
            patient.setPhoto(photoUrl);

            Patient savedPatient = patientRepository.save(patient);
            System.out.println("Photo patient mise à jour en base: " + photoUrl);

            return entityMapper.toPatientDTO(savedPatient);

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du téléchargement de la photo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la photo de profil: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getPatientStats(Long patientId) {
        Map<String, Object> stats = new HashMap<>();

        List<RendezVous> allRendezVous = rendezVousRepository.findByPatientId(patientId);

        stats.put("totalRendezVous", allRendezVous.size());
        stats.put("rendezVousConfirmes", allRendezVous.stream()
                .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.CONFIRME).count());
        stats.put("rendezVousAnnules", allRendezVous.stream()
                .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.ANNULE).count());
        stats.put("rendezVousTermines", allRendezVous.stream()
                .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.TERMINE).count());

        long rdvFuturs = allRendezVous.stream()
                .filter(r -> r.getDateHeure().isAfter(LocalDateTime.now()))
                .count();
        stats.put("rendezVousFuturs", rdvFuturs);

        return stats;
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.service.DocteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docteurs")
@CrossOrigin("*")
public class DocteurController {

    @Autowired
    private DocteurService docteurService;

    @GetMapping
    public ResponseEntity<List<DocteurDTO>> getAllDocteurs() {
        List<DocteurDTO> docteurs = docteurService.getAllDocteurs();
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<List<DocteurDTO>> getDocteursBySpecialite(@PathVariable Long specialiteId) {
        List<DocteurDTO> docteurs = docteurService.getDocteursBySpecialite(specialiteId);
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocteurDTO> getDocteurById(@PathVariable Long id) {
        DocteurDTO docteur = docteurService.getDocteurById(id);
        return ResponseEntity.ok(docteur);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocteurDTO>> searchDocteurs(@RequestParam String q) {
        List<DocteurDTO> docteurs = docteurService.searchDocteurs(q);
        return ResponseEntity.ok(docteurs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocteurDTO> updateDocteur(@PathVariable Long id, @RequestBody DocteurUpdateDTO docteurDetails) {
        try {
            DocteurDTO updatedDocteur = docteurService.updateDocteur(id, docteurDetails);
            return ResponseEntity.ok(updatedDocteur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocteur(@PathVariable Long id) {
        try {
            docteurService.deleteDocteur(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/actifs")
    public ResponseEntity<List<DocteurDTO>> getDocteursActifs() {
        List<DocteurDTO> docteurs = docteurService.getDocteursActifs();
        return ResponseEntity.ok(docteurs);
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity<DocteurDTO> updatePhotoProfil(
            @PathVariable Long id,
            @RequestParam("photo") MultipartFile photo) {
        try {
            DocteurDTO updatedDocteur = docteurService.updatePhotoProfil(id, photo);
            return ResponseEntity.ok(updatedDocteur);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/profil")
    public ResponseEntity<DocteurDTO> getMonProfil(@PathVariable Long id) {
        try {
            DocteurDTO docteur = docteurService.getDocteurById(id);
            return ResponseEntity.ok(docteur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/patients")
    public ResponseEntity<List<PatientDTO>> getMesPatients(@PathVariable Long docteurId) {
        try {
            List<PatientDTO> patients = docteurService.getPatientsByDocteur(docteurId);
            return ResponseEntity.ok(patients);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/patients/nouveaux")
    public ResponseEntity<List<PatientDTO>> getNouveauxPatients(@PathVariable Long docteurId) {
        try {
            List<PatientDTO> patients = docteurService.getNouveauxPatients(docteurId);
            return ResponseEntity.ok(patients);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/patients/{patientId}")
    public ResponseEntity<PatientDTO> getPatientDetails(
            @PathVariable Long docteurId,
            @PathVariable Long patientId) {
        try {
            PatientDTO patient = docteurService.getPatientDetails(docteurId, patientId);
            return ResponseEntity.ok(patient);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/patients/{patientId}/historique")
    public ResponseEntity<List<RendezVousDTO>> getHistoriquePatient(
            @PathVariable Long docteurId,
            @PathVariable Long patientId) {
        try {
            List<RendezVousDTO> historique = docteurService.getHistoriquePatient(docteurId, patientId);
            return ResponseEntity.ok(historique);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/patients/recherche")
    public ResponseEntity<List<PatientDTO>> rechercherPatients(
            @PathVariable Long docteurId,
            @RequestParam String q) {
        try {
            List<PatientDTO> patients = docteurService.rechercherPatients(docteurId, q);
            return ResponseEntity.ok(patients);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/statistiques")
    public ResponseEntity<Map<String, Object>> getStatistiquesDocteur(@PathVariable Long docteurId) {
        try {
            Map<String, Object> statistiques = docteurService.getStatistiquesDocteur(docteurId);
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/statistiques/mensuelles")
    public ResponseEntity<Map<String, Object>> getStatistiquesMensuelles(
            @PathVariable Long docteurId,
            @RequestParam int annee,
            @RequestParam int mois) {
        try {
            Map<String, Object> statistiques = docteurService.getStatistiquesMensuelles(docteurId, annee, mois);
            return ResponseEntity.ok(statistiques);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/statistiques/evolution")
    public ResponseEntity<Map<String, Object>> getEvolutionRendezVous(
            @PathVariable Long docteurId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            LocalDateTime debut = LocalDateTime.parse(dateDebut + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(dateFin + "T23:59:59");

            Map<String, Object> evolution = docteurService.getEvolutionRendezVous(docteurId, debut, fin);
            return ResponseEntity.ok(evolution);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/agenda")
    public ResponseEntity<Map<String, Object>> getAgenda(
            @PathVariable Long docteurId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            LocalDateTime debut = LocalDateTime.parse(dateDebut + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(dateFin + "T23:59:59");

            Map<String, Object> agenda = docteurService.getAgenda(docteurId, debut, fin);
            return ResponseEntity.ok(agenda);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/agenda/date/{date}")
    public ResponseEntity<List<RendezVousDTO>> getEvenementsAgenda(
            @PathVariable Long docteurId,
            @PathVariable String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime debut = localDate.atStartOfDay();
            LocalDateTime fin = localDate.atTime(23, 59, 59);

            List<RendezVousDTO> evenements = docteurService.getEvenementsAgenda(docteurId, debut, fin);
            return ResponseEntity.ok(evenements);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/rapports/activite")
    public ResponseEntity<byte[]> genererRapportActivite(
            @PathVariable Long docteurId,
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {
        try {
            LocalDateTime debut = LocalDateTime.parse(dateDebut + "T00:00:00");
            LocalDateTime fin = LocalDateTime.parse(dateFin + "T23:59:59");

            byte[] rapport = docteurService.genererRapportActivite(docteurId, debut, fin);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"rapport-activite-" + docteurId + ".pdf\"")
                    .body(rapport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/rapports/financier")
    public ResponseEntity<byte[]> genererRapportFinancier(
            @PathVariable Long docteurId,
            @RequestParam int annee,
            @RequestParam int mois) {
        try {
            byte[] rapport = docteurService.genererRapportFinancier(docteurId, annee, mois);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"rapport-financier-" + docteurId + "-" + annee + "-" + mois + ".pdf\"")
                    .body(rapport);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{docteurId}/verifier-conflit")
    public ResponseEntity<Boolean> verifierConflitRendezVous(
            @PathVariable Long docteurId,
            @RequestParam String dateHeure,
            @RequestParam(defaultValue = "30") int duree) {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateHeure);
            boolean conflit = docteurService.verifierConflitRendezVous(docteurId, dateTime, duree);
            return ResponseEntity.ok(conflit);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{docteurId}/sante")
    public ResponseEntity<Map<String, String>> checkHealth(@PathVariable Long docteurId) {
        Map<String, String> response = new HashMap<>();
        try {
            DocteurDTO docteur = docteurService.getDocteurById(docteurId);
            response.put("status", "OK");
            response.put("message", "Docteur " + docteur.getNom() + " " + docteur.getPrenom() + " est actif");
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "ERROR");
            response.put("message", "Docteur non trouvé");
            return ResponseEntity.notFound().build();
        }
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.PatientDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.model.Patient;
import com.example.gestiondesrendezvousmedicauxbackend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin("*")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<?> getDashboardData(@PathVariable Long id) {
        try {
            Map<String, Object> dashboard = patientService.getDashboardData(id);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/profil")
    public ResponseEntity<?> getProfilPatient(@PathVariable Long id) {
        try {
            PatientDTO patientProfil = patientService.getPatientProfil(id);
            return ResponseEntity.ok(patientProfil);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/profil")
    public ResponseEntity<?> updateProfilPatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        try {
            PatientDTO updatedPatient = patientService.updatePatient(id, patientDetails);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity<?> updatePhotoProfil(@PathVariable Long id, @RequestParam("photo") MultipartFile photo) {
        try {
            PatientDTO updatedPatient = patientService.updatePhotoProfil(id, photo);
            return ResponseEntity.ok(updatedPatient);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/rendezvous")
    public ResponseEntity<?> getRendezVousPatient(@PathVariable Long id) {
        try {
            List<RendezVousDTO> rendezVous = patientService.getRendezVousByPatient(id);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/rendezvous/prochains")
    public ResponseEntity<?> getProchainsRendezVous(@PathVariable Long id) {
        try {
            List<RendezVousDTO> rendezVous = patientService.getProchainsRendezVous(id);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/rendezvous/passes")
    public ResponseEntity<?> getRendezVousPasses(@PathVariable Long id) {
        try {
            List<RendezVousDTO> rendezVous = patientService.getRendezVousPasses(id);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<?> getPatientStats(@PathVariable Long id) {
        try {
            Map<String, Object> stats = patientService.getPatientStats(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
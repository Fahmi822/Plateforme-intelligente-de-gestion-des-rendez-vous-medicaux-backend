package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousCreationDTO;
import com.example.gestiondesrendezvousmedicauxbackend.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendezvous")
@CrossOrigin("*")
public class RendezVousController {

    @Autowired
    private RendezVousService rendezVousService;

    @PostMapping
    public ResponseEntity<?> createRendezVous(@RequestBody RendezVousCreationDTO rendezVousDTO) {
        try {
            RendezVousDTO rendezVous = rendezVousService.createRendezVous(rendezVousDTO);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousPatient(@PathVariable Long patientId) {
        List<RendezVousDTO> rendezVous = rendezVousService.getRendezVousByPatient(patientId);
        return ResponseEntity.ok(rendezVous);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerRendezVous(@PathVariable Long id, @RequestParam Long patientId) {
        try {
            RendezVousDTO rendezVous = rendezVousService.annulerRendezVous(id, patientId);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<?> confirmerRendezVous(@PathVariable Long id, @RequestParam Long patientId) {
        try {
            RendezVousDTO rendezVous = rendezVousService.confirmerRendezVous(id, patientId);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/docteur/{docteurId}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousDocteur(@PathVariable Long docteurId) {
        try {
            List<RendezVousDTO> rendezVous = rendezVousService.getRendezVousByDocteur(docteurId);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/docteur/{docteurId}/date/{date}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousDocteurByDate(
            @PathVariable Long docteurId,
            @PathVariable String date) {
        try {
            List<RendezVousDTO> rendezVous = rendezVousService.getRendezVousByDocteurAndDate(docteurId, date);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/docteur/{docteurId}/statut/{statut}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousDocteurByStatut(
            @PathVariable Long docteurId,
            @PathVariable String statut) {
        try {
            List<RendezVousDTO> rendezVous = rendezVousService.getRendezVousByDocteurAndStatut(docteurId, statut);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/terminer")
    public ResponseEntity<?> terminerRendezVous(@PathVariable Long id) {
        try {
            RendezVousDTO rendezVous = rendezVousService.terminerRendezVous(id);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
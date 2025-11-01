package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.RendezVousDTO;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
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
    public ResponseEntity<?> createRendezVous(@RequestBody RendezVousDTO rendezVousDTO) {
        try {
            RendezVous rendezVous = rendezVousService.createRendezVous(rendezVousDTO);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVous>> getRendezVousPatient(@PathVariable Long patientId) {
        List<RendezVous> rendezVous = rendezVousService.getRendezVousByPatient(patientId);
        return ResponseEntity.ok(rendezVous);
    }

    @PutMapping("/{id}/annuler")
    public ResponseEntity<?> annulerRendezVous(@PathVariable Long id, @RequestParam Long patientId) {
        try {
            RendezVous rendezVous = rendezVousService.annulerRendezVous(id, patientId);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/confirmer")
    public ResponseEntity<?> confirmerRendezVous(@PathVariable Long id, @RequestParam Long patientId) {
        try {
            RendezVous rendezVous = rendezVousService.confirmerRendezVous(id, patientId);
            return ResponseEntity.ok(rendezVous);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
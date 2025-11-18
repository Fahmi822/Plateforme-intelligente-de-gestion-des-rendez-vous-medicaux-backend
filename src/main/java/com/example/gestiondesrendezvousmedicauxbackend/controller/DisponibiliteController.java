package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.DisponibiliteDTO;
import com.example.gestiondesrendezvousmedicauxbackend.dto.DisponibiliteCreationDTO;
import com.example.gestiondesrendezvousmedicauxbackend.service.DisponibiliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
@CrossOrigin("*")
public class DisponibiliteController {

    @Autowired
    private DisponibiliteService disponibiliteService;

    @GetMapping("/docteur/{docteurId}/disponible")
    public ResponseEntity<Boolean> estDocteurDisponible(
            @PathVariable Long docteurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure) {

        boolean disponible = disponibiliteService.estDocteurDisponible(docteurId, dateHeure);
        return ResponseEntity.ok(disponible);
    }

    @GetMapping("/docteur/{docteurId}/creneaux")
    public ResponseEntity<List<LocalDateTime>> getCreneauxDisponibles(
            @PathVariable Long docteurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFin) {

        List<LocalDateTime> creneaux = disponibiliteService.getCreneauxDisponibles(docteurId, dateDebut, dateFin);
        return ResponseEntity.ok(creneaux);
    }

    @GetMapping("/docteur/{docteurId}")
    public ResponseEntity<List<DisponibiliteDTO>> getDisponibilitesDocteur(@PathVariable Long docteurId) {
        List<DisponibiliteDTO> disponibilites = disponibiliteService.getProchainesDisponibilites(docteurId, 30);
        return ResponseEntity.ok(disponibilites);
    }

    @PostMapping
    public ResponseEntity<?> ajouterDisponibilite(@RequestBody DisponibiliteCreationDTO disponibiliteDTO) {
        try {
            DisponibiliteDTO disponibilite = disponibiliteService.ajouterDisponibilite(disponibiliteDTO);
            return ResponseEntity.ok(disponibilite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/indisponible")
    public ResponseEntity<?> ajouterIndisponibilite(@RequestBody DisponibiliteCreationDTO disponibiliteDTO) {
        try {
            DisponibiliteDTO indisponibilite = disponibiliteService.ajouterIndisponibilite(disponibiliteDTO);
            return ResponseEntity.ok(indisponibilite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/docteur/{docteurId}/generer-semaine")
    public ResponseEntity<?> genererDisponibilitesSemaine(
            @PathVariable Long docteurId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateDebutSemaine) {

        try {
            List<DisponibiliteDTO> disponibilites = disponibiliteService
                    .genererDisponibilitesSemaine(docteurId, dateDebutSemaine);
            return ResponseEntity.ok(disponibilites);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> supprimerDisponibilite(@PathVariable Long id) {
        try {
            disponibiliteService.supprimerDisponibilite(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.model.Docteur;
import com.example.gestiondesrendezvousmedicauxbackend.service.DocteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docteurs")
@CrossOrigin("*")
public class DocteurController {

    @Autowired
    private DocteurService docteurService;

    @GetMapping
    public ResponseEntity<List<Docteur>> getAllDocteurs() {
        List<Docteur> docteurs = docteurService.getAllDocteurs();
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/specialite/{specialiteId}")
    public ResponseEntity<List<Docteur>> getDocteursBySpecialite(@PathVariable Long specialiteId) {
        List<Docteur> docteurs = docteurService.getDocteursBySpecialite(specialiteId);
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Docteur> getDocteurById(@PathVariable Long id) {
        Docteur docteur = docteurService.getDocteurById(id);
        return ResponseEntity.ok(docteur);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Docteur>> searchDocteurs(@RequestParam String q) {
        List<Docteur> docteurs = docteurService.searchDocteurs(q);
        return ResponseEntity.ok(docteurs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Docteur> updateDocteur(@PathVariable Long id, @RequestBody Docteur docteurDetails) {
        try {
            Docteur updatedDocteur = docteurService.updateDocteur(id, docteurDetails);
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
    public ResponseEntity<List<Docteur>> getDocteursActifs() {
        List<Docteur> docteurs = docteurService.getDocteursActifs();
        return ResponseEntity.ok(docteurs);
    }
}
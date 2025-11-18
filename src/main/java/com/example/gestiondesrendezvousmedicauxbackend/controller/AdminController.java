package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.*;
import com.example.gestiondesrendezvousmedicauxbackend.mapper.EntityMapper;
import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import com.example.gestiondesrendezvousmedicauxbackend.model.RendezVous;
import com.example.gestiondesrendezvousmedicauxbackend.model.*;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DocteurRepository docteurRepository;

    @Autowired
    private RendezVousRepository rendezVousRepository;

    @Autowired
    private SpecialiteRepository specialiteRepository;

    @Autowired
    private EntityMapper entityMapper;

    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesGlobales> getStatistiques() {
        StatistiquesGlobales stats = new StatistiquesGlobales();

        stats.setTotalUtilisateurs(utilisateurRepository.count());
        stats.setTotalPatients(patientRepository.count());
        stats.setTotalDocteurs(docteurRepository.count());
        stats.setTotalRendezVous(rendezVousRepository.count());

        LocalDateTime aujourdhuiDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime aujourdhuiFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        stats.setRendezVousAujourdhui(rendezVousRepository.countByDateHeureBetween(aujourdhuiDebut, aujourdhuiFin));

        stats.setRevenuMensuel(calculerRevenuMensuel());
        stats.setTauxOccupation(calculerTauxOccupation());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistiques/avancees")
    public ResponseEntity<Map<String, Object>> getStatistiquesAvancees() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("utilisateursActifs", utilisateurRepository.countByActifTrue());
        stats.put("utilisateursInactifs", utilisateurRepository.countByActifFalse());
        stats.put("nouveauxUtilisateursMois", utilisateurRepository.findUtilisateursRecents(
                LocalDateTime.now().minusMonths(1)).size());

        List<Object[]> statsRendezVous = rendezVousRepository.countByStatutGroupByStatut();
        Map<String, Long> rendezVousParStatut = new HashMap<>();
        for (Object[] stat : statsRendezVous) {
            rendezVousParStatut.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("rendezVousParStatut", rendezVousParStatut);

        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Object[]> docteursActifs = rendezVousRepository.findDocteursPlusActifs(debutMois, LocalDateTime.now());
        stats.put("docteursPlusActifs", docteursActifs);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        List<UtilisateurDTO> utilisateurDTOs = utilisateurs.stream()
                .map(entityMapper::toUtilisateurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(utilisateurDTOs);
    }

    @GetMapping("/utilisateurs/role/{role}")
    public ResponseEntity<List<UtilisateurDTO>> getUtilisateursByRole(@PathVariable String role) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByRole(role);
        List<UtilisateurDTO> utilisateurDTOs = utilisateurs.stream()
                .map(entityMapper::toUtilisateurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(utilisateurDTOs);
    }

    @GetMapping("/utilisateurs/actifs")
    public ResponseEntity<List<UtilisateurDTO>> getUtilisateursActifs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByActifTrue();
        List<UtilisateurDTO> utilisateurDTOs = utilisateurs.stream()
                .map(entityMapper::toUtilisateurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(utilisateurDTOs);
    }

    @GetMapping("/utilisateurs/recherche")
    public ResponseEntity<List<UtilisateurDTO>> searchUtilisateurs(@RequestParam String q) {
        List<Utilisateur> utilisateurs = utilisateurRepository.searchUtilisateurs(q);
        List<UtilisateurDTO> utilisateurDTOs = utilisateurs.stream()
                .map(entityMapper::toUtilisateurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(utilisateurDTOs);
    }

    @PatchMapping("/utilisateurs/{id}/toggle-actif")
    public ResponseEntity<UtilisateurDTO> toggleUtilisateurActif(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // CORRECTION : Utiliser les méthodes correctes
        utilisateur.setActif(!utilisateur.isActif());
        utilisateur.setDateModification(LocalDateTime.now());

        Utilisateur updated = utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(entityMapper.toUtilisateurDTO(updated));
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // CORRECTION : Utiliser getRole() correctement
        if ("ADMIN".equals(utilisateur.getRole())) {
            throw new RuntimeException("Impossible de supprimer un administrateur");
        }

        utilisateurRepository.delete(utilisateur);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patients")
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientDTO> patientDTOs = patients.stream()
                .map(entityMapper::toPatientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(patientDTOs);
    }

    @GetMapping("/patients/actifs")
    public ResponseEntity<List<PatientDTO>> getPatientsActifs() {
        List<Patient> patients = patientRepository.findByActifTrue();
        List<PatientDTO> patientDTOs = patients.stream()
                .map(entityMapper::toPatientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(patientDTOs);
    }

    @GetMapping("/patients/recherche")
    public ResponseEntity<List<PatientDTO>> searchPatients(@RequestParam String q) {
        List<Patient> patients = patientRepository.searchPatients(q);
        List<PatientDTO> patientDTOs = patients.stream()
                .map(entityMapper::toPatientDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(patientDTOs);
    }

    @GetMapping("/docteurs")
    public ResponseEntity<List<DocteurDTO>> getAllDocteurs() {
        List<Docteur> docteurs = docteurRepository.findAll();
        List<DocteurDTO> docteurDTOs = docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docteurDTOs);
    }

    @GetMapping("/docteurs/actifs")
    public ResponseEntity<List<DocteurDTO>> getDocteursActifs() {
        List<Docteur> docteurs = docteurRepository.findByActifTrue();
        List<DocteurDTO> docteurDTOs = docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docteurDTOs);
    }

    @GetMapping("/docteurs/meilleurs")
    public ResponseEntity<List<DocteurDTO>> getMeilleursDocteurs() {
        List<Docteur> docteurs = docteurRepository.findByNoteMoyenneGreaterThanEqual(4.0);
        List<DocteurDTO> docteurDTOs = docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docteurDTOs);
    }

    @GetMapping("/docteurs/recherche")
    public ResponseEntity<List<DocteurDTO>> searchDocteurs(@RequestParam String q) {
        List<Docteur> docteurs = docteurRepository.searchDocteurs(q);
        List<DocteurDTO> docteurDTOs = docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docteurDTOs);
    }

    @GetMapping("/rendezvous")
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous() {
        List<RendezVous> rendezVous = rendezVousRepository.findAll();
        List<RendezVousDTO> rendezVousDTOs = rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rendezVousDTOs);
    }

    @GetMapping("/rendezvous/recent")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousRecent() {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(7);
        List<RendezVous> rendezVous = rendezVousRepository.findByDateCreationAfter(dateLimite);
        List<RendezVousDTO> rendezVousDTOs = rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rendezVousDTOs);
    }

    @GetMapping("/rendezvous/periode")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousPeriode(
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {

        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);

        List<RendezVous> rendezVous = rendezVousRepository.findByPeriode(debut, fin);
        List<RendezVousDTO> rendezVousDTOs = rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rendezVousDTOs);
    }

    @GetMapping("/rendezvous/recherche")
    public ResponseEntity<List<RendezVousDTO>> searchRendezVous(@RequestParam String q) {
        List<RendezVous> rendezVous = rendezVousRepository.searchRendezVous(q);
        List<RendezVousDTO> rendezVousDTOs = rendezVous.stream()
                .map(entityMapper::toRendezVousDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rendezVousDTOs);
    }

    @GetMapping("/rendezvous/statistiques/mensuelles")
    public ResponseEntity<List<Object[]>> getStatistiquesMensuelles(
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {

        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);

        List<Object[]> stats = rendezVousRepository.findStatistiquesMensuelles(debut, fin);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/specialites")
    public ResponseEntity<List<SpecialiteDTO>> getAllSpecialites() {
        List<Specialite> specialites = specialiteRepository.findAll();
        List<SpecialiteDTO> specialiteDTOs = specialites.stream()
                .map(entityMapper::toSpecialiteDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(specialiteDTOs);
    }

    @GetMapping("/specialites/{id}/docteurs")
    public ResponseEntity<List<DocteurDTO>> getDocteursBySpecialite(@PathVariable Long id) {
        List<Docteur> docteurs = docteurRepository.findBySpecialiteId(id);
        List<DocteurDTO> docteurDTOs = docteurs.stream()
                .map(entityMapper::toDocteurDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(docteurDTOs);
    }

    private double calculerRevenuMensuel() {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        long rdvMois = rendezVousRepository.countByDateHeureBetween(debutMois, finMois);
        return rdvMois * 50.0;
    }

    private double calculerTauxOccupation() {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        long rdvMois = rendezVousRepository.countByDateHeureBetween(debutMois, finMois);
        long docteursActifs = docteurRepository.countByActifTrue();

        long rdvPossibles = docteursActifs * 20;
        return rdvPossibles > 0 ? (rdvMois * 100.0 / rdvPossibles) : 0;
    }
}
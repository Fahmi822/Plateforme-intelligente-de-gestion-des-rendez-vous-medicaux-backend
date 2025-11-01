package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.StatistiquesGlobales;
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

    // ==================== STATISTIQUES ====================

    @GetMapping("/statistiques")
    public ResponseEntity<StatistiquesGlobales> getStatistiques() {
        StatistiquesGlobales stats = new StatistiquesGlobales();

        stats.setTotalUtilisateurs(utilisateurRepository.count());
        stats.setTotalPatients(patientRepository.count());
        stats.setTotalDocteurs(docteurRepository.count());
        stats.setTotalRendezVous(rendezVousRepository.count());

        // Rendez-vous d'aujourd'hui
        LocalDateTime aujourdhuiDebut = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime aujourdhuiFin = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        stats.setRendezVousAujourdhui(rendezVousRepository.countByDateHeureBetween(aujourdhuiDebut, aujourdhuiFin));

        // Calculs avancés
        stats.setRevenuMensuel(calculerRevenuMensuel());
        stats.setTauxOccupation(calculerTauxOccupation());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/statistiques/avancees")
    public ResponseEntity<Map<String, Object>> getStatistiquesAvancees() {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques utilisateurs
        stats.put("utilisateursActifs", utilisateurRepository.countByActifTrue());
        stats.put("utilisateursInactifs", utilisateurRepository.countByActifFalse());
        stats.put("nouveauxUtilisateursMois", utilisateurRepository.findUtilisateursRecents(
                LocalDateTime.now().minusMonths(1)).size());

        // Statistiques rendez-vous par statut
        List<Object[]> statsRendezVous = rendezVousRepository.countByStatutGroupByStatut();
        Map<String, Long> rendezVousParStatut = new HashMap<>();
        for (Object[] stat : statsRendezVous) {
            rendezVousParStatut.put(stat[0].toString(), (Long) stat[1]);
        }
        stats.put("rendezVousParStatut", rendezVousParStatut);

        // Docteurs les plus actifs
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        List<Object[]> docteursActifs = rendezVousRepository.findDocteursPlusActifs(debutMois, LocalDateTime.now());
        stats.put("docteursPlusActifs", docteursActifs);

        return ResponseEntity.ok(stats);
    }

    // ==================== GESTION UTILISATEURS ====================

    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/role/{role}")
    public ResponseEntity<List<Utilisateur>> getUtilisateursByRole(@PathVariable String role) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByRole(role);
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/actifs")
    public ResponseEntity<List<Utilisateur>> getUtilisateursActifs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByActifTrue();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/utilisateurs/recherche")
    public ResponseEntity<List<Utilisateur>> searchUtilisateurs(@RequestParam String q) {
        List<Utilisateur> utilisateurs = utilisateurRepository.searchUtilisateurs(q);
        return ResponseEntity.ok(utilisateurs);
    }

    @PatchMapping("/utilisateurs/{id}/toggle-actif")
    public ResponseEntity<Utilisateur> toggleUtilisateurActif(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setActif(!utilisateur.isActif());
        Utilisateur updated = utilisateurRepository.save(utilisateur);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Empêcher la suppression des admins
        if ("ADMIN".equals(utilisateur.getRole())) {
            throw new RuntimeException("Impossible de supprimer un administrateur");
        }

        utilisateurRepository.delete(utilisateur);
        return ResponseEntity.ok().build();
    }

    // ==================== GESTION PATIENTS ====================

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patients/actifs")
    public ResponseEntity<List<Patient>> getPatientsActifs() {
        List<Patient> patients = patientRepository.findByActifTrue();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patients/recherche")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam String q) {
        List<Patient> patients = patientRepository.searchPatients(q);
        return ResponseEntity.ok(patients);
    }

    // ==================== GESTION DOCTEURS ====================

    @GetMapping("/docteurs")
    public ResponseEntity<List<Docteur>> getAllDocteurs() {
        List<Docteur> docteurs = docteurRepository.findAll();
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/docteurs/actifs")
    public ResponseEntity<List<Docteur>> getDocteursActifs() {
        List<Docteur> docteurs = docteurRepository.findByActifTrue();
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/docteurs/meilleurs")
    public ResponseEntity<List<Docteur>> getMeilleursDocteurs() {
        List<Docteur> docteurs = docteurRepository.findByNoteMoyenneGreaterThanEqual(4.0);
        return ResponseEntity.ok(docteurs);
    }

    @GetMapping("/docteurs/recherche")
    public ResponseEntity<List<Docteur>> searchDocteurs(@RequestParam String q) {
        List<Docteur> docteurs = docteurRepository.searchDocteurs(q);
        return ResponseEntity.ok(docteurs);
    }

    // ==================== GESTION RENDEZ-VOUS ====================

    @GetMapping("/rendezvous")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        List<RendezVous> rendezVous = rendezVousRepository.findAll();
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/rendezvous/recent")
    public ResponseEntity<List<RendezVous>> getRendezVousRecent() {
        LocalDateTime dateLimite = LocalDateTime.now().minusDays(7);
        List<RendezVous> rendezVous = rendezVousRepository.findByDateCreationAfter(dateLimite);
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/rendezvous/periode")
    public ResponseEntity<List<RendezVous>> getRendezVousPeriode(
            @RequestParam String dateDebut,
            @RequestParam String dateFin) {

        LocalDateTime debut = LocalDateTime.parse(dateDebut);
        LocalDateTime fin = LocalDateTime.parse(dateFin);

        List<RendezVous> rendezVous = rendezVousRepository.findByPeriode(debut, fin);
        return ResponseEntity.ok(rendezVous);
    }

    @GetMapping("/rendezvous/recherche")
    public ResponseEntity<List<RendezVous>> searchRendezVous(@RequestParam String q) {
        List<RendezVous> rendezVous = rendezVousRepository.searchRendezVous(q);
        return ResponseEntity.ok(rendezVous);
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

    // ==================== GESTION SPÉCIALITÉS ====================

    @GetMapping("/specialites")
    public ResponseEntity<List<Specialite>> getAllSpecialites() {
        List<Specialite> specialites = specialiteRepository.findAll();
        return ResponseEntity.ok(specialites);
    }

    @GetMapping("/specialites/{id}/docteurs")
    public ResponseEntity<List<Docteur>> getDocteursBySpecialite(@PathVariable Long id) {
        List<Docteur> docteurs = docteurRepository.findBySpecialiteId(id);
        return ResponseEntity.ok(docteurs);
    }

    // ==================== MÉTHODES PRIVÉES ====================

    private double calculerRevenuMensuel() {
        // Calcul basé sur les rendez-vous du mois en cours
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        long rdvMois = rendezVousRepository.countByDateHeureBetween(debutMois, finMois);

        // Tarif moyen de 50€ par consultation
        return rdvMois * 50.0;
    }

    private double calculerTauxOccupation() {
        LocalDateTime debutMois = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMois = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);

        long rdvMois = rendezVousRepository.countByDateHeureBetween(debutMois, finMois);
        long docteursActifs = docteurRepository.countByActifTrue();

        // Estimation: 20 rendez-vous possibles par docteur par mois
        long rdvPossibles = docteursActifs * 20;

        return rdvPossibles > 0 ? (rdvMois * 100.0 / rdvPossibles) : 0;
    }
}
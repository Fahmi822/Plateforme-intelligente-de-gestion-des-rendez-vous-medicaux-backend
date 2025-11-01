package com.example.gestiondesrendezvousmedicauxbackend.dto;

public class StatistiquesGlobales {
    private long totalUtilisateurs;
    private long totalPatients;
    private long totalDocteurs;
    private long totalRendezVous;
    private long rendezVousAujourdhui;
    private double revenuMensuel;
    private double tauxOccupation;

    // Constructeurs
    public StatistiquesGlobales() {}

    public StatistiquesGlobales(long totalUtilisateurs, long totalPatients, long totalDocteurs,
                                long totalRendezVous, long rendezVousAujourdhui,
                                double revenuMensuel, double tauxOccupation) {
        this.totalUtilisateurs = totalUtilisateurs;
        this.totalPatients = totalPatients;
        this.totalDocteurs = totalDocteurs;
        this.totalRendezVous = totalRendezVous;
        this.rendezVousAujourdhui = rendezVousAujourdhui;
        this.revenuMensuel = revenuMensuel;
        this.tauxOccupation = tauxOccupation;
    }

    // Getters et Setters
    public long getTotalUtilisateurs() { return totalUtilisateurs; }
    public void setTotalUtilisateurs(long totalUtilisateurs) { this.totalUtilisateurs = totalUtilisateurs; }

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalDocteurs() { return totalDocteurs; }
    public void setTotalDocteurs(long totalDocteurs) { this.totalDocteurs = totalDocteurs; }

    public long getTotalRendezVous() { return totalRendezVous; }
    public void setTotalRendezVous(long totalRendezVous) { this.totalRendezVous = totalRendezVous; }

    public long getRendezVousAujourdhui() { return rendezVousAujourdhui; }
    public void setRendezVousAujourdhui(long rendezVousAujourdhui) { this.rendezVousAujourdhui = rendezVousAujourdhui; }

    public double getRevenuMensuel() { return revenuMensuel; }
    public void setRevenuMensuel(double revenuMensuel) { this.revenuMensuel = revenuMensuel; }

    public double getTauxOccupation() { return tauxOccupation; }
    public void setTauxOccupation(double tauxOccupation) { this.tauxOccupation = tauxOccupation; }
}
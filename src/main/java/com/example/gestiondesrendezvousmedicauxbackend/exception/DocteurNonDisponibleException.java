package com.example.gestiondesrendezvousmedicauxbackend.exception;

import java.time.LocalDateTime;
import java.util.List;

public class DocteurNonDisponibleException extends RuntimeException {
    private List<LocalDateTime> creneauxDisponibles;

    public DocteurNonDisponibleException(String message, List<LocalDateTime> creneauxDisponibles) {
        super(message);
        this.creneauxDisponibles = creneauxDisponibles;
    }

    public List<LocalDateTime> getCreneauxDisponibles() {
        return creneauxDisponibles;
    }
}
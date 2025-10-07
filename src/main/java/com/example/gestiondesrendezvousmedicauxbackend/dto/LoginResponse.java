package com.example.gestiondesrendezvousmedicauxbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de réponse renvoyé après une connexion réussie.
 * Contient le token JWT, le rôle et un message.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String role;
    private String message;
    public LoginResponse(String message, String token, String role) {
        this.message = message;
        this.token = token;
        this.role = role;
    }
}

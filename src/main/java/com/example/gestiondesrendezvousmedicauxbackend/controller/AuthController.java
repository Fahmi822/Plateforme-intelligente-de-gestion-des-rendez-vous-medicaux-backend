package com.example.gestiondesrendezvousmedicauxbackend.controller;

import com.example.gestiondesrendezvousmedicauxbackend.dto.DocteurSignupRequest;
import com.example.gestiondesrendezvousmedicauxbackend.dto.LoginRequest;
import com.example.gestiondesrendezvousmedicauxbackend.dto.LoginResponse;
import com.example.gestiondesrendezvousmedicauxbackend.dto.SignupRequest;
import com.example.gestiondesrendezvousmedicauxbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        try {
            String result = authService.signup(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/signup/docteur")
    public ResponseEntity<?> signupDocteur(@RequestBody DocteurSignupRequest request) {
        try {
            // S'assurer que le rôle est bien DOCTEUR
            request.setRole("DOCTEUR");

            String result = authService.signupDocteur(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
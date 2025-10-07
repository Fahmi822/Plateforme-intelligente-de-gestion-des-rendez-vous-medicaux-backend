package com.example.gestiondesrendezvousmedicauxbackend.security;

import com.example.gestiondesrendezvousmedicauxbackend.model.Utilisateur;
import com.example.gestiondesrendezvousmedicauxbackend.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .roles(utilisateur.getRole()) // "ADMIN", "DOCTEUR", "PATIENT"
                .build();
    }
}

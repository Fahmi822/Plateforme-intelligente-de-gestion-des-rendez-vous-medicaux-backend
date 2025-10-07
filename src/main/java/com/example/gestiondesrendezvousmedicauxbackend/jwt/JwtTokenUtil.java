package com.example.gestiondesrendezvousmedicauxbackend.jwt;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenUtil {

    private final String jwtSecret = "cleSecreteJWT123"; // ⚠️ à sécuriser plus tard
    private final long jwtExpirationMs = 86400000; // 24h

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getEmailFromJwt(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            System.out.println("JWT invalide : " + e.getMessage());
        }
        return false;
    }
}

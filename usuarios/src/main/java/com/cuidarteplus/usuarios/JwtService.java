package com.cuidarteplus.usuarios;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final SecretKey key;
    public JwtService(@Value("${app.jwt.secret}") String secret) {
        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes");
        }
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String crear(long usuarioId) {
        Instant ahora = Instant.now();
        return Jwts.builder().subject(Long.toString(usuarioId)).issuedAt(Date.from(ahora))
            .expiration(Date.from(ahora.plusSeconds(7200))).signWith(key).compact();
    }
}

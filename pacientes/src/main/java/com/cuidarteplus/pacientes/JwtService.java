package com.cuidarteplus.pacientes;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    public long validar(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }
        try {
            String sub = Jwts.parser().verifyWith(key).build().parseSignedClaims(authorization.substring(7)).getPayload().getSubject();
            return Long.parseLong(sub);
        } catch (RuntimeException exc) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
    }
}

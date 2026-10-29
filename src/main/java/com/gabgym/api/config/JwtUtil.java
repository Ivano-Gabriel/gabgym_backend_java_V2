package com.gabgym.api.config;

import com.gabgym.api.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/** JWT HMAC: segredo base64 gerado fora do Git, expiração curta e versão revogável. */
@Component
public class JwtUtil {
    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(@Value("${jwt.secret}") String base64Secret,
                   @Value("${jwt.expiration-ms:7200000}") long expirationMs) {
        try {
            byte[] raw = Decoders.BASE64.decode(base64Secret);
            if (raw.length < 32 || expirationMs < 60_000 || expirationMs > 86_400_000) {
                throw new IllegalArgumentException("Configuração JWT inválida");
            }
            this.key = Keys.hmacShaKeyFor(raw);
            this.expirationMs = expirationMs;
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("JWT_SECRET deve ser uma chave forte em base64 (mín. 32 bytes)", ex);
        }
    }

    public String generateToken(User user) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .setSubject(user.getUsername())
            .claim("sv", user.getSessionVersion())
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(now + expirationMs))
            .signWith(key)
            .compact();
    }

    private Claims claims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            return claims(token).getSubject() != null;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isSessionCurrent(String token, User user) {
        try {
            Claims claims = claims(token);
            Integer version = claims.get("sv", Integer.class);
            return user.isActive() && user.getUsername().equals(claims.getSubject())
                && version != null && version.intValue() == user.getSessionVersion();
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }
}

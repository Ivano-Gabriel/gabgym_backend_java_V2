package com.gabgym.api.config;

import com.gabgym.api.entities.User;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private static final String SECRET = Base64.getEncoder().encodeToString(
        "this-is-a-unit-test-secret-of-48-bytes-or-more-123456".getBytes(StandardCharsets.UTF_8));

    @Test void aceitaSomenteTokenValidoEComVersaoDaSessao() {
        JwtUtil jwt = new JwtUtil(SECRET, 3_600_000L);
        User user = new User();
        user.setId(21L);
        user.setUsername("ana");
        user.setSessionVersion(2);
        String token = jwt.generateToken(user);
        assertTrue(jwt.isTokenValid(token));
        assertEquals("ana", jwt.extractUsername(token));
        assertTrue(jwt.isSessionCurrent(token, user));
        user.setSessionVersion(3);
        assertFalse(jwt.isSessionCurrent(token, user));
        assertFalse(jwt.isTokenValid(token + "invalido"));
    }

    @Test void recusaChaveFraca() {
        assertThrows(IllegalArgumentException.class, () -> new JwtUtil("fraca", 3_600_000L));
    }
}
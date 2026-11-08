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
    @Test void aceitaSegredoBase64UrlForteSemTrocarChave() {
        byte[] raw=new byte[48];
        java.util.Arrays.fill(raw,(byte)0xfb);
        String urlSafe=Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
        assertTrue(urlSafe.contains("-"));
        JwtUtil jwt=new JwtUtil(urlSafe,3_600_000L);
        User user=new User();
        user.setUsername("coach");
        String token=jwt.generateToken(user);
        assertTrue(jwt.isTokenValid(token));
    }

    @Test void base64UrlCurtoContinuaProibido() {
        byte[] shortKey=new byte[16];
        java.util.Arrays.fill(shortKey,(byte)0xfb);
        String urlSafe=Base64.getUrlEncoder().withoutPadding().encodeToString(shortKey);
        assertThrows(IllegalArgumentException.class,()->new JwtUtil(urlSafe,3_600_000L));
    }

}

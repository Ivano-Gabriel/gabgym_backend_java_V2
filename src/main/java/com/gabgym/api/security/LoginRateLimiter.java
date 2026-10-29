package com.gabgym.api.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

/** Limite básico em memória para uma instância. Substituir por Redis/banco se escalar horizontalmente. */
@Component
public class LoginRateLimiter {
    private static final long WINDOW_MS = 10 * 60 * 1000L;
    private final Map<String, Window> attempts = new HashMap<>();
    private long globalWindowStart = System.currentTimeMillis();
    private int globalAttempts = 0;

    public synchronized void check(String username) {
        long now = System.currentTimeMillis();
        if (now - globalWindowStart >= 60_000L) {
            globalWindowStart = now;
            globalAttempts = 0;
        }
        attempts.entrySet().removeIf(entry -> now - entry.getValue().start >= WINDOW_MS);
        String key = username == null ? "" : username.toLowerCase(java.util.Locale.ROOT);
        Window count = attempts.get(key);
        if (globalAttempts >= 100 || (count != null && count.count >= 8) || attempts.size() >= 10_000) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Tente novamente mais tarde");
        }
        globalAttempts++;
        if (count == null) {
            count = new Window(now);
            attempts.put(key, count);
        }
        count.count++;
    }

    private static final class Window {
        private final long start;
        private int count;
        Window(long start) { this.start = start; }
    }
}

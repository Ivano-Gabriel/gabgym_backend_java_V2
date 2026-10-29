package com.gabgym.api.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Clock;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * MFA TOTP (RFC 6238): segredo de admin exclusivo do GABGYM, guardado apenas
 * no ambiente do servidor. Não habilitar login admin sem segredo configurado.
 */
@Component
public class TotpService {
    private final byte[] secret;
    private final Clock clock;

    @Autowired
    public TotpService(@Value("${ADMIN_TOTP_SECRET:}") String secret) {
        this(secret, Clock.systemUTC());
    }

    TotpService(String secret, Clock clock) {
        this.secret = decodeBase32(secret == null ? "" : secret.trim().replace(" ", "").toUpperCase());
        if (this.secret.length > 0 && this.secret.length < 20)
            throw new IllegalArgumentException("ADMIN_TOTP_SECRET exige ao menos 20 bytes aleatórios em base32");
        this.clock = clock;
    }

    public boolean isConfigured() { return secret.length >= 20; }

    /** Retorna o contador válido ou -1. Rejeita códigos já consumidos. */
    public long validatedCounter(String supplied, long lastCounter) {
        if (!isConfigured() || supplied == null || !supplied.matches("[0-9]{6}")) return -1L;
        long current = Math.floorDiv(clock.instant().getEpochSecond(), 30);
        for (long i = current - 1; i <= current + 1; i++) {
            if (i <= lastCounter || i < 0) continue;
            byte[] expected = otp(i).getBytes(StandardCharsets.US_ASCII);
            if (MessageDigest.isEqual(expected, supplied.getBytes(StandardCharsets.US_ASCII))) return i;
        }
        return -1L;
    }

    private String otp(long counter) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(secret, "HmacSHA1"));
            byte[] digest = mac.doFinal(ByteBuffer.allocate(8).putLong(counter).array());
            int offset = digest[digest.length - 1] & 15;
            int code = ((digest[offset] & 127) << 24) | ((digest[offset+1] & 255) << 16)
                    | ((digest[offset+2] & 255) << 8) | (digest[offset+3] & 255);
            return String.format(java.util.Locale.ROOT, "%06d", code % 1_000_000);
        } catch (Exception ex) { throw new IllegalStateException("TOTP indisponível", ex); }
    }

    private static byte[] decodeBase32(String input) {
        if (input.isEmpty()) return new byte[0];
        if (!input.matches("[A-Z2-7]+=*")) throw new IllegalArgumentException("ADMIN_TOTP_SECRET inválido");
        input = input.replaceAll("=+$", "");
        byte[] buffer = new byte[(input.length() * 5) / 8];
        int bitBuffer = 0, bits = 0, out = 0;
        for (char c : input.toCharArray()) {
            int value = c >= 'A' && c <= 'Z' ? c - 'A' : c - '2' + 26;
            bitBuffer = (bitBuffer << 5) | value;
            bits += 5;
            if (bits >= 8) {
                bits -= 8;
                buffer[out++] = (byte) ((bitBuffer >> bits) & 255);
            }
            bitBuffer &= (1 << bits) - 1;
        }
        return Arrays.copyOf(buffer, out);
    }
}

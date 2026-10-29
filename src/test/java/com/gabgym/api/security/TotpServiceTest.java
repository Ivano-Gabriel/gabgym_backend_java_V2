package com.gabgym.api.security;

import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;

/** Vetor público RFC 6238: HMAC-SHA1 em 59 segundos -> 94287082 (8 dígitos). */
class TotpServiceTest {
    @Test void validaCodigoDeSeisDigitosERecusaCodigoErrado() {
        TotpService totp = new TotpService("GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ",
            Clock.fixed(Instant.ofEpochSecond(59), ZoneOffset.UTC));
        assertEquals(1L, totp.validatedCounter("287082", -1L));
        assertEquals(-1L, totp.validatedCounter("000000", -1L));
        assertEquals(-1L, totp.validatedCounter("287082", 1L)); // anti-replay
    }
    @Test void recusaSegredoInvalidoEInstanciaSemConfiguracao() {
        assertThrows(IllegalArgumentException.class, () -> new TotpService("INVALIDO!", Clock.systemUTC()));
        assertEquals(-1L, new TotpService("", Clock.systemUTC()).validatedCounter("123456", -1L));
    }
}

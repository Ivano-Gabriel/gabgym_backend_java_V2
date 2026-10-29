package com.gabgym.api.security;

import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MfaHttpTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder encoder;

    private String otp(long step) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(new SecretKeySpec("12345678901234567890".getBytes(StandardCharsets.US_ASCII), "HmacSHA1"));
        byte[] digest = mac.doFinal(ByteBuffer.allocate(8).putLong(step).array());
        int offset = digest[19] & 15;
        int code = ((digest[offset] & 127) << 24) |
             ((digest[offset+1] & 255) << 16) |
             ((digest[offset+2] & 255) << 8) |
              (digest[offset+3] & 255);
        return String.format(java.util.Locale.ROOT, "%06d", code % 1000000);
    }

    private String payload(String username, String code) {
        return "{\"username\":\"" + username + "\",\"password\":\"senha-admin-segura\""
             + (code == null ? "" : ",\"codigoMfa\":\"" + code + "\"") + "}";
    }

    @Test void superAdminExigeMfaERejeitaReutilizacaoDoMesmoCodigo() throws Exception {
        User admin = new User();
        admin.setUsername("admin-mfa-" + UUID.randomUUID().toString().substring(0, 7));
        admin.setPassword(encoder.encode("senha-admin-segura"));
        admin.setRole(UserRole.SUPER_ADMIN);
        users.save(admin);

        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(admin.getUsername(), null)))
            .andExpect(status().isPreconditionRequired());

        String currentCode = otp(Instant.now().getEpochSecond() / 30);
        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(admin.getUsername(), currentCode)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));

        mvc.perform(post("/api/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(payload(admin.getUsername(), currentCode)))
            .andExpect(status().isUnauthorized());
    }
}

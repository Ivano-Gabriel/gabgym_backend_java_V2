package com.gabgym.api.config;

import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/** Bootstrap manual, uma única vez. Nunca guardar a senha de admin no repositório. */
@Configuration
public class AdminBootstrap {
    @Bean
    CommandLineRunner bootstrapAdmin(UserRepository users, PasswordEncoder passwords,
            @Value("${ADMIN_BOOTSTRAP_ENABLED:false}") boolean enabled,
            @Value("${ADMIN_BOOTSTRAP_USERNAME:}") String username,
            @Value("${ADMIN_BOOTSTRAP_PASSWORD:}") String password) {
        return args -> {
            if (!enabled) return;
            if (username == null || !username.matches("[a-zA-Z0-9_.-]{3,40}")
                    || password == null || password.length() < 16) {
                throw new IllegalStateException("Bootstrap admin exige username válido e senha >= 16 caracteres.");
            }
            if (users.existsByRole(UserRole.SUPER_ADMIN)) return;
            if (users.findByUsername(username).isPresent()) {
                throw new IllegalStateException("Nome de superadmin já pertence a outra conta.");
            }
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwords.encode(password));
            admin.setRole(UserRole.SUPER_ADMIN);
            users.save(admin);
        };
    }
}

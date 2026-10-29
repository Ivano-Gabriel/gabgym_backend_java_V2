package com.gabgym.api.security;

import com.gabgym.api.config.JwtUtil;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HttpAuthorizationTest {
    @Autowired private MockMvc mvc;
    @Autowired private UserRepository users;
    @Autowired private PasswordEncoder passwords;
    @Autowired private JwtUtil jwt;

    private User student() {
        User user = new User();
        user.setUsername("aluno-" + UUID.randomUUID().toString().substring(0, 10));
        user.setPassword(passwords.encode("uma-senha-de-teste-forte"));
        user.setRole(UserRole.ALUNO);
        return users.save(user);
    }

    @Test
    void rotasDeUsuarioEAdminExigemLogin() throws Exception {
        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/students")).andExpect(status().isUnauthorized());
    }

    @Test
    void alunoNaoPodeLerPerfilAlheioNemPainelAdmin() throws Exception {
        User aluno = student();
        User outro = student();
        String token = "Bearer " + jwt.generateToken(aluno);

        mvc.perform(get("/api/users/" + aluno.getId()).header("Authorization", token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value(aluno.getUsername()))
            .andExpect(jsonPath("$.password").doesNotExist());

        mvc.perform(get("/api/users/" + outro.getId()).header("Authorization", token))
            .andExpect(status().isForbidden());

        mvc.perform(get("/api/admin/students").header("Authorization", token))
            .andExpect(status().isForbidden());

        mvc.perform(get("/api/metas/user/" + outro.getId()).header("Authorization", token))
            .andExpect(status().isForbidden());

        mvc.perform(get("/api/logs/user/" + outro.getId()).param("date", "2026-10-29")
                .header("Authorization", token))
            .andExpect(status().isForbidden());
    }

    @Test
    void registroPublicoNaoPermiteEscolherSuperAdmin() throws Exception {
        String username = "novo-" + UUID.randomUUID().toString().substring(0, 10);
        mvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username +
                         "\",\"password\":\"uma-senha-bem-segura\",\"role\":\"SUPER_ADMIN\",\"active\":false}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.role").value("ALUNO"))
            .andExpect(jsonPath("$.password").doesNotExist());

        User created = users.findByUsername(username).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(UserRole.ALUNO, created.getRole());
        org.junit.jupiter.api.Assertions.assertTrue(created.isActive());
    }

    @Test
    void usuarioNaoConsegueSalvarDiarioEmContaDeOutroAluno() throws Exception {
        User aluno = student();
        User outro = student();
        String token = "Bearer " + jwt.generateToken(aluno);

        mvc.perform(post("/api/logs")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":" + outro.getId() +
                         ",\"type\":\"water\",\"name\":\"Água\",\"volume\":300}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(aluno.getId()));

        mvc.perform(get("/api/logs/user/" + outro.getId())
                .param("date", "2026-10-29").header("Authorization", token))
            .andExpect(status().isForbidden());
    }
}

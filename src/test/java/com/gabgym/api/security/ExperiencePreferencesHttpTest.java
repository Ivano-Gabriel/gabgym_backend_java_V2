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
class ExperiencePreferencesHttpTest {
    @Autowired MockMvc mvc;
    @Autowired JwtUtil jwt;
    @Autowired UserRepository users;
    @Autowired PasswordEncoder passwords;

    private User account(){
        User u=new User();
        u.setUsername("ex-"+UUID.randomUUID().toString().substring(0,12));
        u.setPassword(passwords.encode("senha-para-testar-preferencias"));
        u.setRole(UserRole.ALUNO);
        return users.save(u);
    }
    private String token(User user){return "Bearer "+jwt.generateToken(user);}

    @Test void novasContasUsamPadraoCompativelSemDeletarHistorico() throws Exception {
        User student=account();
        mvc.perform(get("/api/users/me/preferences").header("Authorization",token(student)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mode").value("normal"))
            .andExpect(jsonPath("$.food").value(true))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test void umUsuarioNuncaAlteraPreferenciasDeOutro() throws Exception {
        User first=account(),second=account();
        String update="{\"mode\":\"simple\",\"food\":true,\"workout\":false,\"weight\":true,\"coach\":false,\"language\":\"fr\",\"completed\":true}";
        mvc.perform(put("/api/users/me/preferences").header("Authorization",token(first))
            .contentType(MediaType.APPLICATION_JSON).content(update))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.mode").value("simple"))
            .andExpect(jsonPath("$.language").value("fr"));
        mvc.perform(get("/api/users/me/preferences").header("Authorization",token(first)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.workout").value(false));
        mvc.perform(get("/api/users/me/preferences").header("Authorization",token(second)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.workout").value(true))
            .andExpect(jsonPath("$.language").value("pt"));
    }

    @Test void valoresInvalidosEAnonimosSaoRejeitados() throws Exception {
        User student=account();
        mvc.perform(get("/api/users/me/preferences")).andExpect(status().isUnauthorized());
        String malformed="{\"mode\":\"admin\",\"food\":true,\"workout\":true,\"weight\":true,\"coach\":true,\"language\":\"invalid\",\"completed\":true}";
        mvc.perform(put("/api/users/me/preferences").header("Authorization",token(student))
            .contentType(MediaType.APPLICATION_JSON).content(malformed))
            .andExpect(status().isBadRequest());
    }
}

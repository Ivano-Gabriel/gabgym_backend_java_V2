package com.gabgym.api.security;

import com.gabgym.api.config.JwtUtil;
import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.repositories.UserRepository;
import com.gabgym.api.repositories.DiaryLogRepository;
import com.gabgym.api.entities.DiaryLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PersonalAuthorizationHttpTest {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired DiaryLogRepository logs;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtUtil jwt;

    private User member(UserRole role) {
        User user=new User();
        user.setUsername("test-" + UUID.randomUUID().toString().substring(0,12));
        user.setPassword(passwords.encode("senha-teste-muito-forte"));
        user.setRole(role);
        return users.save(user);
    }
    private String bearer(User user){return "Bearer " + jwt.generateToken(user);}
    private String requestBody(User personal) {
        return "{\"username\":\"" + personal.getUsername() + "\"}";
    }

    @Test
    void cadastroPersonalNaoPermiteInjetarSuperAdminENaoTrocaLoginAluno() throws Exception {
        String username="coach-"+UUID.randomUUID().toString().substring(0,10);
        mvc.perform(post("/api/users/register-personal").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\""+username+"\",\"password\":\"senha-teste-muito-forte\",\"role\":\"SUPER_ADMIN\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("PERSONAL"))
            .andExpect(jsonPath("$.password").doesNotExist());
        User account=users.findByUsername(username).orElseThrow();
        mvc.perform(get("/api/personal/coach/students").header("Authorization",bearer(account)))
            .andExpect(status().isOk());
        mvc.perform(get("/api/admin/students").header("Authorization",bearer(account)))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/logs").header("Authorization",bearer(account))
            .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"water\",\"volume\":200}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void consentimentoAlunoSolicitaCoachAprovaERevogacaoFechaTudo() throws Exception {
        User student=member(UserRole.ALUNO),coach=member(UserRole.PERSONAL),
             another=member(UserRole.PERSONAL),stranger=member(UserRole.ALUNO);
        mvc.perform(get("/api/personal/coach/students").header("Authorization",bearer(student)))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/student/requests").header("Authorization",bearer(coach))
             .contentType(MediaType.APPLICATION_JSON).content(requestBody(coach)))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/student/requests").header("Authorization",bearer(student))
             .contentType(MediaType.APPLICATION_JSON).content(requestBody(coach)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING"));
        mvc.perform(get("/api/personal/coach/students").header("Authorization",bearer(coach)))
            .andExpect(status().isOk()).andExpect(jsonPath("$[0].status").value("PENDING"));
        mvc.perform(get("/api/personal/coach/students/"+student.getId()+"/day")
             .header("Authorization",bearer(coach))).andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/coach/requests/"+student.getId()+"/decision")
             .header("Authorization",bearer(another)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"decision\":\"ACCEPT\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/coach/requests/"+student.getId()+"/decision")
             .header("Authorization",bearer(coach)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"decision\":\"ACCEPT\"}")).andExpect(status().isOk())
             .andExpect(jsonPath("$.status").value("ACTIVE"));
        DiaryLog food=new DiaryLog();food.setUserId(student.getId());food.setType("food");
        food.setName("Arroz e feijão");food.setCalories(550);food.setLogDate(LocalDate.now());logs.save(food);
        DiaryLog water=new DiaryLog();water.setUserId(student.getId());water.setType("water");
        water.setVolume(300);water.setLogDate(LocalDate.now());logs.save(water);

        mvc.perform(get("/api/personal/coach/students/"+student.getId()+"/day")
             .header("Authorization",bearer(coach)))
             .andExpect(status().isOk()).andExpect(jsonPath("$.calories").value(550))
             .andExpect(jsonPath("$.waterMl").value(300));
        mvc.perform(get("/api/personal/coach/students/"+student.getId()+"/day")
             .header("Authorization",bearer(another))).andExpect(status().isForbidden());
        // Vínculo Coach jamais concede acesso a endpoint legado que exige dono.
        mvc.perform(get("/api/logs/user/"+student.getId()).param("date",LocalDate.now().toString())
             .header("Authorization",bearer(coach))).andExpect(status().isForbidden());
        mvc.perform(get("/api/personal/coach/students/"+student.getId()+"/day")
             .header("Authorization",bearer(stranger))).andExpect(status().isForbidden());

        mvc.perform(delete("/api/personal/student/connection")
             .header("Authorization",bearer(student))).andExpect(status().isNoContent());
        mvc.perform(get("/api/personal/coach/students/"+student.getId()+"/day")
             .header("Authorization",bearer(coach))).andExpect(status().isForbidden());
    }

    @Test
    void mensagensNotasPrivadasEPlanosExigemVinculoEConsentimento() throws Exception {
        User student=member(UserRole.ALUNO),coach=member(UserRole.PERSONAL),other=member(UserRole.PERSONAL);
        mvc.perform(post("/api/personal/student/requests").header("Authorization",bearer(student))
             .contentType(MediaType.APPLICATION_JSON).content(requestBody(coach)))
            .andExpect(status().isCreated());
        mvc.perform(post("/api/personal/coach/requests/"+student.getId()+"/decision")
             .header("Authorization",bearer(coach)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"decision\":\"ACCEPT\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/personal/students/"+student.getId()+"/timeline")
             .header("Authorization",bearer(student)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"kind\":\"PLAN\",\"content\":\"falso plano\"}"))
             .andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/students/"+student.getId()+"/timeline")
             .header("Authorization",bearer(other)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"kind\":\"MESSAGE\",\"content\":\"intrusão\"}"))
             .andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/students/"+student.getId()+"/timeline")
             .header("Authorization",bearer(coach)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"kind\":\"NOTE\",\"content\":\"Nota confidencial do coach\"}"))
             .andExpect(status().isCreated());
        mvc.perform(get("/api/personal/students/"+student.getId()+"/timeline")
             .header("Authorization",bearer(student))).andExpect(status().isOk())
             .andExpect(jsonPath("$").isEmpty());
        mvc.perform(post("/api/personal/students/"+student.getId()+"/timeline")
             .header("Authorization",bearer(coach)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"kind\":\"PLAN\",\"content\":\"Treino superior 3x semana\"}"))
             .andExpect(status().isCreated()).andExpect(jsonPath("$.status").value("PENDING"));
        String response=mvc.perform(get("/api/personal/students/"+student.getId()+"/timeline")
            .header("Authorization",bearer(student))).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].kind").value("PLAN")).andReturn().getResponse().getContentAsString();
        long entryId=com.fasterxml.jackson.databind.json.JsonMapper.builder().build()
            .readTree(response).get(0).get("id").asLong();
        mvc.perform(post("/api/personal/students/"+student.getId()+"/plans/"+entryId+"/decision")
             .header("Authorization",bearer(coach)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"decision\":\"APPROVE\"}")).andExpect(status().isForbidden());
        mvc.perform(post("/api/personal/students/"+student.getId()+"/plans/"+entryId+"/decision")
             .header("Authorization",bearer(student)).contentType(MediaType.APPLICATION_JSON)
             .content("{\"decision\":\"APPROVE\"}"))
             .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("APPROVED"));
    }
}

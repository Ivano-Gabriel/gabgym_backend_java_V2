package com.gabgym.api.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Direct Render access for protected Vercel QA branch, without opening CORS globally. */
@SpringBootTest
@AutoConfigureMockMvc
class PersonalQaCorsHttpTest {
    @Autowired MockMvc mvc;
    private static final String QA_ORIGIN =
        "https://gabgym-frontend-git-test-gabgym-personal-n-09dadb-gabriel-team3.vercel.app";

    @Test
    void qaPreviewPodeCadastrarDiretoViaCorsRestrito() throws Exception {
        mvc.perform(options("/api/users/register-personal")
            .header("Origin",QA_ORIGIN)
            .header("Access-Control-Request-Method","POST")
            .header("Access-Control-Request-Headers","content-type"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin",QA_ORIGIN))
            .andExpect(header().string("Vary",org.hamcrest.Matchers.containsString("Origin")));
    }

    @Test
    void origemDesconhecidaNaoRecebeAutorizacaoCors() throws Exception {
        mvc.perform(options("/api/users/register-personal")
            .header("Origin","https://site-desconhecido.example")
            .header("Access-Control-Request-Method","POST")
            .header("Access-Control-Request-Headers","content-type"))
            .andExpect(status().isForbidden())
            .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}

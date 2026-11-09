package com.gabgym.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Value("${cors.allowed.origin}")
    private String allowedOrigins;

    // QA isolado do Meu Personal. A Vercel continua exigindo autenticação no preview;
    // CORS libera somente este origin estável para chamadas diretas ao Render Neon.
    private static final String PERSONAL_QA_ORIGIN =
        "https://gabgym-frontend-git-test-gabgym-personal-n-09dadb-gabriel-team3.vercel.app";

    @Override public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Stream.concat(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim).filter(origin -> !origin.isEmpty()),
                Stream.of(PERSONAL_QA_ORIGIN)).distinct().toArray(String[]::new);
        registry.addMapping("/api/**")
            .allowedOrigins(origins)
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("Authorization", "Content-Type");
    }
}

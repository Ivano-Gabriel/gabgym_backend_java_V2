package com.gabgym.api.controllers;

import com.gabgym.api.config.JwtUtil;
import com.gabgym.api.entities.User;
import com.gabgym.api.entities.DailyLog;
import com.gabgym.api.repositories.UserRepository;
import com.gabgym.api.repositories.DailyLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")

public class UserController {

    private final UserRepository userRepo;
    private final DailyLogRepository logRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserController(UserRepository userRepo, DailyLogRepository logRepo,
                           PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.logRepo = logRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Cria a conta do usuário (AGORA com senha criptografada)
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.ok(userRepo.save(user));
    }

    // NOVO: Login — recebe username+senha, devolve o token JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String rawPassword = credentials.get("password");

        User user = userRepo.findByUsername(username).orElse(null);

        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuário ou senha inválidos"));
        }

        String token = jwtUtil.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "userId", user.getId(),
                "username", user.getUsername()
        ));
    }

    // Busca o perfil do usuário
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/logs")
public ResponseEntity<DailyLog> addLog(@PathVariable Long id, @RequestBody DailyLog log) {
    return userRepo.findById(id).map(user -> {

        // Verifica se já existe um log de hoje pra esse usuário
        DailyLog logDoDia = logRepo.findByUserIdAndDate(id, java.time.LocalDate.now())
                .orElseGet(() -> {
                    DailyLog novo = new DailyLog();
                    novo.setUser(user);
                    return novo;
                });

        // Soma os valores novos em cima do que já existia no dia
        logDoDia.setCaloriesConsumed(logDoDia.getCaloriesConsumed() + log.getCaloriesConsumed());
        logDoDia.setProteinConsumed(logDoDia.getProteinConsumed() + log.getProteinConsumed());
        logDoDia.setCarbsConsumed(logDoDia.getCarbsConsumed() + log.getCarbsConsumed());
        logDoDia.setFatConsumed(logDoDia.getFatConsumed() + log.getFatConsumed());
        logDoDia.setWaterMl(logDoDia.getWaterMl() + log.getWaterMl());

        // Sono não faz sentido "somar" (não é cumulativo tipo caloria) — substitui pelo valor mais recente
        if (log.getSleepHours() > 0) {
            logDoDia.setSleepHours(log.getSleepHours());
        }

        return ResponseEntity.ok(logRepo.save(logDoDia));
    }).orElse(ResponseEntity.notFound().build());
}

    // Mostra o histórico diário dele
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<DailyLog>> getUserLogs(@PathVariable Long id) {
        return ResponseEntity.ok(logRepo.findByUserId(id));
    }
}
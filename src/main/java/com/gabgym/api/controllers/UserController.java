package com.gabgym.api.controllers;

import com.gabgym.api.config.JwtUtil;
import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.entities.DailyLog;
import com.gabgym.api.repositories.UserRepository;
import com.gabgym.api.repositories.DailyLogRepository;
import com.gabgym.api.security.AuthorizationService;
import com.gabgym.api.security.LoginRateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository users;
    private final DailyLogRepository logs;
    private final PasswordEncoder passwords;
    private final JwtUtil jwt;
    private final AuthorizationService authorization;
    private final LoginRateLimiter rateLimiter;

    public UserController(UserRepository users, DailyLogRepository logs, PasswordEncoder passwords,
                          JwtUtil jwt, AuthorizationService authorization, LoginRateLimiter rateLimiter) {
        this.users = users;
        this.logs = logs;
        this.passwords = passwords;
        this.jwt = jwt;
        this.authorization = authorization;
        this.rateLimiter = rateLimiter;
    }

    // DTOs impedem que um cliente escolha sua própria role, ID ou flags de sessão.
    public record Credentials(String username, String password) {}
    public record LoginResponse(String token, Long userId, String username, UserRole role) {}
    public record UserProfile(Long id, String username, String name, Integer age,
                              Double weight, Double height, String gender, String objective,
                              String activityLevel, UserRole role) {
        public static UserProfile from(User user) {
            return new UserProfile(user.getId(), user.getUsername(), user.getName(),
                user.getAge(), user.getWeight(), user.getHeight(), user.getGender(),
                user.getObjective(), user.getActivityLevel(), user.getRole());
        }
    }
    public record ProfileUpdate(String name, Integer age, Double weight, Double height,
                                String gender, String objective, String activityLevel) {}

    @PostMapping("/register")
    public ResponseEntity<UserProfile> register(@RequestBody Credentials input) {
        if (input == null || input.username() == null || !input.username().matches("[a-zA-Z0-9_.-]{3,40}")
                || input.password() == null || input.password().length() < 10
                || input.password().length() > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Usuário deve ter 3 a 40 caracteres e senha de 10 a 72 caracteres");
        }
        if (users.findByUsername(input.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuário indisponível");
        }
        User user = new User();
        user.setUsername(input.username());
        user.setPassword(passwords.encode(input.password()));
        user.setRole(UserRole.ALUNO);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserProfile.from(users.save(user)));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Credentials input) {
        if (input == null || input.username() == null || input.password() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }
        rateLimiter.check(input.username());
        User user = users.findByUsername(input.username()).orElse(null);
        if (user == null || !user.isActive() || !passwords.matches(input.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }
        return new LoginResponse(jwt.generateToken(user), user.getId(), user.getUsername(), user.getRole());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        User user = authorization.currentUser();
        user.setSessionVersion(user.getSessionVersion() + 1);
        users.save(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserProfile me() { return UserProfile.from(authorization.currentUser()); }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getUser(@PathVariable Long id) {
        authorization.requireOwnerOrAdmin(id);
        return users.findById(id).map(user -> ResponseEntity.ok(UserProfile.from(user)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateUser(@PathVariable Long id, @RequestBody ProfileUpdate input) {
        authorization.requireOwner(id);
        return users.findById(id).map(user -> {
            if (input.name() != null) user.setName(input.name());
            if (input.age() != null && input.age() >= 0 && input.age() <= 120) user.setAge(input.age());
            if (input.weight() != null && input.weight() > 0 && input.weight() < 800) user.setWeight(input.weight());
            if (input.height() != null && input.height() > 0 && input.height() < 350) user.setHeight(input.height());
            if (input.gender() != null) user.setGender(input.gender());
            if (input.objective() != null) user.setObjective(input.objective());
            if (input.activityLevel() != null) user.setActivityLevel(input.activityLevel());
            return ResponseEntity.ok(UserProfile.from(users.save(user)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/logs")
    public ResponseEntity<DailyLog> addLog(@PathVariable Long id, @RequestBody DailyLog input) {
        authorization.requireOwner(id);
        User user = users.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        DailyLog today = logs.findByUserIdAndDate(id, LocalDate.now()).orElseGet(() -> {
            DailyLog fresh = new DailyLog();
            fresh.setUser(user);
            return fresh;
        });
        today.setCaloriesConsumed(today.getCaloriesConsumed() + input.getCaloriesConsumed());
        today.setProteinConsumed(today.getProteinConsumed() + input.getProteinConsumed());
        today.setCarbsConsumed(today.getCarbsConsumed() + input.getCarbsConsumed());
        today.setFatConsumed(today.getFatConsumed() + input.getFatConsumed());
        today.setWaterMl(today.getWaterMl() + input.getWaterMl());
        if (input.getSleepHours() > 0) today.setSleepHours(input.getSleepHours());
        return ResponseEntity.ok(logs.save(today));
    }

    @GetMapping("/{id}/logs")
    public List<DailyLog> getUserLogs(@PathVariable Long id) {
        authorization.requireOwnerOrAdmin(id);
        return logs.findByUserId(id);
    }
}

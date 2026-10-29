package com.gabgym.api.controllers;

import com.gabgym.api.entities.User;
import com.gabgym.api.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users;

    public AdminController(UserRepository users) { this.users = users; }

    public record StudentSummary(Long id, String username, String name, boolean profileComplete) {
        static StudentSummary from(User user) {
            return new StudentSummary(user.getId(), user.getUsername(), user.getName(),
                user.getWeight() != null && user.getHeight() != null);
        }
    }

    @GetMapping("/students")
    public List<StudentSummary> students() {
        return users.findAll().stream()
            .filter(user -> user.getRole() == com.gabgym.api.entities.UserRole.ALUNO)
            .map(StudentSummary::from)
            .toList();
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<UserController.UserProfile> student(@PathVariable Long id) {
        return users.findById(id)
            .filter(user -> user.getRole() == com.gabgym.api.entities.UserRole.ALUNO)
            .map(user -> ResponseEntity.ok(UserController.UserProfile.from(user)))
            .orElse(ResponseEntity.notFound().build());
    }
}

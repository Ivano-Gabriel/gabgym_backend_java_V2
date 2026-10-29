package com.gabgym.api.controllers;

import com.gabgym.api.entities.DiaryLog;
import com.gabgym.api.repositories.DiaryLogRepository;
import com.gabgym.api.security.AuthorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class DiaryLogController {
    private final DiaryLogRepository logs;
    private final AuthorizationService auth;

    public DiaryLogController(DiaryLogRepository logs, AuthorizationService auth) {
        this.logs = logs;
        this.auth = auth;
    }

    @GetMapping("/user/{userId}")
    public List<DiaryLog> getLogsByDate(@PathVariable Long userId, @RequestParam LocalDate date) {
        auth.requireOwnerOrAdmin(userId);
        return logs.findByUserIdAndLogDate(userId, date);
    }

    @PostMapping
    public DiaryLog addLog(@RequestBody DiaryLog log) {
        // ID, usuário e timestamp de propriedade nunca vêm do cliente.
        log.setId(null);
        log.setUserId(auth.requireOwner(auth.currentUser().getId()).getId());
        if (log.getLogDate() == null) log.setLogDate(LocalDate.now());
        return logs.save(log);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        return logs.findById(id).map(log -> {
            auth.requireOwner(log.getUserId());
            logs.delete(log);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

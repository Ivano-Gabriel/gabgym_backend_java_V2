package com.gabgym.api.controllers;

import com.gabgym.api.entities.DiaryLog;
import com.gabgym.api.repositories.DiaryLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class DiaryLogController {

    private final DiaryLogRepository repository;

    public DiaryLogController(DiaryLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiaryLog>> getLogsByDate(@PathVariable Long userId, @RequestParam String date) {
        LocalDate logDate = LocalDate.parse(date);
        return ResponseEntity.ok(repository.findByUserIdAndLogDate(userId, logDate));
    }

    @PostMapping
    public ResponseEntity<DiaryLog> addLog(@RequestBody DiaryLog log) {
        if (log.getLogDate() == null) {
            log.setLogDate(LocalDate.now());
        }
        return ResponseEntity.ok(repository.save(log));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
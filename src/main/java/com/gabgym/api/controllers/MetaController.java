package com.gabgym.api.controllers;

import com.gabgym.api.entities.Meta;
import com.gabgym.api.repositories.MetaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    private final MetaRepository repository;

    public MetaController(MetaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Meta>> getMetasByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(repository.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Meta> createMeta(@RequestBody Meta meta) {
        if (meta.getStatus() == null) {
            meta.setStatus("ATIVA");
        }
        return ResponseEntity.ok(repository.save(meta));
    }

    // Recebe { "status": "CONCLUIDA" } ou { "status": "DESISTIDA" }
    @PutMapping("/{id}/status")
    public ResponseEntity<Meta> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return repository.findById(id).map(meta -> {
            meta.setStatus(body.get("status"));
            return ResponseEntity.ok(repository.save(meta));
        }).orElse(ResponseEntity.notFound().build());
    }
}
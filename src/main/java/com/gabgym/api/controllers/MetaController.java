package com.gabgym.api.controllers;

import com.gabgym.api.entities.Meta;
import com.gabgym.api.repositories.MetaRepository;
import com.gabgym.api.security.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metas")
public class MetaController {
    private final MetaRepository metas;
    private final AuthorizationService auth;
    public MetaController(MetaRepository metas, AuthorizationService auth) {
        this.metas = metas;
        this.auth = auth;
    }

    @GetMapping("/user/{userId}")
    public List<Meta> getMetasByUser(@PathVariable Long userId) {
        auth.requireOwnerOrAdmin(userId);
        return metas.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<Meta> createMeta(@RequestBody Meta meta) {
        if (meta.getTitulo() == null || meta.getTitulo().isBlank()
            || meta.getTitulo().length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Título inválido");
        }
        meta.setId(null);
        meta.setUserId(auth.currentUser().getId());
        meta.setStatus("ATIVA");
        return ResponseEntity.status(HttpStatus.CREATED).body(metas.save(meta));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Meta> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> data) {
        String status = data.get("status");
        if (!List.of("ATIVA", "CONCLUIDA", "DESISTIDA").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido");
        }
        return metas.findById(id).map(meta -> {
            auth.requireOwnerOrAdmin(meta.getUserId());
            meta.setStatus(status);
            return ResponseEntity.ok(metas.save(meta));
        }).orElse(ResponseEntity.notFound().build());
    }
}

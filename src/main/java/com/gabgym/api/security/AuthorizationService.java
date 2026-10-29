package com.gabgym.api.security;

import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/** Toda autorização de propriedade ocorre na API, nunca com o userId enviado pelo cliente. */
@Service
public class AuthorizationService {
    private final UserRepository users;

    public AuthorizationService(UserRepository users) {
        this.users = users;
    }

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticação necessária");
        }
        return users.findByUsername(authentication.getName())
            .filter(User::isActive)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão inválida"));
    }

    public User requireOwner(Long targetUserId) {
        User actor = currentUser();
        if (actor.getRole() != UserRole.ALUNO || !actor.getId().equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return actor;
    }

    public User requireOwnerOrAdmin(Long targetUserId) {
        User actor = currentUser();
        if (actor.getRole() != UserRole.SUPER_ADMIN && !actor.getId().equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return actor;
    }
}

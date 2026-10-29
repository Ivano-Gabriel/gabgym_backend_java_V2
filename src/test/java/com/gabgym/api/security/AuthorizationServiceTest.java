package com.gabgym.api.security;

import com.gabgym.api.entities.User;
import com.gabgym.api.entities.UserRole;
import com.gabgym.api.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final AuthorizationService authorization = new AuthorizationService(users);

    private void loginAs(String username, Long id, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRole(role);
        when(users.findByUsername(username)).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(username, null, java.util.List.of()));
    }

    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }

    @Test void alunoPodeAcessarSeusPropriosDados() {
        loginAs("ana", 7L, UserRole.ALUNO);
        assertDoesNotThrow(() -> authorization.requireOwnerOrAdmin(7L));
    }

    @Test void alunoNaoPodeAcessarDadosDeOutroAluno() {
        loginAs("ana", 7L, UserRole.ALUNO);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> authorization.requireOwnerOrAdmin(8L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test void superAdminPodeConsultarDadosDeAluno() {
        loginAs("gestor", 1L, UserRole.SUPER_ADMIN);
        assertDoesNotThrow(() -> authorization.requireOwnerOrAdmin(8L));
    }

    @Test void superAdminNaoPodeAlterarRegistrosDosAlunos() {
        loginAs("gestor", 1L, UserRole.SUPER_ADMIN);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> authorization.requireOwner(8L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test void requisicaoSemSessaoNaoPodeConsultarDados() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
            () -> authorization.requireOwnerOrAdmin(7L));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
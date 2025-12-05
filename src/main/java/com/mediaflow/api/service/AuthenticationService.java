package com.mediaflow.api.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mediaflow.api.model.User;
import com.mediaflow.api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;

    /**
     * Obtiene el email del usuario autenticado actualmente
     */
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return authentication.getName();
    }

    /**
     * Obtiene el usuario autenticado completo
     */
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        return userRepository.findByEmailForAuth(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario autenticado no encontrado"));
    }

    /**
     * Obtiene el ID del usuario autenticado
     */
    public Integer getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * Verifica si el usuario autenticado es el propietario del recurso
     */
    public boolean isOwner(Integer userId) {
        return getCurrentUserId().equals(userId);
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        User user = getCurrentUser();
        return user.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    /**
     * Verifica si el usuario es administrador
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verifica si el usuario puede acceder al recurso
     * (es el propietario o es admin)
     */
    public boolean canAccess(Integer userId) {
        return isOwner(userId) || isAdmin();
    }

    /**
     * Lanza excepción si el usuario no puede acceder al recurso
     */
    public void validateAccess(Integer userId) {
        if (!canAccess(userId)) {
            throw new SecurityException("No tienes permiso para acceder a este recurso");
        }
    }
}
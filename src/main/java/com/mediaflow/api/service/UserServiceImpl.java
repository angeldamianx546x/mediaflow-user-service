package com.mediaflow.api.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mediaflow.api.dto.UserAuth;
import com.mediaflow.api.dto.UserRequest;
import com.mediaflow.api.dto.UserResponse;
import com.mediaflow.api.mapper.UserMapper;
import com.mediaflow.api.model.Profile;
import com.mediaflow.api.model.Role;
import com.mediaflow.api.model.User;
import com.mediaflow.api.repository.ProfileRepository;
import com.mediaflow.api.repository.RoleRepository;
import com.mediaflow.api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final RoleRepository repositoryRole;
    private final PasswordEncoder passwordEncoder;
    private final ProfileRepository profileRepository;

    private static final List<String> RESTRICTED_ROLES = List.of("ADMIN", "MODERATOR");
    private static final Integer DEFAULT_ROLE_ID = 1; // VIEWER por defecto

    @Override
    public UserResponse findById(Integer userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse findByEmail(String email) {
        User user = repository.findByEmailForAuth(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse create(UserRequest req) {
        User user = UserMapper.toEntity(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        // ASIGNACIÓN SEGURA DE ROLES EN EL REGISTRO
        List<Role> roles = new ArrayList<>();

        // Siempre agregar el rol por defecto (VIEWER)
        Role defaultRole = repositoryRole.findById(DEFAULT_ROLE_ID)
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));
        roles.add(defaultRole);

        // Si se proporcionan roles adicionales, validarlos
        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            for (Integer roleId : req.getRoles()) {
                // Saltar el rol por defecto (ya está agregado)
                if (roleId.equals(DEFAULT_ROLE_ID)) {
                    continue;
                }

                // Buscar el rol
                Role role = repositoryRole.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

                // BLOQUEAR ROLES RESTRINGIDOS
                if (RESTRICTED_ROLES.contains(role.getName().toUpperCase())) {
                    throw new IllegalArgumentException(
                            "Cannot assign restricted role: " + role.getName() +
                                    ". This role can only be assigned by an administrator.");
                }

                roles.add(role);
            }
        }

        // Eliminar duplicados
        Map<Integer, Role> rolesMap = roles.stream()
                .collect(Collectors.toMap(Role::getRoleId, r -> r, (r1, r2) -> r1));
        user.setRoles(new ArrayList<>(rolesMap.values()));

        // Guardar el usuario
        User savedUser = repository.save(user);

        // Crear el perfil predeterminado
        String language = (req.getPreferredLanguage() != null && !req.getPreferredLanguage().isBlank())
                ? req.getPreferredLanguage()
                : "es";

        Profile profile = Profile.builder()
                .displayName(savedUser.getName())
                .preferredLanguage(language)
                .avatarUrl(
                        "https://media.istockphoto.com/id/1495088043/es/vector/icono-de-perfil-de-usuario-avatar-o-icono-de-persona-foto-de-perfil-s%C3%ADmbolo-de-retrato.jpg?s=612x612&w=0&k=20&c=mY3gnj2lU7khgLhV6dQBNqomEGj3ayWH-xtpYuCXrzk=")
                .bio("Nuevo usuario registrado.")
                .user(savedUser)
                .build();

        Profile savedProfile = profileRepository.save(profile);
        savedUser.setProfile(savedProfile);

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse update(Integer userId, UserRequest req) {
        User existing = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        UserMapper.copyToEntity(req, existing);

        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        Set<Role> roles = new HashSet<>();

        // Siempre mantener el rol por defecto
        Role defaultRole = repositoryRole.findById(DEFAULT_ROLE_ID)
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));
        roles.add(defaultRole);

        if (req.getRoles() != null && !req.getRoles().isEmpty()) {
            for (Integer roleId : req.getRoles()) {
                if (roleId.equals(DEFAULT_ROLE_ID)) {
                    continue;
                }

                Role role = repositoryRole.findById(roleId)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

                // PROTEGER CONTRA ASIGNACIÓN NO AUTORIZADA DE ROLES RESTRINGIDOS
                if (RESTRICTED_ROLES.contains(role.getName().toUpperCase())) {
                    // Verificar si el usuario YA TIENE ese rol (puede mantenerlo)
                    boolean userAlreadyHasRole = existing.getRoles().stream()
                            .anyMatch(r -> r.getRoleId().equals(roleId));

                    if (!userAlreadyHasRole) {
                        throw new IllegalArgumentException(
                                "Cannot assign restricted role: " + role.getName() +
                                        ". This role can only be assigned by an administrator.");
                    }
                }

                roles.add(role);
            }
        }

        existing.setRoles(new ArrayList<>(roles));

        // Actualizar idioma del perfil
        if (req.getPreferredLanguage() != null && !req.getPreferredLanguage().isBlank()) {
            if (existing.getProfile() != null) {
                existing.getProfile().setPreferredLanguage(req.getPreferredLanguage());
                profileRepository.save(existing.getProfile());
            }
        }

        User saved = repository.save(existing);
        return UserMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer userId) {
        if (!repository.existsById(userId)) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        repository.deleteById(userId);
    }

    @Override
    public UserAuth login(String email, String password) {
        UserAuth user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return user;
    }

}

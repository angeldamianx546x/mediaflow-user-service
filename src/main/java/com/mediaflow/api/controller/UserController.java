package com.mediaflow.api.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mediaflow.api.dto.AuthResponse;
import com.mediaflow.api.dto.LoguinReques;
import com.mediaflow.api.dto.UserAuth;
import com.mediaflow.api.dto.UserRequest;
import com.mediaflow.api.dto.UserResponse;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.JwtService;
import com.mediaflow.api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE,
                RequestMethod.PUT })
@Tag(name = "Users", description = "Provides methods for managing users")
public class UserController {

        private final UserService service;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;
        private final UserDetailsService userDetailsService;
        private final AuthenticationService authenticationService;

        @Operation(summary = "Register new user", description = "Creates a new user in the system with profile")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "409", description = "Email already registered")
        })
        @PostMapping("/register")
        public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest req) {
                UserResponse created = service.create(req);
                return ResponseEntity
                                .created(URI.create("/api/v1/users/" + created.getUserId()))
                                .body(created);
        }

        @Operation(summary = "Update user account", description = "Updates user information. Users can only update their own account unless they are ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Cannot update another user's account"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PutMapping("/update_account/{userId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<?> update(
                        @PathVariable Integer userId,
                        @Valid @RequestBody UserRequest req) {

                // Validar que el usuario solo pueda actualizar su propia cuenta (o sea admin)
                if (!authenticationService.canAccess(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(buildErrorResponse("No tienes permiso para actualizar esta cuenta"));
                }

                return ResponseEntity.ok(service.update(userId, req));
        }

        @Operation(summary = "Delete user account", description = "Permanently deletes a user. Users can only delete their own account unless they are ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete another user's account"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @DeleteMapping("/delete_account/{userId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<?> delete(@PathVariable Integer userId) {
                // Validar que el usuario solo pueda eliminar su propia cuenta (o sea admin)
                if (!authenticationService.canAccess(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(buildErrorResponse("No tienes permiso para eliminar esta cuenta"));
                }

                service.delete(userId);
                return ResponseEntity.noContent().build();
        }

        @Operation(summary = "User login", description = "Authenticates a user with email and password. Returns JWT token for subsequent requests.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @PostMapping("/login")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoguinReques request) {
                // Autenticar usuario
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword()));

                // Obtener datos del usuario
                UserAuth user = service.login(request.getEmail(), request.getPassword());

                // Generar token JWT con roles y userId
                UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
                Map<String, Object> extraClaims = new HashMap<>();
                extraClaims.put("roles", userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()));
                extraClaims.put("userId", user.getId());

                String jwtToken = jwtService.generateToken(extraClaims, userDetails);

                // Construir respuesta con token
                AuthResponse authResponse = AuthResponse.builder()
                                .token(jwtToken)
                                .tokenType("Bearer")
                                .expiresIn(jwtService.getExpirationTime())
                                .userId(user.getId())
                                .userName(user.getUserName())
                                .email(user.getEmail())
                                .dateBirth(user.getDateBirth())
                                .roles(user.getRoles())
                                .profileId(user.getProfileId())
                                .displayName(user.getDisplayName())
                                .preferredLanguage(user.getPreferredLanguage())
                                .avatarUrl(user.getAvatarUrl())
                                .bio(user.getBio())
                                .build();

                return ResponseEntity.ok(authResponse);
        }

        @Operation(summary = "Get current user info", description = "Returns information about the currently authenticated user", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
                        @ApiResponse(responseCode = "401", description = "Not authenticated")
        })
        @GetMapping("/me")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<UserResponse> getCurrentUser() {
                String email = authenticationService.getCurrentUserEmail();
                UserResponse user = service.findByEmail(email);
                return ResponseEntity.ok(user);
        }

        @Operation(summary = "Get user by ID", description = "Returns user information by ID. Users can only access their own info unless they are ADMIN.", security = @SecurityRequirement(name = "bearerAuth"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User info retrieved successfully"),
                        @ApiResponse(responseCode = "403", description = "Forbidden - Cannot access another user's info"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/{userId}")
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<?> getUserById(@PathVariable Integer userId) {
                // Validar que el usuario solo pueda ver su propia información (o sea admin)
                if (!authenticationService.canAccess(userId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(buildErrorResponse("No tienes permiso para ver esta información"));
                }

                UserResponse user = service.findById(userId);
                return ResponseEntity.ok(user);
        }

        // Método auxiliar para construir respuestas de error
        private java.util.Map<String, Object> buildErrorResponse(String message) {
                java.util.Map<String, Object> error = new java.util.HashMap<>();
                error.put("timestamp", java.time.Instant.now().toString());
                error.put("code", "FORBIDDEN");
                error.put("message", message);
                return error;
        }
}
package com.mediaflow.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mediaflow.api.dto.ProfileRequest;
import com.mediaflow.api.model.Profile;
import com.mediaflow.api.repository.ProfileRepository;
import com.mediaflow.api.service.AuthenticationService;
import com.mediaflow.api.service.ProfileService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    
    private final ProfileService service;
    private final AuthenticationService authenticationService;
    private final ProfileRepository profileRepository;

    @PutMapping("/{profileId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> update(
            @PathVariable Integer profileId, 
            @Valid @RequestBody ProfileRequest req) {
        
        // Obtener el profile para verificar a qué usuario pertenece
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + profileId));
        
        // Validar que el usuario solo pueda actualizar su propio perfil (o sea admin)
        if (!authenticationService.canAccess(profile.getUser().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para actualizar este perfil"));
        }
        
        return ResponseEntity.ok(service.update(profileId, req));
    }

    @DeleteMapping("/{profileId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> delete(@PathVariable Integer profileId) {
        // Obtener el profile para verificar a qué usuario pertenece
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + profileId));
        
        // Validar que el usuario solo pueda eliminar su propio perfil (o sea admin)
        if (!authenticationService.canAccess(profile.getUser().getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(buildErrorResponse("No tienes permiso para eliminar este perfil"));
        }
        
        service.delete(profileId);
        return ResponseEntity.noContent().build();
    }

    private java.util.Map<String, Object> buildErrorResponse(String message) {
        java.util.Map<String, Object> error = new java.util.HashMap<>();
        error.put("timestamp", java.time.Instant.now().toString());
        error.put("code", "FORBIDDEN");
        error.put("message", message);
        return error;
    }
}

package com.mediaflow.api.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.mediaflow.api.dto.RoleRequest;
import com.mediaflow.api.dto.RoleResponse;
import com.mediaflow.api.service.RoleService;

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
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", methods= {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Roles", description = "Role management endpoints - ADMIN only")
public class RoleController {
    private final RoleService service;

    @Operation(
        summary = "Get all roles",
        description = "Retrieves a list of all available roles in the system. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Roles retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(
        summary = "Get role by ID",
        description = "Retrieves a specific role by its ID. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> getById(@PathVariable Integer roleId) {
        return ResponseEntity.ok(service.findById(roleId));
    }

    @Operation(
        summary = "Create new role",
        description = "Creates a new role in the system. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Role created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "409", description = "Role already exists")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody RoleRequest req) {
        RoleResponse created = service.create(req);
        return ResponseEntity
                .created(URI.create("/api/v1/roles/" + created.getRoleId()))
                .body(created);
    }
    
    @Operation(
        summary = "Update role",
        description = "Updates an existing role. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Role updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable Integer roleId, 
            @Valid @RequestBody RoleRequest req) {
        return ResponseEntity.ok(service.update(roleId, req));
    }

    @Operation(
        summary = "Delete role",
        description = "Deletes a role from the system. Only accessible by administrators. Warning: This may affect users with this role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Requires ADMIN role"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "409", description = "Cannot delete role - still in use")
    })
    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Integer roleId) {
        service.delete(roleId);
    }
}

package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.RoleRequest;
import com.mediaflow.api.dto.RoleResponse;
import com.mediaflow.api.model.Role;

public final class RoleMapper {
    public static RoleResponse toResponse(Role role) {
        if (role == null)
            return null;
        return RoleResponse.builder()
                .roleId(role.getRoleId())
                .name(role.getName())
                .description(role.getDescription())
                .build();
    }

    public static Role toEntity(RoleRequest dto) {
        if (dto == null)
            return null;
        return Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public static void copyToEntity(RoleRequest dto, Role entity) {
        if (dto == null || entity == null)
            return;
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription()
        );
    }
}

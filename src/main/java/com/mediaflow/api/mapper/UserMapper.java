package com.mediaflow.api.mapper;

import java.util.stream.Collectors;

import com.mediaflow.api.dto.UserRequest;
import com.mediaflow.api.dto.UserResponse;
import com.mediaflow.api.model.Role;
import com.mediaflow.api.model.User;

public final class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .dateBirth(user.getDateBirth());

        if (user.getRoles() != null) {
            builder.roles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
        }

        if (user.getProfile() != null) {
            builder.profile(ProfileMapper.toResponse(user.getProfile()));
        }

        return builder.build();
    }

    public static User toEntity(UserRequest dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .dateBirth(dto.getDateBirth())
                .build();
    }

    public static void copyToEntity(UserRequest dto, User entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setDateBirth(dto.getDateBirth());
    }
}

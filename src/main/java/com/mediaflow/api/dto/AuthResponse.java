package com.mediaflow.api.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String tokenType = "Bearer";

    private Long expiresIn;

    private Integer userId;

    private String userName;

    private String email;

    private LocalDate dateBirth;

    private String[] roles;

    private Integer profileId;

    private String displayName;

    private String preferredLanguage;

    private String avatarUrl;

    private String bio;
}

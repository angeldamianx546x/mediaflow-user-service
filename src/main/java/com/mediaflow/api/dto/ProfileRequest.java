package com.mediaflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank
    @Size(max = 30)
    private String displayName;

    @NotBlank
    @Size(max = 30)
    private String preferredLanguage;

    @NotBlank
    private String avatarUrl;

    @NotBlank
    private String bio;
}
 
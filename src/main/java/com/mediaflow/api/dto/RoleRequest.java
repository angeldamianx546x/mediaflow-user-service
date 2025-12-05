package com.mediaflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RoleRequest {
    @NotBlank
    @Size(max = 30)
    private String name;

    @NotBlank
    @Size(max = 150)
    private String description;
}

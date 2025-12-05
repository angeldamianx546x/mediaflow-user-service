package com.mediaflow.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoguinReques {
    @NotBlank
    @Size(max = 70)
    private String email;
    @NotBlank
    @Size(max = 255)
    private String password;
}

package com.mediaflow.api.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    @Size(max = 70)
    private String name;

    @NotBlank
    @Email(message = "Email inválido")
    @Size(max = 70)
    private String email;

    @NotBlank
    @Size(max = 70)
    private String password;

    @NotNull
    private LocalDate dateBirth;

    private List<Integer> roles;

    @Size(max = 30)
    private String PreferredLanguage;

}

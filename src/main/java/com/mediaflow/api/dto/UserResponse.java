package com.mediaflow.api.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserResponse {
    Integer userId;
    String name;
    String email;
    LocalDate dateBirth;
    List<String> roles;
    ProfileResponse profile;
}

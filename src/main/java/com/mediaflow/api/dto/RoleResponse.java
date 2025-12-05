package com.mediaflow.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RoleResponse {
    @JsonProperty("role Id")
    Integer roleId;
    String name;
    String description;
}

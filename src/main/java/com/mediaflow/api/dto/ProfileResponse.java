package com.mediaflow.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfileResponse {
    @JsonProperty("profile Id")
    Integer profileId;
    @JsonProperty("display name")
    String displayName;
    @JsonProperty("preferred language")
    String preferredLanguage;
    @JsonProperty("avatar url")
    String avatarUrl;
    String bio;
}

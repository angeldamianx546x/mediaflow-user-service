package com.mediaflow.api.dto;

import java.time.LocalDate;

public interface UserAuth {
    Integer getId();
    String getUserName();
    String getEmail();
    LocalDate getDateBirth();
    String getPasswordHash();
    String[] getRoles();
    Integer getProfileId();
    String getDisplayName();
    String getPreferredLanguage();
    String getAvatarUrl();
    String getBio();
}

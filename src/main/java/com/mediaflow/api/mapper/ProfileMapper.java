package com.mediaflow.api.mapper;

import com.mediaflow.api.dto.ProfileRequest;
import com.mediaflow.api.dto.ProfileResponse;
import com.mediaflow.api.model.Profile;

public class ProfileMapper {
    public static ProfileResponse toResponse(Profile profile) {
        if (profile == null)
            return null;
        return ProfileResponse.builder()
                .profileId(profile.getProfileId())
                .displayName(profile.getDisplayName())
                .preferredLanguage(profile.getPreferredLanguage())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .build();
    }

    public static Profile toEntity(ProfileRequest dto) {
        if (dto == null)
            return null;
        return Profile.builder()
                .displayName(dto.getDisplayName())
                .preferredLanguage(dto.getPreferredLanguage())
                .avatarUrl(dto.getAvatarUrl())
                .bio(dto.getBio())
                .build();
    }

    public static void copyToEntity(ProfileRequest dto, Profile entity) {
        if (dto == null || entity == null)
            return;
        entity.setDisplayName(dto.getDisplayName());
        entity.setPreferredLanguage(dto.getPreferredLanguage());
        entity.setAvatarUrl(dto.getAvatarUrl());
        entity.setBio(dto.getBio());
    }
}

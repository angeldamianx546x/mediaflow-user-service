package com.mediaflow.api.service;

import com.mediaflow.api.dto.ProfileRequest;
import com.mediaflow.api.dto.ProfileResponse;
import com.mediaflow.api.model.Profile;
import com.mediaflow.api.model.User;

public interface ProfileService {
    ProfileResponse update(Integer profileId, ProfileRequest req);

    void delete(Integer profileId);

    Profile createDefaultProfile(User user);
}
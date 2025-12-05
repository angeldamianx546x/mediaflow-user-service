package com.mediaflow.api.service;

import org.springframework.stereotype.Service;

import com.mediaflow.api.dto.ProfileRequest;
import com.mediaflow.api.dto.ProfileResponse;
import com.mediaflow.api.mapper.ProfileMapper;
import com.mediaflow.api.model.Profile;
import com.mediaflow.api.model.User;
import com.mediaflow.api.repository.ProfileRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService{
    private final ProfileRepository repository;

    @Override
    public ProfileResponse update(Integer profileId, ProfileRequest req) {
        Profile existing = repository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found: " + profileId));
        ProfileMapper.copyToEntity(req, existing);
        Profile saved = repository.save(existing);
        return ProfileMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer profileId) {
        if (!repository.existsById(profileId)) {
            throw new EntityNotFoundException("Profile not found: " + profileId);
        }
        repository.deleteById(profileId);
    }

    @Override
    public Profile createDefaultProfile(User user) {
        Profile profile = Profile.builder()
                .displayName(user.getName())
                .preferredLanguage("es")
                .avatarUrl("https://media.istockphoto.com/id/1495088043/es/vector/icono-de-perfil-de-usuario-avatar-o-icono-de-persona-foto-de-perfil-s%C3%ADmbolo-de-retrato.jpg?s=612x612&w=0&k=20&c=mY3gnj2lU7khgLhV6dQBNqomEGj3ayWH-xtpYuCXrzk=")
                .bio("Nuevo usuario registrado.")
                .user(user)
                .build();

        return repository.save(profile);
    }
    
}

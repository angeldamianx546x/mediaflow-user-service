package com.mediaflow.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediaflow.api.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Integer>{
}

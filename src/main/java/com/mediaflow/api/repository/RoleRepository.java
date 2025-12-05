package com.mediaflow.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mediaflow.api.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    
}

package com.mediaflow.api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mediaflow.api.dto.RoleRequest;
import com.mediaflow.api.dto.RoleResponse;
import com.mediaflow.api.mapper.RoleMapper;
import com.mediaflow.api.model.Role;
import com.mediaflow.api.repository.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService{
    private final RoleRepository repository;
    @Override
    public List<RoleResponse> findAll() {
        return repository.findAll().stream()
                .map(RoleMapper::toResponse)
                .toList();
    }

    @Override
    public RoleResponse findById(Integer roleId) {
        Role role = repository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));
        return RoleMapper.toResponse(role);
    }

    @Override
    public RoleResponse create(RoleRequest req) {
        Role saved = repository.save(RoleMapper.toEntity(req));
        return RoleMapper.toResponse(saved);
    }

    @Override
    public RoleResponse update(Integer roleId, RoleRequest req) {
        Role existing = repository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));
        RoleMapper.copyToEntity(req, existing);
        Role saved = repository.save(existing);
        return RoleMapper.toResponse(saved);
    }

    @Override
    public void delete(Integer roleId) {
        if (!repository.existsById(roleId)) {
            throw new EntityNotFoundException("Role not found: " + roleId);
        }
        repository.deleteById(roleId);
    }
    
}

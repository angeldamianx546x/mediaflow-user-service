package com.mediaflow.api.service;

import java.util.List;

import com.mediaflow.api.dto.RoleRequest;
import com.mediaflow.api.dto.RoleResponse;

public interface RoleService {
    List<RoleResponse> findAll();

    RoleResponse findById(Integer roleId);

    RoleResponse create(RoleRequest req);

    RoleResponse update(Integer roleId, RoleRequest req);

    void delete(Integer roleId);
}

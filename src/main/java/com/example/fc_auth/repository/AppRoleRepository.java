package com.example.fc_auth.repository;

import com.example.fc_auth.model.AppRole;
import com.example.fc_auth.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByAppIdAndApiId(Long appId, Long apiId);
}
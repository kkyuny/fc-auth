package com.example.fc_auth.service;

import com.example.fc_auth.model.Department;
import com.example.fc_auth.repository.DepartmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentServcie {
    private final DepartmentRepository departmentRepository;

    public List<Department> listEmployees() {
        return departmentRepository.findAll();
    }
}

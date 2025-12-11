package com.example.fc_auth.controller;

import com.example.fc_auth.model.Department;
import com.example.fc_auth.service.DepartmentServcie;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Basics", description = "기본 관리 API")
@RequiredArgsConstructor
@RestController
public class DepartmentController {
    private final DepartmentServcie departmentServcie;

    @GetMapping(value = "departments")
    public ResponseEntity<List<Department>> listAll() {
        return new ResponseEntity<>(departmentServcie.listEmployees(), HttpStatus.OK);
    }

}

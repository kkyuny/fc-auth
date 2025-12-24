package com.example.fc_auth.service;

import com.example.fc_auth.dto.AppTokenRespDto;
import com.example.fc_auth.dto.ValidateTokenDto;
import com.example.fc_auth.model.Api;
import com.example.fc_auth.model.App;
import com.example.fc_auth.model.Department;
import com.example.fc_auth.repository.ApiRepository;
import com.example.fc_auth.repository.AppRepository;
import com.example.fc_auth.repository.DepartmentRepository;
import com.example.fc_auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final AppRepository appRepository;
    private final ApiRepository apiRepository;

    public AppTokenRespDto createAppToken(Long appId){
        App app = appRepository.getById(appId);
        String token = JwtUtil.createAppToken(app);
        return AppTokenRespDto.builder()
                .token(token)
                .build();
    }

    public ResponseEntity<String> validateToken(ValidateTokenDto dto) {
        Api api = apiRepository.findByMethodAndPath(dto.getMethod(), dto.getPath());
        return JwtUtil.validateAppToken(dto, api);
    }
}
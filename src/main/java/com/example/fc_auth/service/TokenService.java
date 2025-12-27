package com.example.fc_auth.service;

import com.example.fc_auth.config.CustomRateLimiter;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final AppRepository appRepository;
    private final ApiRepository apiRepository;
    private final CustomRateLimiter customRateLimiter;

    public AppTokenRespDto createAppToken(Long appId){
        App app = appRepository.getById(appId);
        String token = JwtUtil.createAppToken(app);
        return AppTokenRespDto.builder()
                .token(token)
                .build();
    }

    public ResponseEntity<String> validateToken(ValidateTokenDto dto) {
        Api api = apiRepository.findByMethodAndPath(dto.getMethod(), dto.getPath());
        ResponseEntity resp = JwtUtil.validateAppToken(dto, api);
        if(resp.getStatusCode().is2xxSuccessful()){
            Long appId = Long.valueOf(JwtUtil.parseSubject(dto.getToken()));
            if(!customRateLimiter.tryConsume(appId, api.getId())){
                log.error("TOO MANY REQUESTS");
                return new ResponseEntity<>("too many requests", HttpStatus.TOO_MANY_REQUESTS);
            }
        }
        return resp;
    }
}
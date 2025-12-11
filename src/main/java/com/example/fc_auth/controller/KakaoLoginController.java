package com.example.fc_auth.controller;

import com.example.fc_auth.service.KakaoService;
import com.example.fc_auth.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoLoginController {
    private final LoginService loginService;

    @GetMapping("/kakao/callback")
    public ResponseEntity callback(@RequestParam("code")String code) {
        return loginService.login(code);
    }
}

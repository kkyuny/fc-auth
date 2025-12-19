package com.example.fc_auth.controller;

import com.example.fc_auth.model.App;
import com.example.fc_auth.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name="Basics", description = "기본 관리 API")
public class AppController {
    private final AppService appService;

    @Operation(description = "전사 부서 조회")
    @GetMapping(value = "/apps",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<App>> listAll(){
        return new ResponseEntity<>(appService.listApps(), HttpStatus.OK);
    }
}
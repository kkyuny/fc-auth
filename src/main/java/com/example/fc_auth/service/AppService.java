package com.example.fc_auth.service;

import com.example.fc_auth.model.App;
import com.example.fc_auth.repository.AppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppService {
    private final AppRepository appRepository;

    public List<App> listApps(){
        return appRepository.findAll();
    }
}
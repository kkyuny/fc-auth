package com.example.fc_auth.repository;

import com.example.fc_auth.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, Long> {
}
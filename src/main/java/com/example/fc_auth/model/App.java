package com.example.fc_auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "123", description = "auto increment pk")
    private Long id;

    @Schema(example = "vacation", description = "시스템 이름")
    private String name;

    @OneToMany
    @JoinColumn(name="app_id")
    private List<Api> apis;

    @OneToMany
    @JoinColumn(name="app_id")
    private List<AppRole> appRoles;
}
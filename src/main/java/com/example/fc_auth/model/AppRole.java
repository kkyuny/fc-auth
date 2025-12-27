package com.example.fc_auth.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "123", description = "auto increment pk")
    private Long id;

    @OneToOne
    @JoinColumn(name = "api_id", referencedColumnName = "id")
    private Api api;

    @Column(name = "app_id")
    private Long appId;

    private Integer threshold;
}
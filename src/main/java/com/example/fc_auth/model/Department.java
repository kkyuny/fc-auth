package com.example.fc_auth.model;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String departmentName;
    @OneToOne
    @JoinColumn(name = "team_lead_id", referencedColumnName = "id")
    private Employee teamLead;
}

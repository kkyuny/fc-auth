package com.example.fc_auth.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Set;

@Getter
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Long departmentId;
    @ManyToMany
    @JoinTable(
            name = "employee_role_mapping",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),           // 현재 엔티티(Employee)의 FK
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")         // 반대 엔티티(Role)의 FK
    )
    private Set<Role> roles;
}

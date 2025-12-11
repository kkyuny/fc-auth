package com.example.fc_auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Long departmentId;
    private String nickname;
    @ManyToMany
    @JoinTable(
            name = "employee_role_mapping",
            joinColumns = @JoinColumn(name = "employee_id", referencedColumnName = "id"),           // 현재 엔티티(Employee)의 FK
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")         // 반대 엔티티(Role)의 FK
    )
    private Set<Role> roles;
}

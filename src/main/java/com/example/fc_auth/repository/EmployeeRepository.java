package com.example.fc_auth.repository;

import com.example.fc_auth.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByKakaoNickName(String nickName);
}

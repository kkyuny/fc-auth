package com.example.fc_auth.util;

import com.example.fc_auth.model.Employee;
import com.example.fc_auth.model.EmployeeRole;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JwtUtilTest {

    @Test
    public void test_nickname(){
        String testNick = "john doe";
        Employee employee = Employee.builder()
                .kakaoNickName(testNick)
                .build();

        String token = JwtUtil.createToken(employee);

        assertEquals(testNick, JwtUtil.parseToken(token).get("nickname"));
    }

    @Test
    public void test_role(){
        EmployeeRole employeeRole1 = EmployeeRole.builder()
                .id(1L)
                .name("role1")
                .build();
        EmployeeRole employeeRole2 = EmployeeRole.builder()
                .id(2L)
                .name("role2")
                .build();
        List<EmployeeRole> employeeRoles = Arrays.asList(employeeRole1, employeeRole2);
        Set<EmployeeRole> employeeRoleSet = new HashSet<>(employeeRoles);

        Employee employee = Employee.builder()
                .employeeRoles(employeeRoleSet)
                .build();

        String token = JwtUtil.createToken(employee);
        List res = JwtUtil.parseToken(token).get("roles", List.class);
        assertEquals(employeeRoleSet.size(), res.size());
        assertTrue(res.contains(employeeRole1.getName()));
        assertTrue(res.contains(employeeRole2.getName()));
    }
}
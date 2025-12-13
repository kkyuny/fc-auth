package com.example.fc_auth.util;

import com.example.fc_auth.model.Employee;
import com.example.fc_auth.model.Role;
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
        Role role1 = Role.builder()
                .id(1L)
                .name("role1")
                .build();
        Role role2 = Role.builder()
                .id(2L)
                .name("role2")
                .build();
        List<Role> roles = Arrays.asList(role1, role2);
        Set<Role> roleSet = new HashSet<>(roles);

        Employee employee = Employee.builder()
                .roles(roleSet)
                .build();

        String token = JwtUtil.createToken(employee);
        List res = JwtUtil.parseToken(token).get("roles", List.class);
        assertEquals(roleSet.size(), res.size());
        assertTrue(res.contains(role1.getName()));
        assertTrue(res.contains(role2.getName()));
    }
}
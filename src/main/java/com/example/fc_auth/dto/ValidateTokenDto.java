package com.example.fc_auth.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ValidateTokenDto {
    private String token;

    private Long app;

    private String method;

    private String path;
}
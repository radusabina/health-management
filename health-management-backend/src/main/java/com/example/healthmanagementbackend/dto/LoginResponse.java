package com.example.healthmanagementbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private UUID userId;
    private String accessToken;
    private String refreshToken;
}
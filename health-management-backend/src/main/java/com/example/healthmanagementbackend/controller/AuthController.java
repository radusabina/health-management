package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.LoginRequest;
import com.example.healthmanagementbackend.dto.LoginResponse;
import com.example.healthmanagementbackend.dto.RegisterRequest;
import com.example.healthmanagementbackend.dto.UserDto;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.service.UserService;
import com.example.healthmanagementbackend.service.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request.getEmail(), request.getPassword(), request.getFullName(),
                    request.getHeightCm(), request.getWeightKg(), request.getGender(), request.getAge());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getEmail(), request.getPassword());

            String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
            String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
            UserDto userDto = UserDto.builder().id(user.getId())
                    .email(user.getEmail()).password(user.getPassword())
                    .fullName(user.getFullName()).age(user.getAge()).gender(user.getGender())
                    .heightCm(user.getHeightCm()).weightKg(user.getWeightKg()).build();

            return ResponseEntity.ok(new LoginResponse(userDto, accessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Object> refresh(@RequestParam String refreshToken) {
        try {
            String email = jwtService.extractEmailFromRefresh(refreshToken);

            if (!jwtService.validateRefreshToken(refreshToken, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid or expired refresh token"));
            }

            UUID userId = jwtService.extractUserIdFromRefresh(refreshToken);

            String newAccessToken = jwtService.generateToken(userId, email);

            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
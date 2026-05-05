package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.exception.InvalidCredentialsException;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.model.enums.Gender;
import com.example.healthmanagementbackend.service.UserService;
import com.example.healthmanagementbackend.service.security.JwtService;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
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
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        userService.register(request.getEmail(), request.getPassword(), request.getFullName(),
                request.getHeightCm(), request.getWeightKg(), request.getGender(), request.getAge());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws InvalidCredentialsException {
        User user = userService.login(request.getEmail(), request.getPassword());

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        UserDto userDto = UserDto.builder()
                .id(user.getId()).email(user.getEmail()).password(user.getPassword())
                .fullName(user.getFullName()).age(user.getAge()).gender(user.getGender())
                .heightCm(user.getHeightCm()).weightKg(user.getWeightKg()).build();

        return ResponseEntity.ok(new LoginResponse(userDto, accessToken, refreshToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestParam String refreshToken) {
        String email = jwtService.extractEmailFromRefresh(refreshToken);

        if (!jwtService.validateRefreshToken(refreshToken, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }

        UUID userId = jwtService.extractUserIdFromRefresh(refreshToken);
        String newAccessToken = jwtService.generateToken(userId, email);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class LoginResponse {
        private UserDto user;
        private String accessToken;
        private String refreshToken;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private int age;
        private Gender gender;
        private int weightKg;
        private int heightCm;
    }

    @Getter @Setter @Builder
    public static class UserDto {
        private UUID id;
        private String email;
        private String password;
        private String fullName;
        private int age;
        private Gender gender;
        private int heightCm;
        private int weightKg;
    }
}

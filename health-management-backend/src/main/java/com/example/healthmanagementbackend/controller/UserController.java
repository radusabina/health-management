package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.UpdateUserRequest;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") UUID id, @RequestBody UpdateUserRequest request) {
        try {
            userService.updateUser(id, request.getEmail(),
                    request.getFullName(), request.getHeightCm(), request.getWeightKg(),
                    request.getGender(), request.getAge());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/is-password-valid")
    public ResponseEntity<Object> isPasswordValid(@RequestParam String password, @RequestParam UUID userId) {
        try {
            return ResponseEntity.ok().body(Map.of("valid", userService.isPasswordValid(password, userId)));
        }  catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/is-new-user/{userId}")
    public ResponseEntity<Object> isNewUser(@PathVariable UUID userId) {
        try {
            return ResponseEntity.ok().body(new IsNewUserResponse(userService.isNewUser(userId)));
        }  catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<Void> updatePassword(
            @PathVariable("id") UUID id,
            @RequestBody UpdatePasswordRequest request
    ) {
        userService.updatePassword(id, request.newPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(userService.deleteUser(id));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("type", e.getClass().getSimpleName(),
                        "message", e.getMessage()));
    }

    public record UpdatePasswordRequest(String newPassword) {}
    public record IsNewUserResponse(boolean isNewUser) {}
}

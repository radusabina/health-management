package com.example.healthmanagementbackend.dto;

import com.example.healthmanagementbackend.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UpdateUserRequest {

    @NotBlank(message = "User id must not be blank")
    private UUID userId;

    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotBlank(message = "Full name must not be blank")
    private String fullName;

    @NotBlank(message = "Age must not be blank")
    private int age;

    @NotBlank(message = "Gender must not be blank")
    private Gender gender;

    @NotBlank(message = "Height must not be blank")
    private int heightCm;

    @NotBlank(message = "Weight must not be blank")
    private int weightKg;
}

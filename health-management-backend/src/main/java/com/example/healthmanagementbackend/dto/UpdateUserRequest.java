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
    private String email;
    private String fullName;
    private int age;
    private Gender gender;
    private int heightCm;
    private int weightKg;
}

package com.example.healthmanagementbackend.dto;


import com.example.healthmanagementbackend.model.enums.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @Builder
public class UserDto {
    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private int age;
    private Gender gender;
    private int heightCm;
    private int weightKg;
}

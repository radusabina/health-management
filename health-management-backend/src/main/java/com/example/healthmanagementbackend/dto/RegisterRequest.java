package com.example.healthmanagementbackend.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class RegisterRequest {

    @Email
    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "Name must not be blank")
    @Size(min = 4)
    private String fullName;

    @NotBlank(message = "Age must not be blank")
    @Min(value = 18)
    private Integer age;

    @NotBlank(message = "Gender must not be blank")
    private String gender;

    @Column(name = "height_cm")
    @Max(value = 220)
    private Integer heightCm;
}
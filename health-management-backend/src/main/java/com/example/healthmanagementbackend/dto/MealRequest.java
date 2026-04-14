package com.example.healthmanagementbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class MealRequest {

    @NotBlank(message = "User id must not be blank")
    private UUID userId;

    @NotBlank(message = "Meal type must not be blank")
    private String mealType;

    @NotBlank(message = "Description must not be blank")
    private String description;

    @NotBlank(message = "Calories must not be blank")
    private int calories;
}

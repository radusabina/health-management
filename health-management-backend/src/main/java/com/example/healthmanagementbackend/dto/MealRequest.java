package com.example.healthmanagementbackend.dto;

import com.example.healthmanagementbackend.apininjas.dto.MealItemResponse;
import com.example.healthmanagementbackend.model.enums.MealType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MealRequest {

    @NotNull(message = "User id must not be blank")
    private UUID userId;

    @NotNull(message = "Meal type must not be blank")
    private MealType mealType;

    @NotNull(message = "Description must not be blank")
    private String description;

    @NotNull(message = "Meal items must contain at least one element")
    private List<MealItemResponse> items;
}

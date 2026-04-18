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
public class MealItemRequest {

    @NotBlank(message = "Meal id must not be blank")
    private UUID mealId;

    @NotBlank(message = "Food item name must not be blank")
    private String foodItemName;

    @NotBlank(message = "Quantity grams must not be blank")
    private int quantityGrams;
}

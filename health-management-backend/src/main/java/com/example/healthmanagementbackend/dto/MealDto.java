package com.example.healthmanagementbackend.dto;

import com.example.healthmanagementbackend.model.enums.MealType;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @Builder
public class MealDto {
    private MealType mealType;
    private String description;
    private LocalDate date;
    NutritionDto nutrition;
    private int totalCalories;
    private List<MealItemDto> items;
}
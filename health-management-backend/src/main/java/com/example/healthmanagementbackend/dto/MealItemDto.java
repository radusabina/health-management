package com.example.healthmanagementbackend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class MealItemDto {
    private String name;
    private double quantityGrams;
    private double sugarG;
    private double fiberG;
    private double sodiumMg;
    private double potassiumMg;
    private double fatSaturatedG;
    private double calories;
    private double cholesterolMg;
    private double proteinG;
    private double carbohydratesTotalG;
}
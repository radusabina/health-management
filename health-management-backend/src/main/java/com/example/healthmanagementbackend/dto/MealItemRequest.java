package com.example.healthmanagementbackend.dto;

import lombok.*;

@Getter @Setter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class MealItemRequest {
    private String name;
    private double quantityGrams;
}

package com.example.healthmanagementbackend.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "food_items")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FoodItem {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "sugar_g")
    private double sugarG;

    @Column(name = "quantity_grams")
    private double quantityGrams;

    @Column(name = "fiber_g")
    private double fiberG;

    @Column(name = "sodium_mg")
    private double sodiumMg;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "potassium_mg")
    private double potassiumMg;

    @Column(name = "fat_saturated_g")
    private double fatSaturatedG;

    @Column(name = "calories")
    private double calories;

    @Column(name = "cholesterol_mg")
    private double cholesterolMg;

    @Column(name = "protein_g")
    private double proteinG;

    @Column(name = "carbohydrates_total_g")
    private double carbohydratesTotalG;
}

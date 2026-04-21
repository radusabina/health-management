package com.example.healthmanagementbackend.apininjas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class FoodItemResponse {
    private String name;

    private Double calories;

    @JsonProperty("serving_size_g")
    private int quantityGrams;

    @JsonProperty("fat_saturated_g")
    private Double fatSaturatedG;

    @JsonProperty("protein_g")
    private Double proteinG;

    @JsonProperty("sodium_mg")
    private Double sodiumMg;

    @JsonProperty("potassium_mg")
    private Double potassiumMg;

    @JsonProperty("cholesterol_mg")
    private Double cholesterolMg;

    @JsonProperty("carbohydrates_total_g")
    private Double carbohydratesTotalG;

    @JsonProperty("fiber_g")
    private Double fiberG;

    @JsonProperty("sugar_g")
    private Double sugarG;
}

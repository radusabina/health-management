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
public class UpdateGeneralGoalRequest {

    @NotBlank(message = "General goal id must not be blank")
    private UUID generalGoalId;

    @NotBlank(message = "Calorie goal must not be blank")
    private int calorieGoal;

    @NotBlank(message = "Water goal must not be blank")
    private int waterGoal;

    @NotBlank(message = "Weight target must not be blank")
    private int weightTarget;
}

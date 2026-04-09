package com.example.healthmanagementbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DailyGoalRequest {

    @NotBlank(message = "Calories goal must not be blank")
    private int caloriesGoal;

    @NotBlank(message = "Steps goal must not be blank")
    private int stepsGoal;

    @NotBlank(message = "Water goal must not be blank")
    private int waterGoal;

    @NotBlank(message = "User id must not be blank")
    private UUID userId;

    @NotBlank(message = "Weight target must not be blank")
    private int weightTarget;
}

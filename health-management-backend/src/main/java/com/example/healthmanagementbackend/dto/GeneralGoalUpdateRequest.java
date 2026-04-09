package com.example.healthmanagementbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GeneralGoalUpdateRequest {

    @NotBlank(message = "Daily goal id must not be empty")
    @NotNull
    private UUID dailyGoalId;

    @NotBlank(message = "User id must not be empty")
    @NotNull
    private UUID userId;

    private int caloriesGoal;
    private int stepsGoal;
    private int waterGoal;
}

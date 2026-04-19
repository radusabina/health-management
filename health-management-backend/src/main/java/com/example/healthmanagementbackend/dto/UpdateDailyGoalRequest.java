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
public class UpdateDailyGoalRequest {

    @NotBlank(message = "Daily goal id must not be blank")
    private UUID id;

    @NotBlank(message = "Calories done must not be blank")
    private int caloriesDone;

    @NotBlank(message = "Water done must not be blank")
    private int waterDone;
}

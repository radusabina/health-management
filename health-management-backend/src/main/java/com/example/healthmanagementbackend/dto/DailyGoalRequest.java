package com.example.healthmanagementbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class DailyGoalRequest {

    @NotBlank(message = "User id must not be blank")
    private UUID userId;

    @NotBlank(message = "General goal id must not be blank")
    private UUID generalGoalId;

    private LocalDate date;
}

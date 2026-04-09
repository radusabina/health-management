package com.example.healthmanagementbackend.controller;

import com.example.healthmanagement.dto.GeneralGoalUpdateRequest;
import com.example.healthmanagement.service.DailyGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/daily-goal")
public class DailyGoalController {

    private final DailyGoalService dailyGoalService;

    public DailyGoalController(DailyGoalService dailyGoalService) {
        this.dailyGoalService = dailyGoalService;
    }

    @PostMapping("/add-daily-goal")
    public ResponseEntity<Object> addDailyGoals(@RequestBody GeneralGoalUpdateRequest request) {
        try {
            dailyGoalService.updateGeneralGoals(request.getDailyGoalId(), request.getUserId(), request.getCaloriesGoal(),
                    request.getStepsGoal(), request.getWaterGoal());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-general-goals")
    public ResponseEntity<Object> updateGeneralGoals(@RequestBody GeneralGoalUpdateRequest request) {
        try {
            dailyGoalService.updateGeneralGoals(request.getDailyGoalId(), request.getUserId(), request.getCaloriesGoal(),
                    request.getStepsGoal(), request.getWaterGoal());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

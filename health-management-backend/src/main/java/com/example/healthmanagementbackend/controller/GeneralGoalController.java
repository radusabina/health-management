package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.GeneralGoalRequest;
import com.example.healthmanagementbackend.dto.UpdateGeneralGoalRequest;
import com.example.healthmanagementbackend.model.GeneralGoal;
import com.example.healthmanagementbackend.service.GeneralGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/general-goal")
public class GeneralGoalController {

    private final GeneralGoalService generalGoalService;

    public GeneralGoalController(GeneralGoalService generalGoalService) {
        this.generalGoalService = generalGoalService;
    }

    @PostMapping("/add")
    public ResponseEntity<Object> add(@RequestBody GeneralGoalRequest request) {
        try {
            generalGoalService.addGeneralGoal(request.getCalorieGoal(), request.getStepsGoal(), request.getWaterGoal(),
                    request.getWeightTarget(), request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody UpdateGeneralGoalRequest request) {
        try {
            generalGoalService.updateGeneralGoal(request.getGeneralGoalId(), request.getCalorieGoal(), request.getStepsGoal(),
                    request.getWaterGoal(), request.getWeightTarget());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getGeneralGoalByUserId(@PathVariable UUID userId) {
        try {
            GeneralGoal generalGoal = generalGoalService.getGeneralGoalByUserId(userId);
            return ResponseEntity.ok(generalGoal);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(generalGoalService.deleteGeneralGoal(id));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("type", e.getClass().getSimpleName(),
                        "message", e.getMessage()));
    }
}

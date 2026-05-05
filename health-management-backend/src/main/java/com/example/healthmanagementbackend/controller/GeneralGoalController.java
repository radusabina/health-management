package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.model.GeneralGoal;
import com.example.healthmanagementbackend.service.GeneralGoalService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/general-goal")
public class GeneralGoalController {
    private final GeneralGoalService generalGoalService;

    public GeneralGoalController(GeneralGoalService generalGoalService) {
        this.generalGoalService = generalGoalService;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody GeneralGoalRequest request) {
        try {
            boolean alreadyExists = generalGoalService.existsForUser(request.getUserId());
            GeneralGoal generalGoal = generalGoalService.addGeneralGoal(request.getUserId(),
                    request.getCalorieGoal(), request.getWaterGoal(), request.getWeightTarget(),
                    request.getBottleAmountMl());

            return new ResponseEntity<>(mapToGeneralGoalDto(generalGoal), alreadyExists ? HttpStatus.OK : HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody UpdateGeneralGoalRequest request) {
        try {
            generalGoalService.updateGeneralGoal(request.getGeneralGoalId(), request.getCalorieGoal(),
                    request.getWaterGoal(), request.getWeightTarget(), request.getBottleAmountMl());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<Object> getGeneralGoalByUserId(@PathVariable("userId") UUID userId) {
        try {
            GeneralGoal generalGoal = generalGoalService.getGeneralGoalByUserId(userId);
            return new ResponseEntity<>(mapToGeneralGoalDto(generalGoal), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getGeneralGoalById(@PathVariable("id") UUID id) {
        try {
            GeneralGoal generalGoal = generalGoalService.getGeneralGoalById(id);
            return new ResponseEntity<>(mapToGeneralGoalDto(generalGoal), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/updateGeneralGoalForUser")
    public ResponseEntity<Object> updateGeneralGoalForUser(@RequestBody UpdateGeneralGoalForUserRequest req) {
        try {
            generalGoalService.updateGeneralGoalForUser(req.getUserId(), req.getCalorieGoal(),
                    req.getWaterGoal(), req.getWeightTarget(), req.getBottleAmountMl());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(generalGoalService.deleteGeneralGoal(id));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<Object> deleteGeneralGoalForUser(@PathVariable("userId") UUID userId) {
        try {
            return ResponseEntity.ok(generalGoalService.deleteGeneralGoalForUser(userId));
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

    private GeneralGoalDto mapToGeneralGoalDto(GeneralGoal generalGoal) {
        return GeneralGoalDto.builder()
                .id(generalGoal.getId())
                .calorieGoal(generalGoal.getCalorieGoal())
                .waterGoal(generalGoal.getWaterGoal())
                .weightTarget(generalGoal.getWeightTarget())
                .bottleAmountMl(generalGoal.getBottleAmountMl())
                .build();
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter @Builder
    public static class GeneralGoalDto {
        private UUID id;
        private int calorieGoal;
        private int waterGoal;
        private int weightTarget;
        private int bottleAmountMl;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class UpdateGeneralGoalRequest {
        private UUID generalGoalId;
        private int calorieGoal;
        private int waterGoal;
        private int weightTarget;
        private int bottleAmountMl;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class GeneralGoalRequest {
        private UUID userId;
        private int calorieGoal;
        private int waterGoal;
        private int weightTarget;
        private int bottleAmountMl;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class UpdateGeneralGoalForUserRequest {
        private UUID userId;
        private int calorieGoal;
        private int waterGoal;
        private int weightTarget;
        private int bottleAmountMl;
    }

}

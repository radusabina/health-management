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

import java.util.UUID;

@RestController
@RequestMapping("/api/general-goal")
public class GeneralGoalController {
    private final GeneralGoalService generalGoalService;

    public GeneralGoalController(GeneralGoalService generalGoalService) {
        this.generalGoalService = generalGoalService;
    }

    @PostMapping
    public ResponseEntity<GeneralGoalDto> add(@RequestBody GeneralGoalRequest request) {
        boolean alreadyExists = generalGoalService.existsForUser(request.getUserId());
        GeneralGoal generalGoal = generalGoalService.addGeneralGoal(
                request.getUserId(), request.getCalorieGoal(), request.getWaterGoal(),
                request.getWeightTarget(), request.getBottleAmountMl());
        return new ResponseEntity<>(mapToGeneralGoalDto(generalGoal), alreadyExists ? HttpStatus.OK : HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateGeneralGoalRequest request) {
        generalGoalService.updateGeneralGoal(request.getGeneralGoalId(), request.getCalorieGoal(),
                request.getWaterGoal(), request.getWeightTarget(), request.getBottleAmountMl());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<GeneralGoalDto> getGeneralGoalByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(mapToGeneralGoalDto(generalGoalService.getGeneralGoalByUserId(userId)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralGoalDto> getGeneralGoalById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mapToGeneralGoalDto(generalGoalService.getGeneralGoalById(id)));
    }

    @PutMapping("/updateGeneralGoalForUser")
    public ResponseEntity<Void> updateGeneralGoalForUser(@RequestBody UpdateGeneralGoalForUserRequest req) {
        generalGoalService.updateGeneralGoalForUser(req.getUserId(), req.getCalorieGoal(),
                req.getWaterGoal(), req.getWeightTarget(), req.getBottleAmountMl());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(generalGoalService.deleteGeneralGoal(id));
    }

    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<Boolean> deleteGeneralGoalForUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(generalGoalService.deleteGeneralGoalForUser(userId));
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

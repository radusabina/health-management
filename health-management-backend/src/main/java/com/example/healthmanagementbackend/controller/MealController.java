package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.AnalyzeResponse;
import com.example.healthmanagementbackend.dto.MealDto;
import com.example.healthmanagementbackend.dto.MealItemRequest;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.enums.MealType;
import com.example.healthmanagementbackend.service.MealService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meal")
public class MealController {
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody MealRequest request) {
        Meal meal = mealService.addMeal(request.getMealType(), request.getDescription(), request.getUserId(), request.getItems());
        return new ResponseEntity<>(meal, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateMealRequest request) {
        mealService.updateMeal(request.getMealId(), request.getMealType(), request.getDescription());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealDto>> getMealsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(mealService.getMealsForUser(userId));
    }

    @GetMapping
    public ResponseEntity<List<MealDto>> getMealsForUserByType(@RequestParam("userId") UUID userId,
                                                               @RequestParam("mealType") MealType mealType) {
        return ResponseEntity.ok(mealService.getMealsForUserByType(userId, mealType));
    }

    @GetMapping("/today")
    public ResponseEntity<List<MealDto>> getTodayMealsForUser(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(mealService.getTodayMealsForUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDto> getMealById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mealService.getMealById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(mealService.deleteMeal(id));
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyze(@RequestParam String description) {
        return ResponseEntity.ok(mealService.analyzeMeal(description));
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class MealRequest {
        private UUID userId;
        private MealType mealType;
        private String description;
        private List<MealItemRequest> items;
    }

    @NoArgsConstructor @AllArgsConstructor
    @Getter @Setter
    public static class UpdateMealRequest {
        private UUID mealId;
        private MealType mealType;
        private String description;
    }
}

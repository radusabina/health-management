package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.MealRequest;
import com.example.healthmanagementbackend.dto.UpdateMealRequest;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.service.MealService;
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

import java.util.List;
import java.util.Map;
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
        try {
            mealService.addMeal(request.getMealType(), request.getDescription(), request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> update(@RequestBody UpdateMealRequest request) {
        try {
            mealService.updateMeal(request.getMealId(), request.getMealType(), request.getDescription());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getMealsByUserId(@PathVariable UUID userId) {
        try {
            List<Meal> meal = mealService.getMealsByUserId(userId);
            return ResponseEntity.ok(meal);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(mealService.deleteMeal(id));
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

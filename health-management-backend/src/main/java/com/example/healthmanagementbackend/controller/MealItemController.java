package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.dto.MealItemRequest;
import com.example.healthmanagementbackend.model.MealItem;
import com.example.healthmanagementbackend.service.MealItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/meal-item")
public class MealItemController {

    private MealItemService mealItemService;

    public MealItemController(MealItemService mealItemService) {
        this.mealItemService = mealItemService;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody MealItemRequest req) {
        try {
            MealItem mealItem = mealItemService.addMealItemToMeal(req.getMealId(), req.getFoodItemName(), req.getQuantityGrams());
            return new ResponseEntity<>(mealItem, HttpStatus.CREATED);
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

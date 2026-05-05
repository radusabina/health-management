package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.apininjas.dto.MealItemResponse;
import com.example.healthmanagementbackend.model.FoodItem;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.MealItem;
import com.example.healthmanagementbackend.model.enums.MealType;
import com.example.healthmanagementbackend.service.MealService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
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

import java.time.LocalDate;
import java.util.ArrayList;
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
    public ResponseEntity<MealDto> add(@RequestBody MealRequest request) {
        Meal meal = mealService.addMeal(request.getMealType(), request.getDescription(),
                request.getUserId(), request.getItems());
        return new ResponseEntity<>(mapToDto(meal), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody UpdateMealRequest request) {
        mealService.updateMeal(request.getMealId(), request.getMealType(), request.getDescription());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MealDto>> getMealsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(mealService.getMealsForUser(userId).stream().map(this::mapToDto).toList());
    }

    @GetMapping
    public ResponseEntity<List<MealDto>> getMealsForUserByType(@RequestParam("userId") UUID userId,
                                                               @RequestParam("mealType") MealType mealType) {
        return ResponseEntity.ok(mealService.getMealsForUserByType(userId, mealType).stream().map(this::mapToDto).toList());
    }

    @GetMapping("/today")
    public ResponseEntity<List<MealDto>> getTodayMealsForUser(@RequestParam("userId") UUID userId) {
        return ResponseEntity.ok(mealService.getTodayMealsForUser(userId).stream().map(this::mapToDto).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDto> getMealById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mapToDto(mealService.getMealById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable UUID id) {
        return ResponseEntity.ok(mealService.deleteMeal(id));
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalyzeResponse> analyze(@RequestParam String description) {
        return ResponseEntity.ok(mapToAnalyzeResponse(mealService.analyzeMeal(description)));
    }

    // ── mappers ───────────────────────────────────────────────────────────────

    private MealDto mapToDto(Meal meal) {
        List<MealItemDto> itemDtos = new ArrayList<>();
        double totalCalories = 0;

        for (MealItem item : meal.getItems()) {
            FoodItem food = item.getFoodItem();
            double factor = item.getQuantityGrams() / 100.0;

            double itemCalories    = roundDecimalCustom(food.getCalories()             * factor);
            double itemSugar       = roundDecimalCustom(food.getSugarG()               * factor);
            double itemFiber       = roundDecimalCustom(food.getFiberG()               * factor);
            double itemSodium      = roundDecimalCustom(food.getSodiumMg()             * factor);
            double itemPotassium   = roundDecimalCustom(food.getPotassiumMg()          * factor);
            double itemFatSat      = roundDecimalCustom(food.getFatSaturatedG()        * factor);
            double itemCholesterol = roundDecimalCustom(food.getCholesterolMg()        * factor);
            double itemProtein     = roundDecimalCustom(food.getProteinG()             * factor);
            double itemCarbs       = roundDecimalCustom(food.getCarbohydratesTotalG()  * factor);

            itemDtos.add(MealItemDto.builder()
                    .name(food.getName())
                    .quantityGrams(item.getQuantityGrams())
                    .calories(itemCalories)
                    .sugarG(itemSugar)
                    .fiberG(itemFiber)
                    .sodiumMg(itemSodium)
                    .potassiumMg(itemPotassium)
                    .fatSaturatedG(itemFatSat)
                    .cholesterolMg(itemCholesterol)
                    .proteinG(itemProtein)
                    .carbohydratesTotalG(itemCarbs)
                    .build());

            totalCalories += itemCalories;
        }

        return MealDto.builder()
                .mealType(meal.getMealType())
                .description(meal.getDescription())
                .date(meal.getDate())
                .totalCalories((int) Math.round(totalCalories))
                .items(itemDtos)
                .build();
    }

    private AnalyzeResponse mapToAnalyzeResponse(List<MealItemResponse> items) {
        return AnalyzeResponse.builder()
                .items(items.stream()
                        .map(item -> AnalyzeItem.builder()
                                .name(item.getName())
                                .quantityGrams(item.getQuantityGrams())
                                .build())
                        .toList())
                .build();
    }

    private double roundDecimalCustom(double value) {
        double decimal = value - Math.floor(value);
        decimal = Math.round(decimal * 1000.0) / 1000.0;

        if (decimal < 0.1) return Math.floor(value);
        if (decimal > 0.9) return Math.ceil(value);
        return Math.round(value * 10.0) / 10.0;
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

    @Getter @Setter @Builder
    public static class MealDto {
        private MealType mealType;
        private String description;
        private LocalDate date;
        NutritionDto nutrition;
        private int totalCalories;
        private List<MealItemDto> items;
    }

    @Getter @Setter @Builder
    public static class MealItemDto {
        private String name;
        private double quantityGrams;
        private double sugarG;
        private double fiberG;
        private double sodiumMg;
        private double potassiumMg;
        private double fatSaturatedG;
        private double calories;
        private double cholesterolMg;
        private double proteinG;
        private double carbohydratesTotalG;
    }

    @Getter @Setter @Builder
    public static class AnalyzeResponse {
        List<AnalyzeItem> items;
    }

    @Getter @Setter @Builder
    public static class AnalyzeItem {
        private String name;
        private int quantityGrams;
    }

    @Getter @Setter @Builder
    public static class NutritionDto {
        private double sugarG;
        private double fiberG;
        private double sodiumMg;
        private double potassiumMg;
        private double fatSaturatedG;
        private double calories;
        private double cholesterolMg;
        private double proteinG;
        private double carbohydratesTotalG;
    }

    @Getter @Setter @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MealItemRequest {
        private String name;
        private double quantityGrams;
    }
}

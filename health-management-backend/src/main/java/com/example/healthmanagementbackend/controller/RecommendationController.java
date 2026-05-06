package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.model.Recommendation;
import com.example.healthmanagementbackend.model.RecommendationIngredient;
import com.example.healthmanagementbackend.model.RecommendationStep;
import com.example.healthmanagementbackend.service.RecommendationService;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * GET /api/recommendations
     *
     * @param includeIngredients comma-separated ingredients to include (required)
     * @param excludeIngredients comma-separated ingredients to exclude (optional)
     * @param maxCalories        max calories per serving (optional)
     */
    @GetMapping
    public ResponseEntity<List<RecommendationDto>> getRecommendations(
            @RequestParam String includeIngredients,
            @RequestParam(required = false) String excludeIngredients,
            @RequestParam(required = false) Integer maxCalories) {

        List<Recommendation> recommendations =
                recommendationService.getRecommendations(includeIngredients, excludeIngredients, maxCalories);

        return ResponseEntity.ok(recommendations.stream().map(this::mapToDto).toList());
    }

    // ── mappers ───────────────────────────────────────────────────────────────

    private RecommendationDto mapToDto(Recommendation r) {
        return RecommendationDto.builder()
                .spoonacularId(r.getSpoonacularId())
                .title(r.getTitle())
                .summary(r.getSummary())
                .imageUrl(r.getImageUrl())
                .readyInMinutes(r.getReadyInMinutes())
                .servings(r.getServings())
                .healthScore(r.getHealthScore())
                .totalCalories(r.getTotalCalories())
                .percentProtein(r.getPercentProtein())
                .percentFat(r.getPercentFat())
                .percentCarbs(r.getPercentCarbs())
                .cuisines(r.getCuisines())
                .ingredients(r.getIngredients().stream().map(this::mapIngredient).toList())
                .steps(r.getSteps().stream().map(this::mapStep).toList())
                .build();
    }

    private IngredientDto mapIngredient(RecommendationIngredient i) {
        return IngredientDto.builder()
                .name(i.getName())
                .amount(i.getAmount())
                .unit(i.getUnit())
                .build();
    }

    private StepDto mapStep(RecommendationStep s) {
        return StepDto.builder()
                .stepNumber(s.getStepNumber())
                .stepText(s.getStepText())
                .build();
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    @Getter @Setter @Builder
    public static class RecommendationDto {
        private int spoonacularId;
        private String title;
        private String summary;
        private String imageUrl;
        private int readyInMinutes;
        private int servings;
        private double healthScore;
        private double totalCalories;
        private double percentProtein;
        private double percentFat;
        private double percentCarbs;
        private List<String> cuisines;
        private List<IngredientDto> ingredients;
        private List<StepDto> steps;
    }

    @Getter @Setter @Builder
    public static class IngredientDto {
        private String name;
        private double amount;
        private String unit;
    }

    @Getter @Setter @Builder
    public static class StepDto {
        private int stepNumber;
        private String stepText;
    }
}

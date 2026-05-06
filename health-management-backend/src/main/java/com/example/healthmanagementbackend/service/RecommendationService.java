package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.api.SpoonacularClient;
import com.example.healthmanagementbackend.api.SpoonacularClient.*;
import com.example.healthmanagementbackend.model.Recommendation;
import com.example.healthmanagementbackend.model.RecommendationIngredient;
import com.example.healthmanagementbackend.model.RecommendationStep;
import com.example.healthmanagementbackend.repository.RecommendationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class RecommendationService {

    private static final Logger LOGGER = Logger.getLogger(RecommendationService.class.getName());

    private final SpoonacularClient spoonacularClient;
    private final RecommendationRepository recommendationRepository;

    public RecommendationService(SpoonacularClient spoonacularClient,
                                 RecommendationRepository recommendationRepository) {
        this.spoonacularClient = spoonacularClient;
        this.recommendationRepository = recommendationRepository;
    }

    private static final int CACHE_HIT_THRESHOLD = 5;
    private static final int MAX_CACHE_RESULTS   = 50;

    @Transactional
    public List<Recommendation> getRecommendations(String includeIngredients,
                                                    String excludeIngredients,
                                                    Integer maxCalories) {
        List<String> ingredientNames = parseIngredients(includeIngredients);

        List<Recommendation> cached = recommendationRepository.findByAnyIngredient(
                ingredientNames, PageRequest.of(0, MAX_CACHE_RESULTS));

        if (cached.size() >= CACHE_HIT_THRESHOLD) {
            LOGGER.info("Cache satisfied: " + cached.size() + " recommendations found for " + ingredientNames);
            return cached;
        }

        LOGGER.info("Cache miss (" + cached.size() + " found), querying Spoonacular for " + ingredientNames);
        List<SpoonacularRecipe> recipes =
                spoonacularClient.searchRecipes(includeIngredients, excludeIngredients, maxCalories);

        List<Recommendation> result = new ArrayList<>();
        for (SpoonacularRecipe recipe : recipes) {
            Optional<Recommendation> existing = recommendationRepository.findBySpoonacularId(recipe.getId());
            if (existing.isPresent()) {
                result.add(existing.get());
            } else {
                result.add(mapAndSave(recipe));
            }
        }
        return result;
    }

    // private helpers
    private List<String> parseIngredients(String includeIngredients) {
        if (includeIngredients == null || includeIngredients.isBlank()) return List.of();
        return Arrays.stream(includeIngredients.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private Recommendation mapAndSave(SpoonacularRecipe recipe) {
        Recommendation recommendation = Recommendation.builder()
                .spoonacularId(recipe.getId())
                .title(recipe.getTitle())
                .summary(recipe.getSummary())
                .imageUrl(recipe.getImage())
                .readyInMinutes(recipe.getReadyInMinutes())
                .servings(recipe.getServings())
                .healthScore(recipe.getHealthScore())
                .totalCalories(extractTotalCalories(recipe))
                .cuisines(recipe.getCuisines() != null ? recipe.getCuisines() : new ArrayList<>())
                .percentProtein(extractCaloricBreakdown(recipe, "protein"))
                .percentFat(extractCaloricBreakdown(recipe, "fat"))
                .percentCarbs(extractCaloricBreakdown(recipe, "carbs"))
                .cachedAt(LocalDateTime.now())
                .build();

        recommendation.setIngredients(buildIngredients(recipe, recommendation));
        recommendation.setSteps(buildSteps(recipe, recommendation));

        return recommendationRepository.save(recommendation);
    }

    /**
     * Returns up to {@code count} randomly ordered cached recommendations.
     * Used for the initial page load before the user performs a search.
     */
    @Transactional(readOnly = true)
    public List<Recommendation> getRandomCached(int count) {
        List<Recommendation> all = new ArrayList<>(recommendationRepository.findAll());
        Collections.shuffle(all);
        return all.stream().limit(count).toList();
    }

    private double extractTotalCalories(SpoonacularRecipe recipe) {
        if (recipe.getNutrition() == null || recipe.getNutrition().getNutrients() == null) {
            return 0.0;
        }
        return recipe.getNutrition().getNutrients().stream()
                .filter(n -> "Calories".equals(n.getName()))
                .mapToDouble(SpoonacularNutrient::getAmount)
                .findFirst()
                .orElse(0.0);
    }

    private List<RecommendationIngredient> buildIngredients(SpoonacularRecipe recipe,
                                                             Recommendation recommendation) {
        List<RecommendationIngredient> result = new ArrayList<>();
        if (recipe.getNutrition() == null || recipe.getNutrition().getIngredients() == null) {
            return result;
        }
        for (SpoonacularIngredient si : recipe.getNutrition().getIngredients()) {
            result.add(RecommendationIngredient.builder()
                    .recommendation(recommendation)
                    .name(si.getName())
                    .amount(si.getAmount())
                    .unit(si.getUnit() != null ? si.getUnit() : "")
                    .build());
        }
        return result;
    }

    private List<RecommendationStep> buildSteps(SpoonacularRecipe recipe,
                                                  Recommendation recommendation) {
        List<RecommendationStep> result = new ArrayList<>();
        if (recipe.getAnalyzedInstructions() == null) return result;

        for (SpoonacularInstructionGroup group : recipe.getAnalyzedInstructions()) {
            if (group.getSteps() == null) continue;
            for (SpoonacularStep s : group.getSteps()) {
                result.add(RecommendationStep.builder()
                        .recommendation(recommendation)
                        .stepNumber(s.getNumber())
                        .stepText(s.getStep())
                        .build());
            }
        }
        return result;
    }

    private double extractCaloricBreakdown(SpoonacularRecipe recipe, String type) {
        if (recipe.getNutrition() == null || recipe.getNutrition().getCaloricBreakdown() == null) {
            return 0.0;
        }
        SpoonacularCaloricBreakdown cb = recipe.getNutrition().getCaloricBreakdown();
        return switch (type) {
            case "protein" -> cb.getPercentProtein();
            case "fat"     -> cb.getPercentFat();
            case "carbs"   -> cb.getPercentCarbs();
            default        -> 0.0;
        };
    }
}

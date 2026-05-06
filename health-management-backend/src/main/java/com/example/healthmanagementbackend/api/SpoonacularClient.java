package com.example.healthmanagementbackend.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class SpoonacularClient {

    private static final String BASE_URL = "https://api.spoonacular.com/recipes/complexSearch";
    private static final Logger LOGGER = Logger.getLogger(SpoonacularClient.class.getName());

    private final String apiKey;
    private final RestClient restClient;

    public SpoonacularClient(@Value("${spoonacular.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    /**
     * Search for recipe recommendations.
     *
     * @param includeIngredients comma-separated list of ingredients to include (required)
     * @param excludeIngredients comma-separated list of ingredients to exclude (optional)
     * @param maxCalories        maximum calories per serving (optional)
     * @return list of raw Spoonacular recipe results with nutrition data
     */
    public List<SpoonacularRecipe> searchRecipes(String includeIngredients,
                                                  String excludeIngredients,
                                                  Integer maxCalories) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(BASE_URL)
                .queryParam("apiKey", apiKey)
                .queryParam("includeIngredients", includeIngredients)
                .queryParam("instructionsRequired", true)
                .queryParam("addRecipeNutrition", true)
                .queryParam("maxServings", 4)
                .queryParam("number", 10);

        if (excludeIngredients != null && !excludeIngredients.isBlank()) {
            builder.queryParam("excludeIngredients", excludeIngredients);
        }
        if (maxCalories != null) {
            builder.queryParam("maxCalories", maxCalories);
        }

        String url = builder.build().toUriString();

        SpoonacularSearchResponse response = restClient.get()
                .uri(url)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(SpoonacularSearchResponse.class);

        if (response == null || response.getResults() == null) {
            LOGGER.warning("Spoonacular returned null response for includeIngredients=" + includeIngredients);
            return List.of();
        }

        LOGGER.info("Spoonacular returned " + response.getResults().size() + " recipes");
        return response.getResults();
    }

    // ── internal DTOs ─────────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularSearchResponse {
        private List<SpoonacularRecipe> results = new ArrayList<>();
        private int offset;
        private int number;
        private int totalResults;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularRecipe {
        private int id;
        private String title;
        private String image;
        private int readyInMinutes;
        private int servings;
        private double healthScore;
        private String summary;
        private List<String> cuisines = new ArrayList<>();
        private SpoonacularNutrition nutrition;
        private List<SpoonacularInstructionGroup> analyzedInstructions = new ArrayList<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularNutrition {
        private List<SpoonacularNutrient> nutrients = new ArrayList<>();
        private List<SpoonacularIngredient> ingredients = new ArrayList<>();
        private SpoonacularCaloricBreakdown caloricBreakdown;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularNutrient {
        private String name;
        private double amount;
        private String unit;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularIngredient {
        private int id;
        private String name;
        private double amount;
        private String unit;
        private List<SpoonacularNutrient> nutrients = new ArrayList<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularCaloricBreakdown {
        private double percentProtein;
        private double percentFat;
        private double percentCarbs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularInstructionGroup {
        private String name;
        private List<SpoonacularStep> steps = new ArrayList<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    public static class SpoonacularStep {
        private int number;
        private String step;
    }
}

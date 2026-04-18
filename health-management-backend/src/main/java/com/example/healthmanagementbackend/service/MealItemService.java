package com.example.healthmanagementbackend.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.healthmanagementbackend.exception.NoMealFoundException;
import com.example.healthmanagementbackend.model.FoodItem;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.MealItem;
import com.example.healthmanagementbackend.repository.FoodItemRepository;
import com.example.healthmanagementbackend.repository.MealItemRepository;
import com.example.healthmanagementbackend.repository.MealRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MealItemService {

    private static final Logger LOGGER = Logger.getLogger(MealItemService.class.getName());
    private static final String CALORIE_NINJAS_API_URL = "https://api.calorieninjas.com/v1/nutrition";

    private final MealItemRepository mealItemRepository;
    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final RestClient restClient;
    private final String calorieNinjasApiKey;

    public MealItemService(
            MealItemRepository mealItemRepository,
            MealRepository mealRepository,
            FoodItemRepository foodItemRepository,
            @Value("${calorieninjas.api.key:}") String calorieNinjasApiKey
    ) {
        this.mealItemRepository = mealItemRepository;
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.calorieNinjasApiKey = calorieNinjasApiKey;
        this.restClient = RestClient.create();
    }

    public MealItem addMealItemToMeal(UUID mealId, String foodItemName, int quantityGrams) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));
        FoodItem foodItem = findOrCreateFoodItem(foodItemName);

        MealItem mealItem = MealItem.builder()
                .meal(meal)
                .foodItem(foodItem)
                .quantityGrams(quantityGrams)
                .build();

        MealItem savedMealItem = mealItemRepository.save(mealItem);
        LOGGER.info("Meal item added to meal: " + mealId + " using food item: " + foodItem.getName());
        return savedMealItem;
    }

    private FoodItem findOrCreateFoodItem(String foodItemName) {
        String normalizedFoodItemName = foodItemName == null ? "" : foodItemName.trim();
        if (normalizedFoodItemName.isBlank()) {
            throw new IllegalArgumentException("Food item name must not be blank");
        }

        return foodItemRepository.findByNameIgnoreCase(normalizedFoodItemName)
                .orElseGet(() -> fetchAndStoreFoodItem(normalizedFoodItemName));
    }

    private FoodItem fetchAndStoreFoodItem(String foodItemName) {
        if (calorieNinjasApiKey == null || calorieNinjasApiKey.isBlank()) {
            throw new IllegalStateException("CalorieNinjas API key is not configured");
        }

        String url = UriComponentsBuilder.fromUriString(CALORIE_NINJAS_API_URL)
                .queryParam("query", foodItemName)
                .toUriString();

        CalorieNinjasResponse response = restClient.get()
                .uri(url)
                .header("X-Api-Key", calorieNinjasApiKey)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(CalorieNinjasResponse.class);

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            throw new IllegalArgumentException("No nutrition information found for food item: " + foodItemName);
        }

        CalorieNinjasItem firstItem = response.getItems().get(0);
        if (firstItem.getCalories() == null || firstItem.getServingSizeG() == null || firstItem.getServingSizeG() <= 0) {
            throw new IllegalArgumentException("Nutrition data for food item is incomplete: " + foodItemName);
        }

        int caloriesPer100g = (int) Math.round((firstItem.getCalories() * 100) / firstItem.getServingSizeG());
        FoodItem foodItem = FoodItem.builder()
                .name(firstItem.getName() != null && !firstItem.getName().isBlank() ? firstItem.getName() : foodItemName)
                .caloriesPer100g(caloriesPer100g)
                .build();

        FoodItem savedFoodItem = foodItemRepository.save(foodItem);
        LOGGER.info("Food item fetched from CalorieNinjas and saved locally: " + savedFoodItem.getName());
        return savedFoodItem;
    }

    private static class CalorieNinjasResponse {
        private List<CalorieNinjasItem> items;

        public List<CalorieNinjasItem> getItems() {
            return items;
        }

        public void setItems(List<CalorieNinjasItem> items) {
            this.items = items;
        }
    }

    private static class CalorieNinjasItem {
        private String name;
        private Double calories;
        @JsonProperty("serving_size_g")
        private Double servingSizeG;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getCalories() {
            return calories;
        }

        public void setCalories(Double calories) {
            this.calories = calories;
        }

        public Double getServingSizeG() {
            return servingSizeG;
        }

        public void setServingSizeG(Double servingSizeG) {
            this.servingSizeG = servingSizeG;
        }
    }
}

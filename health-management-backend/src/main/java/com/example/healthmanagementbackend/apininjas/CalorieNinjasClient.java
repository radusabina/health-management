package com.example.healthmanagementbackend.apininjas;

import com.example.healthmanagementbackend.apininjas.dto.FoodItemResponse;
import com.example.healthmanagementbackend.apininjas.dto.MealItemResponse;
import com.example.healthmanagementbackend.model.FoodItem;
import com.example.healthmanagementbackend.repository.FoodItemRepository;
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
public class CalorieNinjasClient {

    private static final String API_URL = "https://api.calorieninjas.com/v1/nutrition";
    private static final Logger LOGGER = Logger.getLogger(CalorieNinjasClient.class.getName());

    private final FoodItemRepository foodItemRepository;
    private final String apiKey;
    private final RestClient restClient;

    public CalorieNinjasClient(FoodItemRepository foodItemRepository, @Value("${calorieninjas.api.key}") String apiKey) {
        this.foodItemRepository = foodItemRepository;
        this.apiKey = apiKey;
        this.restClient = RestClient.create();
    }

    public List<MealItemResponse> getFoodItemsFromDescription(String description) {
        if (description == null || description.trim().isBlank()) {
            throw new IllegalArgumentException("Description must not be blank");
        }

        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("query", description)
                .build()
                .toUriString();

        ClientNinjasMealItemWrapper response = restClient.get()
                .uri(url)
                .header("X-Api-Key", apiKey)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(ClientNinjasMealItemWrapper.class);

        if (response != null) {
            LOGGER.info("CalorieNinjas returned " + response.getItems().size() + " items for query: " + description);
        }

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            throw new IllegalArgumentException("No nutrition information found for meal: " + description);
        }

        return response.getItems();
    }

    public FoodItem fetchFoodItem(String name) {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("Name must not be blank");
        }

        String url = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("query", name)
                .build()
                .toUriString();

        ClientNinjasFoodItemWrapper response = restClient.get()
                .uri(url)
                .header("X-Api-Key", apiKey)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .body(ClientNinjasFoodItemWrapper.class);

        if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
            throw new IllegalArgumentException("No nutrition information found for: " + name);
        }

        FoodItemResponse firstItem = response.getItems().get(0);

        double factor = 100.0 / firstItem.getQuantityGrams();

        FoodItem foodItem = FoodItem.builder()
                .sugarG(firstItem.getSugarG() * factor)
                .quantityGrams(firstItem.getQuantityGrams() * factor)
                .fiberG(firstItem.getFiberG() * factor)
                .sodiumMg(firstItem.getSodiumMg() * factor)
                .name(firstItem.getName())
                .potassiumMg(firstItem.getPotassiumMg() * factor)
                .fatSaturatedG(firstItem.getFatSaturatedG() * factor)
                .calories(firstItem.getCalories() * factor)
                .cholesterolMg(firstItem.getCholesterolMg() * factor)
                .proteinG(firstItem.getProteinG() * factor)
                .carbohydratesTotalG(firstItem.getCarbohydratesTotalG() * factor)
                .build();

        LOGGER.info("CalorieNinjas returned " + foodItem.getName());
        return foodItemRepository.save(foodItem);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    private static class ClientNinjasFoodItemWrapper {
        private List<FoodItemResponse> items = new ArrayList<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    private static class ClientNinjasMealItemWrapper {
        private List<MealItemResponse> items = new ArrayList<>();
    }
}

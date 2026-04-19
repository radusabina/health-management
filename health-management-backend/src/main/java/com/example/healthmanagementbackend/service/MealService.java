package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.apininjas.CalorieNinjasClient;
import com.example.healthmanagementbackend.dto.MealDto;
import com.example.healthmanagementbackend.dto.MealItemDto;
import com.example.healthmanagementbackend.dto.NutritionDto;
import com.example.healthmanagementbackend.model.FoodItem;
import com.example.healthmanagementbackend.apininjas.dto.MealItemResponse;
import com.example.healthmanagementbackend.exception.NoMealFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.MealItem;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.model.enums.MealType;
import com.example.healthmanagementbackend.repository.FoodItemRepository;
import com.example.healthmanagementbackend.repository.MealItemRepository;
import com.example.healthmanagementbackend.repository.MealRepository;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MealService {

    private static final Logger LOGGER = Logger.getLogger(MealService.class.getName());

    private final MealRepository mealRepository;
    private final MealItemRepository mealItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final CalorieNinjasClient calorieNinjasClient;

    public MealService(MealRepository mealRepository, MealItemRepository mealItemRepository,
                       FoodItemRepository foodItemRepository, UserRepository userRepository, CalorieNinjasClient calorieNinjasClient) {
        this.mealRepository = mealRepository;
        this.mealItemRepository = mealItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.calorieNinjasClient = calorieNinjasClient;
    }

    public Meal addMeal(MealType mealType, String description, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));
        List<MealItemResponse> itemsInMeal = calorieNinjasClient.getFoodItemsFromDescription(description);

        Meal meal = Meal.builder()
                .user(user)
                .mealType(mealType)
                .description(description)
                .date(LocalDate.now())
                .updatedAt(LocalDateTime.now()).build();

        meal = mealRepository.save(meal);

        for (MealItemResponse item : itemsInMeal) {
            String normalizedFoodItemName = normalize(item.getName());
            FoodItem foodItem = foodItemRepository.findByNameIgnoreCase(normalizedFoodItemName)
                    .orElseGet(() -> calorieNinjasClient.fetchFoodItem(normalizedFoodItemName));

            MealItem mealItem = MealItem.builder()
                    .foodItem(foodItem)
                    .meal(meal)
                    .quantityGrams(item.getServingSizeG()).build();

            meal.getItems().add(mealItem);
        }

        meal = mealRepository.save(meal);

        LOGGER.info("operation=addMeal, meal added for userId=" + userId);
        return meal;
    }

    public void updateMeal(UUID mealId, MealType mealType, String description) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));

        meal.setMealType(mealType);
        meal.setDescription(description);
        meal.setUpdatedAt(LocalDateTime.now());
        meal.getItems().clear();

        List<MealItemResponse> itemsInMeal =
                calorieNinjasClient.getFoodItemsFromDescription(description);

        for (MealItemResponse item : itemsInMeal) {
            String normalizedName = normalize(item.getName());
            FoodItem foodItem = foodItemRepository
                    .findByNameIgnoreCase(normalizedName)
                    .orElseGet(() -> calorieNinjasClient.fetchFoodItem(normalizedName));

            MealItem mealItem = MealItem.builder()
                    .foodItem(foodItem)
                    .quantityGrams(item.getServingSizeG())
                    .build();

            meal.getItems().add(mealItem);
        }

        mealRepository.save(meal);
        LOGGER.info("operation=updateMeal, meal updated for userId=" + meal.getUser().getId());
    }

    public List<MealDto> getMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserId(userId);
        LOGGER.info("operation=getMealsForUser, meals size: " + meals.size() + ",userId=" + userId);

        return meals.stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<MealDto> getMealsForUserByType(UUID userId, MealType mealType) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserIdAndMealType(userId, mealType);
        LOGGER.info("operation=getMealsForUserByType, meals size: " + meals.size() + ",userId=" + userId);

        return meals.stream()
                .map(this::mapToDto)
                .toList();
    }

    public List<MealDto> getTodayMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserIdAndDate(userId, LocalDate.now());
        LOGGER.info("operation=getTodayMealsForUser, meals size: " + meals.size() + ",userId=" + userId);

        return meals.stream()
                .map(this::mapToDto)
                .toList();
    }

    public MealDto getMealById(UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));
        LOGGER.info("operation=getMealById, mealId=" + mealId);

        return mapToDto(meal);
    }

    @Transactional
    public boolean deleteMeal(UUID mealId) {
        if (!mealRepository.existsById(mealId)) {
            return false;
        }

        mealRepository.deleteById(mealId);
        LOGGER.info("operation=deleteMeal, mealId=" + mealId);

        return true;
    }

    private String normalize(String name) {
        return name == null ? "" :
                name.trim().toLowerCase().replaceAll("\\s+", " ");
    }

    private double roundDecimalCustom(double value) {
        double decimal = value - Math.floor(value);
        decimal = Math.round(decimal * 1000.0) / 1000.0;

        if (decimal < 0.1) {
            return Math.floor(value);
        }
        if (decimal > 0.9) {
            return Math.ceil(value);
        }

        return Math.round(value * 10.0) / 10.0;
    }

    private MealDto mapToDto(Meal meal) {
        List<MealItemDto> itemDtos = new ArrayList<>();

        double totalCalories = 0;
        double sugar = 0, fiber = 0, sodium = 0, potassium = 0,
                fatSat = 0, cholesterol = 0, protein = 0, carbs = 0;

        for (MealItem item : meal.getItems()) {
            FoodItem food = item.getFoodItem();
            double factor = item.getQuantityGrams() / 100.0;

            double itemCalories = roundDecimalCustom(food.getCalories() * factor);

            double itemSugar = roundDecimalCustom(food.getSugarG() * factor);
            double itemFiber = roundDecimalCustom(food.getFiberG() * factor);
            double itemSodium = roundDecimalCustom(food.getSodiumMg() * factor);
            double itemPotassium = roundDecimalCustom(food.getPotassiumMg() * factor);
            double itemFatSat = roundDecimalCustom(food.getFatSaturatedG() * factor);
            double itemCholesterol = roundDecimalCustom(food.getCholesterolMg() * factor);
            double itemProtein = roundDecimalCustom(food.getProteinG() * factor);
            double itemCarbs = roundDecimalCustom(food.getCarbohydratesTotalG() * factor);

            MealItemDto dto = MealItemDto.builder()
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
                    .build();

            itemDtos.add(dto);

            totalCalories += itemCalories;
            sugar += itemSugar;
            fiber += itemFiber;
            sodium += itemSodium;
            potassium += itemPotassium;
            fatSat += itemFatSat;
            cholesterol += itemCholesterol;
            protein += itemProtein;
            carbs += itemCarbs;
        }

        NutritionDto nutritionDto = NutritionDto.builder()
                .calories(Math.round(totalCalories))
                .sugarG(roundDecimalCustom(sugar))
                .fiberG(roundDecimalCustom(fiber))
                .sodiumMg(roundDecimalCustom(sodium))
                .potassiumMg(roundDecimalCustom(potassium))
                .fatSaturatedG(roundDecimalCustom(fatSat))
                .cholesterolMg(roundDecimalCustom(cholesterol))
                .proteinG(roundDecimalCustom(protein))
                .carbohydratesTotalG(roundDecimalCustom(carbs))
                .build();

        return MealDto.builder()
                .mealType(meal.getMealType())
                .description(meal.getDescription())
                .date(meal.getDate())
                .totalCalories(Math.round(totalCalories))
                .nutritions(nutritionDto)
                .items(itemDtos)
                .build();
    }
}

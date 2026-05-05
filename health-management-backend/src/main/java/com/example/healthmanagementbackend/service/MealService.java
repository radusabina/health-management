package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.apininjas.CalorieNinjasClient;
import com.example.healthmanagementbackend.apininjas.dto.MealItemResponse;
import com.example.healthmanagementbackend.model.*;
import com.example.healthmanagementbackend.exception.NoMealFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.enums.MealType;
import com.example.healthmanagementbackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import static com.example.healthmanagementbackend.controller.MealController.MealItemRequest;

@Service
public class MealService {

    private static final Logger LOGGER = Logger.getLogger(MealService.class.getName());

    private final MealRepository mealRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;
    private final CalorieNinjasClient calorieNinjasClient;
    private final DailyGoalRepository dailyGoalRepository;

    public MealService(MealRepository mealRepository, FoodItemRepository foodItemRepository, UserRepository userRepository,
                       CalorieNinjasClient calorieNinjasClient, DailyGoalRepository dailyGoalRepository) {
        this.mealRepository = mealRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
        this.dailyGoalRepository = dailyGoalRepository;
        this.calorieNinjasClient = calorieNinjasClient;
    }

    @Transactional
    public Meal addMeal(MealType mealType, String description, UUID userId, List<MealItemRequest> items) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        Meal meal = Meal.builder()
                .user(user)
                .mealType(mealType)
                .description(description)
                .date(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();

        meal = mealRepository.save(meal);
        setMealItems(items, meal);
        meal = mealRepository.save(meal);

        Optional<DailyGoal> dailyGoal = dailyGoalRepository.findByUserIdAndDate(userId, LocalDate.now());
        if (dailyGoal.isPresent()) {
            int currentCalories = dailyGoal.get().getCaloriesDone();
            dailyGoal.get().setCaloriesDone(currentCalories + calculateTotalCalories(meal));
            dailyGoalRepository.save(dailyGoal.get());
        }

        LOGGER.info("Operation=addMeal, Message=Meal added for userId=" + userId);
        return meal;
    }

    @Transactional
    public void updateMeal(UUID mealId, MealType mealType, String description) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));

        meal.setMealType(mealType);
        meal.setDescription(description);
        meal.setUpdatedAt(LocalDateTime.now());
        meal.getItems().clear();

        List<MealItemResponse> itemsInMeal = calorieNinjasClient.getFoodItemsFromDescription(description);

        for (MealItemResponse item : itemsInMeal) {
            String normalizedName = normalize(item.getName());
            FoodItem foodItem = foodItemRepository
                    .findByNameIgnoreCase(normalizedName)
                    .orElseGet(() -> calorieNinjasClient.fetchFoodItem(normalizedName));

            MealItem mealItem = MealItem.builder()
                    .foodItem(foodItem)
                    .quantityGrams(item.getQuantityGrams())
                    .build();

            meal.getItems().add(mealItem);
        }

        mealRepository.save(meal);
        LOGGER.info("Operation=updateMeal, Message=Meal updated for userId=" + meal.getUser().getId());
    }

    public List<Meal> getMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserId(userId);
        LOGGER.info("Operation=getMealsForUser, Message=Meals size: " + meals.size() + ", userId=" + userId);
        return meals;
    }

    public List<Meal> getMealsForUserByType(UUID userId, MealType mealType) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserIdAndMealType(userId, mealType);
        LOGGER.info("Operation=getMealsForUserByType, Message=Meals size: " + meals.size() + ", userId=" + userId);
        return meals;
    }

    public List<Meal> getTodayMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        List<Meal> meals = mealRepository.getMealsByUserIdAndDate(userId, LocalDate.now());
        LOGGER.info("Operation=getTodayMealsForUser, Message=Meals size: " + meals.size() + ", userId=" + userId);
        return meals;
    }

    public Meal getMealById(UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));
        LOGGER.info("Operation=getMealById, MealId=" + mealId);
        return meal;
    }

    @Transactional
    public boolean deleteMeal(UUID mealId) {
        if (!mealRepository.existsById(mealId)) {
            LOGGER.info("Operation=deleteMeal, Message=Meal not found, MealId=" + mealId);
            return false;
        }
        mealRepository.deleteById(mealId);
        LOGGER.info("Operation=deleteMeal, mealId=" + mealId);
        return true;
    }

    public List<MealItemResponse> analyzeMeal(String description) {
        List<MealItemResponse> items = calorieNinjasClient.getFoodItemsFromDescription(description);
        LOGGER.info("Operation=analyzeMeal");
        return items;
    }

    // ── private helpers ───────────────────────────────────────────────────────
    private void setMealItems(List<MealItemRequest> itemsInMeal, Meal meal) {
        for (MealItemRequest item : itemsInMeal) {
            String normalizedFoodItemName = normalize(item.getName());
            FoodItem foodItem = foodItemRepository.findByNameIgnoreCase(normalizedFoodItemName)
                    .orElseGet(() -> calorieNinjasClient.fetchFoodItem(normalizedFoodItemName));

            MealItem mealItem = MealItem.builder()
                    .foodItem(foodItem)
                    .meal(meal)
                    .quantityGrams(item.getQuantityGrams())
                    .build();

            meal.getItems().add(mealItem);
        }
    }

    private int calculateTotalCalories(Meal meal) {
        return (int) Math.round(
                meal.getItems().stream()
                        .mapToDouble(item -> item.getFoodItem().getCalories() * (item.getQuantityGrams() / 100.0))
                        .sum()
        );
    }

    private String normalize(String name) {
        return name == null ? "" : name.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}

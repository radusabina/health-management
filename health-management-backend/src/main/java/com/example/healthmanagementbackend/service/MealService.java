package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.NoMealFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.model.enums.MealType;
import com.example.healthmanagementbackend.repository.MealRepository;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class MealService {

    private static final Logger LOGGER = Logger.getLogger(MealService.class.getName());

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealService(MealRepository mealRepository, UserRepository userRepository) {
        this.mealRepository = mealRepository;
        this.userRepository = userRepository;
    }

    public Meal addMeal(MealType mealType, String description, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        Meal meal = Meal.builder()
                .user(user)
                .mealType(mealType)
                .description(description)
                .updatedAt(LocalDateTime.now()).build();

        mealRepository.save(meal);
        LOGGER.info("Meal added for user: " + userId);
        return meal;
    }

    public void updateMeal(UUID mealId, MealType mealType, String description) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));

        meal.setMealType(mealType);
        meal.setDescription(description);
        meal.setUpdatedAt(LocalDateTime.now());

        mealRepository.save(meal);
        LOGGER.info("Meal updated for user: " + meal.getUser().getId());
    }

    public List<Meal> getMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        return mealRepository.getMealsByUserId(userId);
    }

    public List<Meal> getMealsForUserByType(UUID userId, MealType mealType) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        return mealRepository.getMealsByUserIdAndMealType(userId, mealType);
    }

    public List<Meal> getTodayMealsForUser(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        return mealRepository.getMealsByUserIdAndDate(userId, LocalDate.now());
    }

    public boolean deleteMeal(UUID mealId) {
        Meal meal = mealRepository.findById(mealId).orElse(null);
        if (meal == null) {
            LOGGER.info("Meal not found for user: " + mealId);
            return false;
        }
        mealRepository.delete(meal);
        LOGGER.info("Meal deleted for user: " + meal.getUser().getId());
        return true;
    }

    public Meal getMealById(UUID mealId) {
        return mealRepository.findById(mealId)
                .orElseThrow(() -> new NoMealFoundException("No meal found"));
    }

    //TODO: calculate meal calories
}

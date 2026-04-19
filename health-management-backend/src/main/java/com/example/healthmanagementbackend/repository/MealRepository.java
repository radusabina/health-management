package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> getMealsByUserId(UUID userId);
    List<Meal> getMealsByUserIdAndMealType(UUID userId, MealType mealType);
    List<Meal> getMealsByUserIdAndDate(UUID userId, LocalDate date);
}
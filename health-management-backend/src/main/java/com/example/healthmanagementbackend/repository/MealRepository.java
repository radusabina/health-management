package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.Meal;
import com.example.healthmanagementbackend.model.enums.MealType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    @EntityGraph(attributePaths = {"items", "items.foodItem"})
    List<Meal> getMealsByUserId(UUID userId);

    @EntityGraph(attributePaths = {"items", "items.foodItem"})
    List<Meal> getMealsByUserIdAndMealType(UUID userId, MealType mealType);

    @EntityGraph(attributePaths = {"items", "items.foodItem"})
    List<Meal> getMealsByUserIdAndDate(UUID userId, LocalDate date);

    @EntityGraph(attributePaths = {"items", "items.foodItem"})
    Optional<Meal> findMealById(UUID id);
}
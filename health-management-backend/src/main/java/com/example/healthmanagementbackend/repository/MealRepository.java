package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {

    Optional<Meal> findMealByUserId(UUID userId);
}
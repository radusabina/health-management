package com.example.healthmanagementbackend.repository;

import com.example.healthmanagement.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {
}
package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, UUID> {

    Optional<FoodItem> findByName(String name);
    Optional<FoodItem> findByNameIgnoreCase(String name);
}

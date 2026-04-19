package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.apininjas.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, UUID> {

    Optional<FoodItem> findByNameIgnoreCase(String name);
}

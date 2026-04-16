package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.MealItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MealItemRepository extends JpaRepository<MealItem, UUID> {
}

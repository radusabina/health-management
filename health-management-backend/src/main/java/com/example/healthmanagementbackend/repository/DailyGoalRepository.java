package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.DailyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, UUID> {
}
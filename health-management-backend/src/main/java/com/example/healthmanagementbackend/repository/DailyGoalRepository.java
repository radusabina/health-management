package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.DailyGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, UUID> {

    List<DailyGoal> findByDate(LocalDateTime date);
}
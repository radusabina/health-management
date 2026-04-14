package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.GeneralGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneralGoalRepository extends JpaRepository<GeneralGoal, UUID> {

    Optional<GeneralGoal> findGeneralGoalByUserId(UUID userId);
}

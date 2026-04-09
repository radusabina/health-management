package com.example.healthmanagementbackend.repository;

import com.example.healthmanagement.model.WeightLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WeightLogRepository extends JpaRepository<WeightLog, UUID> {
}
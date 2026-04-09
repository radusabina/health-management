package com.example.healthmanagementbackend.repository;

import com.example.healthmanagement.model.WaterLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WaterLogRepository extends JpaRepository<WaterLog, UUID> {
}
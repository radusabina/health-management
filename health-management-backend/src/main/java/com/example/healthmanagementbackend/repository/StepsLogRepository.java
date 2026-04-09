package com.example.healthmanagementbackend.repository;

import com.example.healthmanagement.model.StepsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StepsLogRepository extends JpaRepository<StepsLog, UUID> {
}
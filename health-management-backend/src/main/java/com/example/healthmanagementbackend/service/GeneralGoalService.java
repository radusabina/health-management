package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.NoGeneralGoalFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.GeneralGoal;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.repository.GeneralGoalRepository;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class GeneralGoalService {

    private static final Logger LOGGER = Logger.getLogger(GeneralGoalService.class.getName());

    private final GeneralGoalRepository generalGoalRepository;
    private final UserRepository userRepository;

    public GeneralGoalService(GeneralGoalRepository generalGoalRepository, UserRepository userRepository) {
        this.generalGoalRepository = generalGoalRepository;
        this.userRepository = userRepository;
    }

    public GeneralGoal addGeneralGoal(UUID userId, int calorieGoal, int waterGoal, int weightTarget) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        GeneralGoal existingGeneralGoal = generalGoalRepository.findGeneralGoalByUserId(userId).orElse(null);
        if (existingGeneralGoal != null) {
            return existingGeneralGoal;
        }

        GeneralGoal generalGoal = GeneralGoal.builder()
                .user(user)
                .calorieGoal(calorieGoal)
                .waterGoal(waterGoal)
                .weightTarget(weightTarget)
                .updatedAt(LocalDateTime.now())
                .build();

        GeneralGoal savedGeneralGoal = generalGoalRepository.save(generalGoal);
        LOGGER.info("General goal added for user: " + userId);
        return savedGeneralGoal;
    }

    public GeneralGoal getGeneralGoalById(UUID generalGoalId) {
        return generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
    }

    public void updateGeneralGoal(UUID generalGoalId, int calorieGoal, int waterGoal, int weightTarget) {
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));

        generalGoal.setCalorieGoal(calorieGoal);
        generalGoal.setWaterGoal(waterGoal);
        generalGoal.setWeightTarget(weightTarget);
        generalGoal.setUpdatedAt(LocalDateTime.now());

        generalGoalRepository.save(generalGoal);
        LOGGER.info("General goal updated for user: " + generalGoal.getUser().getId());
    }

    public GeneralGoal getGeneralGoalForUser(UUID userId) {
        return generalGoalRepository.findGeneralGoalByUserId(userId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
    }

    public GeneralGoal getGeneralGoalByUserId(UUID userId) {
        return getGeneralGoalForUser(userId);
    }

    public void updateGeneralGoalForUser(UUID userId, int calorieGoal, int waterGoal, int weightTarget) {
        GeneralGoal generalGoal = getGeneralGoalForUser(userId);

        generalGoal.setCalorieGoal(calorieGoal);
        generalGoal.setWaterGoal(waterGoal);
        generalGoal.setWeightTarget(weightTarget);
        generalGoal.setUpdatedAt(LocalDateTime.now());

        generalGoalRepository.save(generalGoal);
        LOGGER.info("General goal updated for user: " + userId);
    }

    public boolean existsForUser(UUID userId) {
        return generalGoalRepository.existsByUserId(userId);
    }

    public boolean deleteGeneralGoal(UUID generalGoalId) {
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId).orElse(null);
        if (generalGoal == null) {
            LOGGER.info("General goal not found for user: " + generalGoalId);
            return false;
        }
        generalGoalRepository.delete(generalGoal);
        LOGGER.info("General goal deleted for user: " + generalGoal.getUser().getId());
        return true;
    }

    public boolean deleteGeneralGoalForUser(UUID userId) {
        GeneralGoal generalGoal = generalGoalRepository.findGeneralGoalByUserId(userId).orElse(null);
        if (generalGoal == null) {
            LOGGER.info("General goal not found for user: " + userId);
            return false;
        }

        generalGoalRepository.delete(generalGoal);
        LOGGER.info("General goal deleted for user: " + userId);
        return true;
    }
}

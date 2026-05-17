package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.NoGeneralGoalFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.DailyGoal;
import com.example.healthmanagementbackend.model.GeneralGoal;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.repository.DailyGoalRepository;
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
    private final DailyGoalRepository dailyGoalRepository;
    private final UserRepository userRepository;

    public GeneralGoalService(GeneralGoalRepository generalGoalRepository, DailyGoalRepository dailyGoalRepository,
                              UserRepository userRepository) {
        this.generalGoalRepository = generalGoalRepository;
        this.dailyGoalRepository = dailyGoalRepository;
        this.userRepository = userRepository;
    }

    public GeneralGoal addGeneralGoal(UUID userId, int calorieGoal, int waterGoal, int weightTarget, int bottleAmountMl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        GeneralGoal existingGeneralGoal = generalGoalRepository.findGeneralGoalByUserId(userId).orElse(null);
        if (existingGeneralGoal != null) {
            return existingGeneralGoal;
        }

        int resolvedBottleAmount = bottleAmountMl > 0 ? bottleAmountMl : 500;

        GeneralGoal generalGoal = GeneralGoal.builder()
                .user(user)
                .calorieGoal(calorieGoal)
                .waterGoal(waterGoal)
                .weightTarget(weightTarget)
                .bottleAmountMl(resolvedBottleAmount)
                .updatedAt(LocalDateTime.now())
                .build();

        GeneralGoal savedGeneralGoal = generalGoalRepository.save(generalGoal);
        LOGGER.info("Operation=addGeneralGoal, Message=General goal added for userId=" + userId);

        DailyGoal dailyGoal = DailyGoal.builder().generalGoal(savedGeneralGoal).user(user).build();
        dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Operation=addGeneralGoal, Message=Daily goal added for userId=" + userId + ", generalGoalId=" + savedGeneralGoal.getId());
        return savedGeneralGoal;
    }

    public GeneralGoal getGeneralGoalById(UUID generalGoalId) {
        return generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
    }

    public void updateGeneralGoal(UUID generalGoalId, int calorieGoal, int waterGoal, int weightTarget, int bottleAmountMl) {
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));

        generalGoal.setCalorieGoal(calorieGoal);
        generalGoal.setWaterGoal(waterGoal);
        generalGoal.setWeightTarget(weightTarget);
        generalGoal.setBottleAmountMl(bottleAmountMl > 0 ? bottleAmountMl : 500);
        generalGoal.setUpdatedAt(LocalDateTime.now());

        generalGoalRepository.save(generalGoal);
        LOGGER.info("Operation=updateGeneralGoal, Message=General goal updated for userId=" + generalGoal.getUser().getId());
    }

    public GeneralGoal getGeneralGoalForUser(UUID userId) {
        return generalGoalRepository.findGeneralGoalByUserId(userId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
    }

    public GeneralGoal getGeneralGoalByUserId(UUID userId) {
        return getGeneralGoalForUser(userId);
    }

    public void updateGeneralGoalForUser(UUID userId, int calorieGoal, int waterGoal, int weightTarget, int bottleAmountMl) {
        GeneralGoal generalGoal = getGeneralGoalForUser(userId);

        generalGoal.setCalorieGoal(calorieGoal);
        generalGoal.setWaterGoal(waterGoal);
        generalGoal.setWeightTarget(weightTarget);
        generalGoal.setBottleAmountMl(bottleAmountMl > 0 ? bottleAmountMl : 500);
        generalGoal.setUpdatedAt(LocalDateTime.now());

        generalGoalRepository.save(generalGoal);
        LOGGER.info("Operation=updateGeneralGoalForUser, Message=General goal updated for userId=" + userId);
    }

    public boolean existsForUser(UUID userId) {
        return generalGoalRepository.existsByUserId(userId);
    }

    public boolean deleteGeneralGoal(UUID generalGoalId) {
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId).orElse(null);
        if (generalGoal == null) {
            LOGGER.info("Operation=deleteGeneralGoal, Message=General goal not found, generalGoalId=" + generalGoalId);
            return false;
        }
        generalGoalRepository.delete(generalGoal);
        LOGGER.info("Operation=deleteGeneralGoal, Message=General goal deleted for userId=" + generalGoal.getUser().getId());
        return true;
    }

    public boolean deleteGeneralGoalForUser(UUID userId) {
        GeneralGoal generalGoal = generalGoalRepository.findGeneralGoalByUserId(userId).orElse(null);
        if (generalGoal == null) {
            LOGGER.info("Operation=deleteGeneralGoalForUser, Message=General goal not found for userId=" + userId);
            return false;
        }

        generalGoalRepository.delete(generalGoal);
        LOGGER.info("Operation=deleteGeneralGoalForUser, Message=General goal deleted for userId=" + userId);
        return true;
    }
}

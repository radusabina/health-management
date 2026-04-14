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

    public void addGeneralGoal(int calorieGoal, int stepsGoal, int waterGoal, int weightTarget, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        GeneralGoal generalGoal = GeneralGoal.builder()
                .user(user)
                .calorieGoal(calorieGoal)
                .stepsGoal(stepsGoal)
                .waterGoal(waterGoal)
                .weightTarget(weightTarget)
                .updatedAt(LocalDateTime.now()).build();

        generalGoalRepository.save(generalGoal);
        LOGGER.info("General goal added for user: " + userId);
    }

    public void updateGeneralGoal(UUID generalGoalId, int calorieGoal, int stepsGoal, int waterGoal, int weightTarget) {
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));

        generalGoal.setCalorieGoal(calorieGoal);
        generalGoal.setStepsGoal(stepsGoal);
        generalGoal.setWaterGoal(waterGoal);
        generalGoal.setWeightTarget(weightTarget);
        generalGoal.setUpdatedAt(LocalDateTime.now());

        generalGoalRepository.save(generalGoal);
        LOGGER.info("General goal updated for user: " + generalGoal.getUser().getId());
    }

    public GeneralGoal getGeneralGoalByUserId(UUID userId) {
        return generalGoalRepository.findGeneralGoalByUserId(userId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
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
}

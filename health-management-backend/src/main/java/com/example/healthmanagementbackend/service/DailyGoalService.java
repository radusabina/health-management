package com.example.healthmanagementbackend.service;

import com.example.healthmanagement.exception.NoDailyGoalFoundException;
import com.example.healthmanagement.exception.NoUserFoundException;
import com.example.healthmanagement.model.DailyGoal;
import com.example.healthmanagement.model.User;
import com.example.healthmanagement.repository.DailyGoalRepository;
import com.example.healthmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DailyGoalService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyGoalService.class);

    private final DailyGoalRepository dailyGoalRepository;
    private final UserRepository userRepository;

    public DailyGoalService(DailyGoalRepository dailyGoalRepository, UserRepository userRepository) {
        this.dailyGoalRepository = dailyGoalRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyGoals() {
        List<DailyGoal> yesterdayGoals = dailyGoalRepository.findByDate(LocalDateTime.now().minusDays(1));
        LocalDateTime today = LocalDateTime.now();

        for (DailyGoal goal : yesterdayGoals) {
            DailyGoal newDailyGoal = DailyGoal.builder()
                    .user(goal.getUser())
                    .caloriesGoal(goal.getCaloriesGoal())
                    .stepsGoal(goal.getStepsGoal())
                    .waterGoal(goal.getWaterGoal())
                    .stepsDone(0)
                    .caloriesDone(0)
                    .waterDone(0)
                    .date(today)
                    .updatedAt(LocalDateTime.now()).build();

            dailyGoalRepository.save(newDailyGoal);
        }
        LOGGER.info("Daily goals have been reset: {}", today);
    }

    public void addDailyGoal(UUID userId, int caloriesGoal, int stepsGoal, int waterGoal, int weightTarget) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found for id: " + userId));

        DailyGoal dailyGoal = DailyGoal.builder()
                .user(user)
                .caloriesGoal(caloriesGoal)
                .waterGoal(waterGoal)
                .stepsGoal(stepsGoal)
                .weightTarget(weightTarget)
                .updatedAt(LocalDateTime.now())
                .date(LocalDateTime.now()).build();
        dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Daily goal has been added: {}", dailyGoal);
    }

    public void updateGeneralGoals(UUID dailyGoalId, UUID userId, int calorieGoal, int stepsGoal, int waterGoal) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found for id: " + userId));
        DailyGoal goal = dailyGoalRepository.findByUserIdAndDate(userId, LocalDateTime.now())
                .orElseThrow(() -> new NoDailyGoalFoundException("DailyGoal not found for id: " + dailyGoalId));

        goal.setCaloriesGoal(calorieGoal);
        goal.setStepsGoal(stepsGoal);
        goal.setWaterGoal(waterGoal);
        goal.setUpdatedAt(LocalDateTime.now());

        dailyGoalRepository.save(goal);
        LOGGER.info("Daily goal general goals have been updated: {}, calorieGoal={}, stepsGaol={}, waterGoal={}",
                goal, calorieGoal, stepsGoal, waterGoal);
    }

    public void updateSteps(UUID dailyGoalId, int steps) {
        DailyGoal goal = dailyGoalRepository.findById(dailyGoalId)
                .orElseThrow(() -> new NoDailyGoalFoundException("DailyGoal not found for id: " + dailyGoalId));
        goal.setStepsDone(goal.getStepsDone() + steps);
        goal.setUpdatedAt(LocalDateTime.now());
        dailyGoalRepository.save(goal);
        LOGGER.info("Steps added to goal={}: {}",goal.getId(), steps);
    }
}

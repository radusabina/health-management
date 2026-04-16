package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.model.DailyGoal;
import com.example.healthmanagementbackend.repository.DailyGoalRepository;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
                    .caloriesDone(0)
                    .waterDone(0)
                    .date(today)
                    .updatedAt(LocalDateTime.now()).build();

            dailyGoalRepository.save(newDailyGoal);
        }
        LOGGER.info("Daily goals have been reset: {}", today);
    }

}

package com.example.healthmanagementbackend.service;

import com.example.healthmanagementbackend.exception.NoDailyGoalFoundException;
import com.example.healthmanagementbackend.exception.NoGeneralGoalFoundException;
import com.example.healthmanagementbackend.exception.NoUserFoundException;
import com.example.healthmanagementbackend.model.DailyGoal;
import com.example.healthmanagementbackend.model.GeneralGoal;
import com.example.healthmanagementbackend.model.User;
import com.example.healthmanagementbackend.repository.DailyGoalRepository;
import com.example.healthmanagementbackend.repository.GeneralGoalRepository;
import com.example.healthmanagementbackend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DailyGoalService {

    private static final Logger LOGGER = Logger.getLogger(DailyGoalService.class.getName());

    private final DailyGoalRepository dailyGoalRepository;
    private final GeneralGoalRepository generalGoalRepository;
    private final UserRepository userRepository;

    public DailyGoalService(
            DailyGoalRepository dailyGoalRepository,
            GeneralGoalRepository generalGoalRepository,
            UserRepository userRepository
    ) {
        this.dailyGoalRepository = dailyGoalRepository;
        this.generalGoalRepository = generalGoalRepository;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Bucharest")
    public void resetDailyGoals() {
        List<DailyGoal> yesterdayGoals = dailyGoalRepository.findByDate(LocalDate.now().minusDays(1));
        LocalDate today = LocalDate.now();

        for (DailyGoal goal : yesterdayGoals) {
            if (dailyGoalRepository.findByUserIdAndDate(goal.getUser().getId(), today).isEmpty()) {
                DailyGoal newDailyGoal = DailyGoal.builder()
                        .user(goal.getUser())
                        .caloriesDone(0)
                        .waterDone(0)
                        .date(today)
                        .generalGoal(goal.getGeneralGoal())
                        .updatedAt(LocalDateTime.now()).build();

                dailyGoalRepository.save(newDailyGoal);
                LOGGER.info("Daily goals have been reset: " + today);
            }
        }
    }

    public DailyGoal createDailyGoalForUser(UUID userId, UUID generalGoalId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));
        GeneralGoal generalGoal = generalGoalRepository.findById(generalGoalId)
                .orElseThrow(() -> new NoGeneralGoalFoundException("No general goal found"));
        validateGeneralGoalOwnership(user, generalGoal);

        Optional<DailyGoal> existingDailyGoal = dailyGoalRepository.findByUserIdAndDate(userId, date);

        if (existingDailyGoal.isEmpty()) {
            DailyGoal dailyGoal = DailyGoal.builder()
                    .user(user)
                    .generalGoal(generalGoal)
                    .date(date)
                    .caloriesDone(0)
                    .waterDone(0)
                    .updatedAt(LocalDateTime.now())
                    .build();

            DailyGoal savedDailyGoal = dailyGoalRepository.save(dailyGoal);
            LOGGER.info("Daily goal created for user: " + userId + " on " + date);
            return savedDailyGoal;
        }
        return existingDailyGoal.get();
    }

    public DailyGoal getDailyGoalById(UUID id) {
        return dailyGoalRepository.findById(id)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));
    }

    public DailyGoal getTodayDailyGoalForUser(UUID userId) {
        Optional<DailyGoal> dailyGoal = dailyGoalRepository.findByUserIdAndDate(userId, LocalDate.now());
        if (dailyGoal.isEmpty()) {
            Optional<GeneralGoal> generalGoal = generalGoalRepository.findByUserId(userId);
            if (generalGoal.isEmpty()) {
                throw new NoGeneralGoalFoundException("No general goal found");
            }
            DailyGoal dailyGoalToReturn = DailyGoal.builder()
                    .generalGoal(generalGoal.get())
                    .user(userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"))).build();
            dailyGoalRepository.save(dailyGoalToReturn);
            return dailyGoalToReturn;
        }
        return dailyGoal.get();
    }

    public void getDailyGoalForUserByDate(UUID userId, LocalDate date) {
        dailyGoalRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));
    }

    public List<DailyGoal> getDailyGoalsForUser(UUID userId) {
        return dailyGoalRepository.findAllByUserIdOrderByDateDesc(userId);
    }

    public DailyGoal updateDailyGoal(UUID id, int caloriesDone, int waterDone) {
        DailyGoal dailyGoal = dailyGoalRepository.findById(id)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));

        if (!LocalDate.now().equals(dailyGoal.getDate())) {
            throw new NoDailyGoalFoundException("Daily goal is not for today");
        }

        dailyGoal.setCaloriesDone(caloriesDone);
        dailyGoal.setWaterDone(waterDone);
        dailyGoal.setUpdatedAt(LocalDateTime.now());

        DailyGoal updatedDailyGoal = dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Daily goal progress updated: " + id);
        return updatedDailyGoal;
    }

    public void incrementCalories(UUID id, int caloriesToAdd) {
        DailyGoal dailyGoal = dailyGoalRepository.findById(id)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));

        dailyGoal.setCaloriesDone(dailyGoal.getCaloriesDone() + caloriesToAdd);
        dailyGoal.setUpdatedAt(LocalDateTime.now());

        dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Daily goal calories incremented: " + id);
    }

    public void incrementWater(UUID id, int waterToAdd) {
        DailyGoal dailyGoal = dailyGoalRepository.findById(id)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));

        if (waterToAdd < 0 && dailyGoal.getWaterDone() + waterToAdd < 0) {
            dailyGoal.setWaterDone(0);
        } else {
            dailyGoal.setWaterDone(dailyGoal.getWaterDone() + waterToAdd);
            dailyGoal.setUpdatedAt(LocalDateTime.now());
        }
        dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Daily goal water incremented: " + id);
    }

    public boolean deleteDailyGoal(UUID id) {
        DailyGoal dailyGoal = dailyGoalRepository.findById(id).orElse(null);
        if (dailyGoal == null) {
            LOGGER.info("Daily goal not found: " + id);
            return false;
        }

        dailyGoalRepository.delete(dailyGoal);
        LOGGER.info("Daily goal deleted: " + id);
        return true;
    }

    private void validateGeneralGoalOwnership(User user, GeneralGoal generalGoal) {
        if (generalGoal.getUser() == null || !generalGoal.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("General goal does not belong to the provided user");
        }
    }
}

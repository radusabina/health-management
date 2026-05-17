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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class DailyGoalService {

    private static final ZoneId APP_ZONE = ZoneId.of("Europe/Bucharest");
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
    @Transactional
    public void resetDailyGoals() {
        LocalDate today = today();
        LocalDate yesterday = today.minusDays(1);
        int created = 0;

        for (DailyGoal goal : dailyGoalRepository.findByDate(yesterday)) {
            User user = goal.getUser();
            GeneralGoal generalGoal = resolveGeneralGoal(goal, user.getId());
            if (generalGoal != null && createTodayDailyGoalIfMissing(user, generalGoal, today).isPresent()) {
                created++;
            }
        }

        for (GeneralGoal generalGoal : generalGoalRepository.findAll()) {
            User user = generalGoal.getUser();
            if (dailyGoalRepository.findByUserIdAndDate(user.getId(), today).isPresent()) {
                continue;
            }
            List<DailyGoal> history = dailyGoalRepository.findAllByUserIdOrderByDateDesc(user.getId());
            GeneralGoal templateGoal = history.isEmpty()
                    ? generalGoal
                    : Optional.ofNullable(history.get(0).getGeneralGoal()).orElse(generalGoal);
            if (createTodayDailyGoalIfMissing(user, templateGoal, today).isPresent()) {
                created++;
            }
        }

        LOGGER.info("Daily goal reset completed for " + today + ", created=" + created);
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
        LocalDate today = today();
        Optional<DailyGoal> dailyGoal = dailyGoalRepository.findByUserIdAndDate(userId, today);
        if (dailyGoal.isEmpty()) {
            Optional<GeneralGoal> generalGoal = generalGoalRepository.findByUserId(userId);
            if (generalGoal.isEmpty()) {
                throw new NoGeneralGoalFoundException("No general goal found");
            }
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoUserFoundException("No user found"));
            DailyGoal dailyGoalToReturn = DailyGoal.builder()
                    .generalGoal(generalGoal.get())
                    .user(user)
                    .date(today)
                    .caloriesDone(0)
                    .waterDone(0)
                    .updatedAt(now())
                    .build();
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

        if (!today().equals(dailyGoal.getDate())) {
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

    public void updateTodayWeight(UUID id, Double weight) {
        DailyGoal dailyGoal = dailyGoalRepository.findById(id)
                .orElseThrow(() -> new NoDailyGoalFoundException("No daily goal found"));

        dailyGoal.setTodayWeight(weight);
        dailyGoal.setUpdatedAt(LocalDateTime.now());

        dailyGoalRepository.save(dailyGoal);
        LOGGER.info("Today's weight updated for daily goal: " + id);
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

    private LocalDate today() {
        return LocalDate.now(APP_ZONE);
    }

    private LocalDateTime now() {
        return LocalDateTime.now(APP_ZONE);
    }

    private GeneralGoal resolveGeneralGoal(DailyGoal goal, UUID userId) {
        if (goal.getGeneralGoal() != null) {
            return goal.getGeneralGoal();
        }
        return generalGoalRepository.findByUserId(userId).orElse(null);
    }

    private Optional<DailyGoal> createTodayDailyGoalIfMissing(User user, GeneralGoal generalGoal, LocalDate today) {
        if (dailyGoalRepository.findByUserIdAndDate(user.getId(), today).isPresent()) {
            return Optional.empty();
        }
        DailyGoal newDailyGoal = DailyGoal.builder()
                .user(user)
                .caloriesDone(0)
                .waterDone(0)
                .date(today)
                .generalGoal(generalGoal)
                .updatedAt(now())
                .build();
        return Optional.of(dailyGoalRepository.save(newDailyGoal));
    }
}

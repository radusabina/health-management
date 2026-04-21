package com.example.healthmanagementbackend.controller;

import com.example.healthmanagementbackend.exception.NoDailyGoalFoundException;
import com.example.healthmanagementbackend.model.DailyGoal;
import com.example.healthmanagementbackend.service.DailyGoalService;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/daily-goal")
public class DailyGoalController {
    private final DailyGoalService dailyGoalService;

    public DailyGoalController(DailyGoalService dailyGoalService) {
        this.dailyGoalService = dailyGoalService;
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody DailyGoalRequest req) {
        try {
            LocalDate requestedDate = req.date() != null ? req.date() : LocalDate.now();
            boolean alreadyExists = true;

            try {
                dailyGoalService.getDailyGoalForUserByDate(req.userId(), requestedDate);
            } catch (NoDailyGoalFoundException e) {
                alreadyExists = false;
            }

            DailyGoal dailyGoal = dailyGoalService.createDailyGoalForUser(
                    req.userId(),
                    req.generalGoalId(),
                    requestedDate
            );
            return new ResponseEntity<>(mapToDailyGoalDto(dailyGoal), alreadyExists ? HttpStatus.OK : HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getDailyGoalById(@PathVariable("id") UUID id) {
        try {
            DailyGoal dailyGoal = dailyGoalService.getDailyGoalById(id);
            return new ResponseEntity<>(mapToDailyGoalDto(dailyGoal), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<Object> getTodayDailyGoalByUserId(@PathVariable("userId") UUID userId) {
        try {
            DailyGoal dailyGoal = dailyGoalService.getTodayDailyGoalForUser(userId);
            return new ResponseEntity<>(mapToDailyGoalDto(dailyGoal), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/userDailyGoals/{userId}")
    public ResponseEntity<Object> getUserDailyGoals(@PathVariable("userId") UUID userId) {
        try {
            List<DailyGoalDto> dailyGoals = dailyGoalService.getDailyGoalsForUser(userId).stream()
                    .map(this::mapToDailyGoalDto).collect(Collectors.toList());
            return new ResponseEntity<>(dailyGoals, HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping
    public ResponseEntity<Object> update(@RequestBody UpdateDailyGoalRequest req) {
        try {
            DailyGoal dailyGoal = dailyGoalService.updateDailyGoal(req.id(), req.caloriesDone(), req.waterDone());
            return new ResponseEntity<>(mapToDailyGoalDto(dailyGoal), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/incrementCalories")
    public ResponseEntity<Object> incrementCalories(@RequestParam("id") UUID id,
                                                    @RequestParam("caloriesToAdd") int caloriesToAdd) {
        try {
            dailyGoalService.incrementCalories(id, caloriesToAdd);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping("/incrementWater")
    public ResponseEntity<Object> incrementWater(@RequestParam("id") UUID id,
                                                 @RequestParam("waterToAdd") int waterToAdd) {
        try {
            dailyGoalService.incrementWater(id, waterToAdd);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") UUID id) {
        try {
            return ResponseEntity.ok(dailyGoalService.deleteDailyGoal(id));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private static ResponseEntity<Object> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("type", e.getClass().getSimpleName(),
                        "message", e.getMessage()));
    }

    private DailyGoalDto mapToDailyGoalDto(DailyGoal dailyGoal) {
        return DailyGoalDto.builder()
                .id(dailyGoal.getId())
                .date(dailyGoal.getDate())
                .waterDone(dailyGoal.getWaterDone())
                .caloriesDone(dailyGoal.getCaloriesDone())
                .generalGoalId(dailyGoal.getGeneralGoal().getId())
                .build();
    }

    public record UpdateDailyGoalRequest(UUID id, Integer caloriesDone, Integer waterDone) {
    }

    public record DailyGoalRequest(UUID userId, UUID generalGoalId, LocalDate date) {
    }

    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter @Builder
    public static class DailyGoalDto {
        private UUID id;
        private LocalDate date;
        private Integer caloriesDone;
        private Integer waterDone;
        private UUID generalGoalId;
    }
}

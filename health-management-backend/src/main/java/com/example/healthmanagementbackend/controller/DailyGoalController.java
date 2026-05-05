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
    public ResponseEntity<DailyGoalDto> add(@RequestBody DailyGoalRequest req) {
        LocalDate requestedDate = req.date() != null ? req.date() : LocalDate.now();
        boolean alreadyExists = true;

        // Intentional existence check: determines 200 vs 201 response code.
        try {
            dailyGoalService.getDailyGoalForUserByDate(req.userId(), requestedDate);
        } catch (NoDailyGoalFoundException e) {
            alreadyExists = false;
        }

        DailyGoal dailyGoal = dailyGoalService.createDailyGoalForUser(
                req.userId(), req.generalGoalId(), requestedDate);
        return new ResponseEntity<>(mapToDailyGoalDto(dailyGoal), alreadyExists ? HttpStatus.OK : HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DailyGoalDto> getDailyGoalById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(mapToDailyGoalDto(dailyGoalService.getDailyGoalById(id)));
    }

    @GetMapping("/today/{userId}")
    public ResponseEntity<DailyGoalDto> getTodayDailyGoalByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(mapToDailyGoalDto(dailyGoalService.getTodayDailyGoalForUser(userId)));
    }

    @GetMapping("/userDailyGoals/{userId}")
    public ResponseEntity<List<DailyGoalDto>> getUserDailyGoals(@PathVariable("userId") UUID userId) {
        List<DailyGoalDto> dailyGoals = dailyGoalService.getDailyGoalsForUser(userId).stream()
                .map(this::mapToDailyGoalDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dailyGoals);
    }

    @PutMapping
    public ResponseEntity<DailyGoalDto> update(@RequestBody UpdateDailyGoalRequest req) {
        return ResponseEntity.ok(mapToDailyGoalDto(
                dailyGoalService.updateDailyGoal(req.id(), req.caloriesDone(), req.waterDone())));
    }

    @PutMapping("/incrementCalories")
    public ResponseEntity<Void> incrementCalories(@RequestParam("id") UUID id,
                                                  @RequestParam("caloriesToAdd") int caloriesToAdd) {
        dailyGoalService.incrementCalories(id, caloriesToAdd);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/incrementWater")
    public ResponseEntity<Void> incrementWater(@RequestParam("id") UUID id,
                                               @RequestParam("waterToAdd") int waterToAdd) {
        dailyGoalService.incrementWater(id, waterToAdd);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/updateTodayWeight")
    public ResponseEntity<Void> updateTodayWeight(@RequestParam("id") UUID id,
                                                  @RequestParam("weight") Double weight) {
        dailyGoalService.updateTodayWeight(id, weight);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> delete(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(dailyGoalService.deleteDailyGoal(id));
    }

    private DailyGoalDto mapToDailyGoalDto(DailyGoal dailyGoal) {
        return DailyGoalDto.builder()
                .id(dailyGoal.getId())
                .date(dailyGoal.getDate())
                .waterDone(dailyGoal.getWaterDone())
                .caloriesDone(dailyGoal.getCaloriesDone())
                .generalGoalId(dailyGoal.getGeneralGoal().getId())
                .todayWeight(dailyGoal.getTodayWeight())
                .build();
    }

    public record UpdateDailyGoalRequest(UUID id, Integer caloriesDone, Integer waterDone) {}

    public record DailyGoalRequest(UUID userId, UUID generalGoalId, LocalDate date) {}

    @AllArgsConstructor @NoArgsConstructor
    @Getter @Setter @Builder
    public static class DailyGoalDto {
        private UUID id;
        private LocalDate date;
        private Integer caloriesDone;
        private Integer waterDone;
        private UUID generalGoalId;
        private Double todayWeight;
    }
}

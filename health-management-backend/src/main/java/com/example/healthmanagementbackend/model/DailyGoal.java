package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "daily_goals",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailyGoal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "calories_done")
    private int caloriesDone;

    @Column(name = "water_done")
    private int waterDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_goal_id")
    @JsonIgnoreProperties({"user", "dailyGoals"})
    private GeneralGoal generalGoal;
}

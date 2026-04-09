package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_goals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DailyGoal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "calories_done")
    private int caloriesDone;

    @Column(name = "steps_done")
    private int stepsDone;

    @Column(name = "water_done")
    private int waterDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "general_goal_id")
    private GeneralGoal generalGoals;
}
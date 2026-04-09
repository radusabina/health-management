package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

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
    private GeneralGoals generalGoals;
}
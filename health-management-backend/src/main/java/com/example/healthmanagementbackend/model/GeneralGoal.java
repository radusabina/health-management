package com.example.healthmanagementbackend.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
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
@Table(name = "general_goals")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GeneralGoal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "calorie_goal")
    private int calorieGoal;

    @Column(name = "steps_goal")
    private int stepsGoal;

    @Column(name = "water_goal")
    private int waterGoal;

    @Column(name = "weight_target")
    private int weightTarget;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
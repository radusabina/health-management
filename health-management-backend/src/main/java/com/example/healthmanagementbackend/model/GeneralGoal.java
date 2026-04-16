package com.example.healthmanagementbackend.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.OneToMany;
import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "general_goals",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id"})
        })
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

    @Column(name = "water_goal")
    private int waterGoal;

    @Column(name = "weight_target")
    private int weightTarget;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "generalGoal")
    private List<DailyGoal> dailyGoals;
}
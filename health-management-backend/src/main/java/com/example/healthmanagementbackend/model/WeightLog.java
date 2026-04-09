package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weight_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class WeightLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "weight_kg")
    private double weight; // kg

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
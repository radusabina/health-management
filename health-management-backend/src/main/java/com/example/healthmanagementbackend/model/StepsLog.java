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
@Table(name = "steps_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class StepsLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "steps")
    private int steps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
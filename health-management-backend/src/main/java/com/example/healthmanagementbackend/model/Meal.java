package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meals")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Meal {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "meal_type")
    private String mealType;

    @Column(name = "description")
    private String description;

    @Column(name = "calories")
    private int calories;

    @Column(name = "created_at")
    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "recommendation_steps")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RecommendationStep {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    @JsonIgnore
    private Recommendation recommendation;

    @Column(name = "step_number")
    private int stepNumber;

    @Column(name = "step_text", columnDefinition = "TEXT")
    private String stepText;
}

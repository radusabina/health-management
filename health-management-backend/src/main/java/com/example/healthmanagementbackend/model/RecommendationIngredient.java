package com.example.healthmanagementbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Table(name = "recommendation_ingredients")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RecommendationIngredient {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommendation_id", nullable = false)
    @JsonIgnore
    private Recommendation recommendation;

    @Column(name = "name", nullable = false)
    private String name;

    /** The amount as returned by Spoonacular (in whatever unit). */
    @Column(name = "amount")
    private double amount;

    @Column(name = "unit")
    private String unit;
}

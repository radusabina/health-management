package com.example.healthmanagementbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recommendations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue
    private UUID id;

    /** Spoonacular numeric recipe id – used as the cache key. */
    @Column(name = "spoonacular_id", unique = true, nullable = false)
    private int spoonacularId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "ready_in_minutes")
    private int readyInMinutes;

    @Column(name = "servings")
    private int servings;

    @Column(name = "health_score")
    private double healthScore;

    @Column(name = "total_calories")
    private double totalCalories;

    // ── caloric breakdown ─────────────────────────────────────────────────────

    @Column(name = "percent_protein")
    private double percentProtein;

    @Column(name = "percent_fat")
    private double percentFat;

    @Column(name = "percent_carbs")
    private double percentCarbs;

    // ── cuisines (simple collection) ──────────────────────────────────────────

    @ElementCollection
    @CollectionTable(
            name = "recommendation_cuisines",
            joinColumns = @JoinColumn(name = "recommendation_id")
    )
    @Column(name = "cuisine")
    @Builder.Default
    private List<String> cuisines = new ArrayList<>();

    // ── related entities ──────────────────────────────────────────────────────

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecommendationIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recommendation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    @Builder.Default
    private List<RecommendationStep> steps = new ArrayList<>();

    @Column(name = "cached_at")
    private LocalDateTime cachedAt;
}

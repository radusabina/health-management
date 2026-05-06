package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    Optional<Recommendation> findBySpoonacularId(int spoonacularId);
}

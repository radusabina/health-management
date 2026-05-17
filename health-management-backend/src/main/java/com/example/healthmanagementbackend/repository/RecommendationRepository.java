package com.example.healthmanagementbackend.repository;

import com.example.healthmanagementbackend.model.Recommendation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, UUID> {

    Optional<Recommendation> findBySpoonacularId(int spoonacularId);

    /**
     * Returns cached recommendations that contain at least one of the given ingredient names
     * (case-insensitive). DISTINCT ensures each recommendation appears only once even when
     * multiple ingredients match.
     */
    @Query("SELECT DISTINCT r FROM Recommendation r JOIN r.ingredients i " +
           "WHERE LOWER(i.name) IN :names")
    List<Recommendation> findByAnyIngredient(@Param("names") List<String> names, Pageable pageable);
}

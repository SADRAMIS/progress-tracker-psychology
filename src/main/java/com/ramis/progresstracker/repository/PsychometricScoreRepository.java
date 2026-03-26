package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.PsychometricScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PsychometricScoreRepository extends JpaRepository<PsychometricScore, Long> {
    List<PsychometricScore> findByUserId(Long userId);

    @Query(value = "SELECT * FROM psychometric_scores WHERE user_id = :userId ORDER BY created_date DESC LIMIT 1", nativeQuery = true)
    Optional<PsychometricScore> getLatestScoreByUserId(@Param("userId") Long userId);

}

package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.ProgressRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface ProgressRecordRepository extends JpaRepository<ProgressRecord, Long> {
    List<ProgressRecord> findByUserId(Long userId);

    @Query("SELECT p FROM ProgressRecord p WHERE p.user.id = :userId AND p.date BETWEEN :startDate AND :endDate")
    List<ProgressRecord> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(p.xpGained) FROM ProgressRecord p WHERE p.user.id = :userId AND p.date BETWEEN :startDate AND :endDate")
    Integer getTotalXPByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}

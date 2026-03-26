package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByUserId(Long userId);
    Optional<Answer> findByUserIdAndQuestionId(Long userId, Long questionId);

    @Query("SELECT a FROM Answer a WHERE a.user.id = :userId AND a.levelCompleted > 0")
    List<Answer> findCompletedAnswersByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.user.id = :userId AND a.isCorrect = true")
    int countCorrectAnswersByUserId(@Param("userId") Long userId);

}

package com.ramis.progresstracker.repository;

import com.ramis.progresstracker.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByCategory(String category);
    List<Question> findByCategoryAndDifficulty(String category, Question.Difficulty difficulty);
}

package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.RecommendationDTO;
import com.ramis.progresstracker.entity.PsychometricScore;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.PsychometricScoreRepository;
import com.ramis.progresstracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // Автоматическая очистка после тестов
public class RecommendationServiceTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PsychometricScoreRepository psychometricScoreRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // Создаем пользователя
        User testUser = new User();
        testUser.setEmail("test-rec@example.com");
        testUser.setName("Test Recommendation User");
        User savedUser = userRepository.save(testUser);
        this.testUserId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
        // ✅ Используем стандартный метод deleteById для всех записей
        if (testUserId != null) {
            // Удаляем psychometric scores
            List<PsychometricScore> scores = psychometricScoreRepository.findByUserId(testUserId);
            for (PsychometricScore score : scores) {
                psychometricScoreRepository.deleteById(score.getId());
            }

            // Удаляем пользователя (каскадное удаление)
            userRepository.deleteById(testUserId);
        }
    }

    @Test
    void testGenerateRecommendations_WithLowMotivation() {
        // Arrange - Создаем psychometric score с низкой мотивацией
        PsychometricScore score = new PsychometricScore();
        score.setUser(userRepository.findById(testUserId).get());
        score.setMotivation(30.0);  // Низкая мотивация
        score.setConfidence(70.0);
        score.setResilience(60.0);
        score.setFocus(80.0);
        score.setConsistency(70.0);
        psychometricScoreRepository.save(score);

        // Act
        List<RecommendationDTO> recommendations =
                recommendationService.generateRecommendations(testUserId);

        // Assert
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        assertTrue(recommendations.stream()
                .anyMatch(r -> r.getType().equals("MOTIVATION") &&
                        r.getPriority().equals("HIGH")));

        System.out.println("Recommendations count: " + recommendations.size());
    }

    @Test
    void testGenerateRecommendations_WithLowFocus() {
        // Arrange - Создаем score с низким фокусом
        PsychometricScore score = new PsychometricScore();
        score.setUser(userRepository.findById(testUserId).get());
        score.setMotivation(70.0);
        score.setConfidence(70.0);
        score.setResilience(70.0);
        score.setFocus(40.0);  // Низкий фокус
        score.setConsistency(70.0);
        psychometricScoreRepository.save(score);

        // Act
        List<RecommendationDTO> recommendations =
                recommendationService.generateRecommendations(testUserId);

        // Assert
        assertTrue(recommendations.stream()
                .anyMatch(r -> r.getType().equals("FOCUS") &&
                        r.getPriority().equals("MEDIUM")));
    }
}
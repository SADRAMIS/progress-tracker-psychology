package com.ramis.progresstracker.service;

import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // Для порядка выполнения
public class PsychometricAnalysisServiceTest {

    @Autowired
    private PsychometricAnalysisService psychometricService;

    @Autowired
    private UserRepository userRepository;

    private Long testUserId;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        User testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setLevel(1);
        testUser.setTotalXP(100);
        testUser.setMotivationScore(50.0);

        User savedUser = userRepository.save(testUser);
        this.testUserId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
        if (testUserId != null) {
            userRepository.deleteById(testUserId);
        }
    }

    @Test
    void testMotivationCalculation() {
        // Act
        var result = psychometricService.analyzePsychologicalProfile(testUserId);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMotivation() >= 0);
        assertTrue(result.getMotivation() <= 100);
        System.out.println("Motivation score: " + result.getMotivation());  // Для отладки
    }

    @Test
    void testFullPsychometricAnalysis() {
        // Act
        var result = psychometricService.analyzePsychologicalProfile(testUserId);

        // Assert все метрики
        assertNotNull(result);
        assertAll("Psychometric metrics",
                () -> assertTrue(result.getMotivation() >= 0 && result.getMotivation() <= 100),
                () -> assertTrue(result.getConfidence() >= 0 && result.getConfidence() <= 100),
                () -> assertTrue(result.getResilience() >= 0 && result.getResilience() <= 100),
                () -> assertTrue(result.getFocus() >= 0 && result.getFocus() <= 100),
                () -> assertTrue(result.getConsistency() >= 0 && result.getConsistency() <= 100),
                () -> assertNotNull(result.getAnalysis()),
                () -> assertNotNull(result.getRecommendation())
        );
    }
}

package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.FullAnswerDTO;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.QuestionRepository;
import com.ramis.progresstracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProgressServiceTest {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    private Long testUserId;
    private Long testQuestionId = 1L;  // Используем существующий вопрос из БД

    @BeforeEach
    void setUp() {
        // Только пользователь
        User testUser = new User();
        testUser.setEmail("test-progress@example.com");
        testUser.setName("Test Progress User");
        User savedUser = userRepository.save(testUser);
        this.testUserId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
        if (testUserId != null) {
            userRepository.deleteById(testUserId);
        }
        //  question - используем существующий
    }

    @Test
    void testSubmitAnswer_Level3_ReturnsCorrectXP() {
        // Arrange
        FullAnswerDTO dto = new FullAnswerDTO();
        dto.setUserId(testUserId);
        dto.setQuestionId(testQuestionId);  // ID=1 из БД
        dto.setLevelCompleted(3);
        dto.setAttempts(2);
        dto.setTimeSpentMinutes(25);

        // Act
        int xpGained = progressService.submitAnswer(dto);

        // Assert
        assertTrue(xpGained >= 50);
        assertTrue(xpGained <= 200);  // Зависит от XP вопроса в БД
        System.out.println("XP gained Level 3: " + xpGained);
    }

    @Test
    void testSubmitAnswer_Level1_ReturnsBaseXP() {
        FullAnswerDTO dto = new FullAnswerDTO();
        dto.setUserId(testUserId);
        dto.setQuestionId(testQuestionId);
        dto.setLevelCompleted(1);
        dto.setAttempts(1);
        dto.setTimeSpentMinutes(15);

        int xpGained = progressService.submitAnswer(dto);

        assertTrue(xpGained > 0);  // Базовый XP
        System.out.println("XP gained Level 1: " + xpGained);
    }

    @Test
    void testGetWeeklyStatistics_ReturnsCorrectFormat() {
        var stats = progressService.getWeeklyStatistics(testUserId);
        assertNotNull(stats);
        assertTrue(stats.containsKey("totalXP"));
        assertTrue(stats.containsKey("totalHours"));
        assertTrue(stats.containsKey("totalQuestions"));
        System.out.println("Weekly stats: " + stats);
    }
}
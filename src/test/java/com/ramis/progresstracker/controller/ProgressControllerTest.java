package com.ramis.progresstracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramis.progresstracker.dto.ProgressDTO;
import com.ramis.progresstracker.dto.QuestionAnswerDTO;
import com.ramis.progresstracker.service.ProgressService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasKey;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProgressController.class)
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProgressService progressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER"})  //  Добавили мок-пользователя
    void getProgressByDate_ReturnsOk() throws Exception {
        Long userId = 1L;
        String date = "2026-03-01";

        ProgressDTO dto = new ProgressDTO(
                LocalDate.parse(date),
                100,
                1.5,
                5,
                80.0,
                3,
                4,
                "XP 100 Hours 1.5 Questions 5 Accuracy 80.0 Streak 3 days"
        );

        Mockito.when(progressService.getProgressByDate(userId, date))
                .thenReturn(dto);

        mockMvc.perform(get("/api/progress/user/{userId}/date/{date}", userId, date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.xpGained", is(100)))
                .andExpect(jsonPath("$.questionsSolved", is(5)));
    }

    @Test
    @WithMockUser(roles = {"USER"})  //  Добавили мок-пользователя
    void getProgressRange_ReturnsList() throws Exception {
        Long userId = 1L;
        String start = "2026-03-01";
        String end = "2026-03-07";

        ProgressDTO dto1 = new ProgressDTO(
                LocalDate.parse(start),
                50,
                1.0,
                3,
                70.0,
                2,
                4,
                "summary 1"
        );

        ProgressDTO dto2 = new ProgressDTO(
                LocalDate.parse(end),
                150,
                2.0,
                7,
                90.0,
                4,
                5,
                "summary 2"
        );

        Mockito.when(progressService.getProgressRange(userId, start, end))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/progress/user/{userId}/range", userId)
                        .param("startDate", start)
                        .param("endDate", end))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].xpGained", is(50)))
                .andExpect(jsonPath("$[1].xpGained", is(150)));
    }

    @Test
    @WithMockUser(roles = {"USER"})  //  Добавили мок-пользователя
    void submitAnswer_ReturnsXpGained() throws Exception {
        QuestionAnswerDTO dto = new QuestionAnswerDTO();
        dto.setQuestionId(1L);
        dto.setLevelCompleted(3);
        dto.setAttempts(2);
        dto.setTimeSpentMinutes(25);
        dto.setNotes("test");

        int xp = 150;
        Mockito.when(progressService.submitAnswer(dto)).thenReturn(xp);

        mockMvc.perform(post("/api/progress/submit-answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(csrf()))  //  CSRF токен
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.xpGained", is(150)));
    }

    @Test
    @WithMockUser(roles = {"USER"})  //  Добавили мок-пользователя
    void getWeeklyStats_ReturnsMap() throws Exception {
        Long userId = 1L;
        Map<String, Object> stats = Map.of(
                "totalXP", 300,
                "totalHours", "5.0",
                "totalQuestions", 10,
                "avgAccuracy", "80.0",
                "daysActive", 4
        );

        Mockito.when(progressService.getWeeklyStatistics(userId))
                .thenReturn(stats);

        mockMvc.perform(get("/api/progress/user/{userId}/weekly-stats", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalXP", is(300)))
                .andExpect(jsonPath("$", hasKey("totalHours")))
                .andExpect(jsonPath("$", hasKey("totalQuestions")));
    }
}
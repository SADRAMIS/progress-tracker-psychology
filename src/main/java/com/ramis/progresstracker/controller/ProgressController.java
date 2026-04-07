package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.ProgressDTO;
import com.ramis.progresstracker.dto.FullAnswerDTO;
import com.ramis.progresstracker.dto.SubmitAnswerRequest;
import com.ramis.progresstracker.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProgressController {

    private final ProgressService progressService;

    /**
     * Получить прогресс пользователя за дату
     * GET /api/progress/user/{userId}/date/{date}
     */
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<ProgressDTO> getProgress(
            @PathVariable Long userId,
            @PathVariable String date) {
        ProgressDTO progress = progressService.getProgressByDate(userId, date);
        return ResponseEntity.ok(progress);
    }

    /**
     * Получить прогресс за период
     * GET /api/progress/user/{userId}/range?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/user/{userId}/range")
    public ResponseEntity<List<ProgressDTO>> getProgressRange(
            @PathVariable Long userId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<ProgressDTO> progress = progressService.getProgressRange(userId, startDate, endDate);
        return ResponseEntity.ok(progress);
    }

    /**
     * Отправить ответ на вопрос
     * POST /api/progress/submit-answer
     */
    // Полный ответ (со временем, заметками)
    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(@RequestBody FullAnswerDTO answerDTO) {
        int xpGained = progressService.submitAnswer(answerDTO);
        return ResponseEntity.ok().body(Map.of("xpGained", xpGained));
    }

    // Простой быстрый ответ (только isCorrect)
    @PostMapping("/submit-simple")
    public ResponseEntity<?> submitAnswer(@RequestBody SubmitAnswerRequest request) {
        int xpGained = progressService.submitSimpleAnswer(
                request.userId(),
                request.questionId(),
                request.isCorrect());
        return ResponseEntity.ok().body(Map.of("xpGained", xpGained));
    }

    /**
     * Получить статистику за неделю
     * GET /api/progress/user/{userId}/weekly-stats
     */
    @GetMapping("/user/{userId}/weekly-stats")
    public ResponseEntity<?> getWeeklyStats(@PathVariable Long userId) {
        Map<String, Object> stats = progressService.getWeeklyStatistics(userId);
        return ResponseEntity.ok(stats);
    }

}

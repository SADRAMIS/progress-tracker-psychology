package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.RecommendationDTO;
import com.ramis.progresstracker.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Получить рекомендации для пользователя
     * GET /api/recommendations/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(
            @PathVariable Long userId) {
        List<RecommendationDTO> recommendations = recommendationService.generateRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

}

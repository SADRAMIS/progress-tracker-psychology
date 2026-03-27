package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.PsychometricDTO;
import com.ramis.progresstracker.service.PsychometricAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/psychometric")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PsychometricController {

    private final PsychometricAnalysisService psychometricService;

    /**
     * Получить психометрический анализ пользователя
     * GET /api/psychometric/analyze/{userId}
     */
    @GetMapping("/analyze/{userId}")
    public ResponseEntity<PsychometricDTO> analyzePsychologicalProfile(
            @PathVariable Long userId) {
        PsychometricDTO analysis = psychometricService.analyzePsychologicalProfile(userId);
        return ResponseEntity.ok(analysis);
    }

}

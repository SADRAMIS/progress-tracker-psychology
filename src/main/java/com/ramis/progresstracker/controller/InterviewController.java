package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.dto.InterviewAnswerDTO;
import com.ramis.progresstracker.dto.InterviewResultDTO;
import com.ramis.progresstracker.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
@CrossOrigin
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/submit-answer")
    public ResponseEntity<InterviewResultDTO> submitAnswer(@RequestBody InterviewAnswerDTO dto) {
        InterviewResultDTO result = interviewService.checkAnswer(dto);
        return ResponseEntity.ok(result);
    }
}
package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.service.QuestionImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    private final QuestionImportService questionImportService;

    @PostMapping("/import-questions")
    public ResponseEntity<String> importQuestions() {
        questionImportService.importQuestionsFromDocx();
        return ResponseEntity.ok("✅ Imported questions from questions.docx");
    }
}

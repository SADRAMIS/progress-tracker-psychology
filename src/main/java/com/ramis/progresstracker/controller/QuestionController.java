package com.ramis.progresstracker.controller;

import com.ramis.progresstracker.entity.Question;
import com.ramis.progresstracker.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class QuestionController {

    private final QuestionRepository repository;

    @GetMapping
    public ResponseEntity<List<Question>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Question>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(repository.findByCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
package com.ramis.progresstracker.service;

import com.ramis.progresstracker.dto.InterviewAnswerDTO;
import com.ramis.progresstracker.dto.InterviewResultDTO;
import com.ramis.progresstracker.entity.Answer;
import com.ramis.progresstracker.entity.Question;
import com.ramis.progresstracker.entity.User;
import com.ramis.progresstracker.repository.AnswerRepository;
import com.ramis.progresstracker.repository.QuestionRepository;
import com.ramis.progresstracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public InterviewResultDTO checkAnswer(InterviewAnswerDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + dto.getUserId()));
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id " + dto.getQuestionId()));

        String userText = Optional.ofNullable(dto.getUserAnswerText()).orElse("").toLowerCase();
        String model = Optional.ofNullable(question.getAnswerContent()).orElse("").toLowerCase();

        // 1. Базовая оценка по длине и разнообразию
        int baseScore = evaluateBaseQuality(userText);

        // 2. Ключевые слова, извлечённые из эталонного ответа
        KeywordEvaluation keywordEval = evaluateKeywordsFromModel(model, userText);

        // 3. Итоговый score
        int score = (int) Math.round(
                keywordEval.score * 0.6 +
                        baseScore * 0.4
        );
        score = Math.max(0, Math.min(100, score));
        boolean correct = score >= 70;

        // 4. Фидбек
        String feedback = buildFeedback(score, keywordEval);

        // 5. Сохраняем Answer
        Answer answer = new Answer();
        answer.setUser(user);
        answer.setQuestion(question);
        answer.setUserAnswerText(dto.getUserAnswerText());
        answer.setIsCorrect(correct);
        // если ответ хороший – считаем, что прошёл продвинутый уровень
        answer.setLevelCompleted(correct ? 3 : 1);
        answer.setAttempts(1);
        answer.setTimeSpentMinutes(5);
        answer.setAiScore(score);
        answer.setAiFeedback(feedback);

        answerRepository.save(answer);

        log.info("Interview answer saved for user {}, question {}, score {}",
                user.getId(), question.getId(), score);

        return new InterviewResultDTO(
                user.getId(),
                question.getId(),
                score,
                correct,
                feedback
        );
    }

    private int evaluateBaseQuality(String userText) {
        if (userText.isBlank()) {
            return 0;
        }
        String[] words = userText.split("\\s+");
        int wordCount = words.length;

        long unique = Arrays.stream(words)
                .map(w -> w.replaceAll("[^a-zA-Zа-яА-Я0-9]", ""))
                .filter(w -> !w.isBlank())
                .map(String::toLowerCase)
                .distinct()
                .count();

        int lengthScore;
        if (wordCount < 20) {
            lengthScore = 30;
        } else if (wordCount < 80) {
            lengthScore = 70;
        } else {
            lengthScore = 90;
        }

        double diversityRatio = wordCount == 0 ? 0 : (unique * 1.0 / wordCount);
        int diversityScore;
        if (diversityRatio < 0.3) {
            diversityScore = 40;
        } else if (diversityRatio < 0.6) {
            diversityScore = 70;
        } else {
            diversityScore = 90;
        }

        return (lengthScore + diversityScore) / 2;
    }

    private KeywordEvaluation evaluateKeywordsFromModel(String model, String userText) {
        if (model.isBlank()) {
            return new KeywordEvaluation(0, 0, 0, List.of());
        }

        // выделяем наиболее частые слова из эталонного ответа
        String[] tokens = model.toLowerCase().split("\\W+");
        Map<String, Long> freq = Arrays.stream(tokens)
                .filter(t -> t.length() >= 4)
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        List<String> keywords = freq.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();

        int total = keywords.size();
        int matched = 0;
        List<String> missing = new ArrayList<>();

        for (String kw : keywords) {
            if (userText.contains(kw)) {
                matched++;
            } else {
                missing.add(kw);
            }
        }

        int score = total == 0 ? 0 : (int) Math.round(matched * 100.0 / total);
        return new KeywordEvaluation(score, matched, total, missing);
    }

    private String buildFeedback(int score, KeywordEvaluation eval) {
        StringBuilder fb = new StringBuilder();

        if (score >= 80) {
            fb.append("Ответ сильный: ты покрыл большую часть ключевых идей. ");
        } else if (score >= 50) {
            fb.append("Ответ неплохой, но есть пробелы. ");
        } else {
            fb.append("Ответ поверхностный: важные детали не раскрыты. ");
        }

        if (!eval.missingKeywords.isEmpty()) {
            fb.append("Обрати внимание на следующие ключевые понятия: ");
            fb.append(String.join(", ", eval.missingKeywords.stream().limit(5).toList()));
            fb.append(". ");
        }

        fb.append("Попробуй ещё раз сформулировать ответ, учитывая эти моменты.");

        return fb.toString();
    }

    private static class KeywordEvaluation {
        int score;
        int matched;
        int total;
        List<String> missingKeywords;

        public KeywordEvaluation(int score, int matched, int total, List<String> missingKeywords) {
            this.score = score;
            this.matched = matched;
            this.total = total;
            this.missingKeywords = missingKeywords;
        }
    }
}
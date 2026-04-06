package com.ramis.progresstracker.service;

import com.ramis.progresstracker.entity.Question;
import com.ramis.progresstracker.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionImportService {

    private final QuestionRepository questionRepository;

    public void importQuestionsFromDocx() {
        try (InputStream is = getClass().getResourceAsStream("/questions.docx")) {
            if (is == null) {
                log.error("❌ questions.docx NOT FOUND!");
                return;
            }

            try (XWPFDocument doc = new XWPFDocument(is)) {

                List<XWPFParagraph> paragraphs = doc.getParagraphs();
                log.info("📄 Loaded questions.docx with {} paragraphs", paragraphs.size());

                AtomicInteger numberCounter = new AtomicInteger(1);
                List<String> blockBuffer = new ArrayList<>();

                for (XWPFParagraph p : paragraphs) {
                    String text = p.getText();
                    if (text == null || text.trim().isEmpty()) {
                        continue;
                    }

                    // Новый вопрос: строка начинается с "[1)]()", "[2)]()", ...
                    if (isQuestionStart(text)) {
                        // Сохраняем предыдущий блок, если был
                        if (!blockBuffer.isEmpty()) {
                            saveQuestionBlock(blockBuffer, numberCounter.getAndIncrement());
                            blockBuffer.clear();
                        }
                    }
                    blockBuffer.add(text);
                }

                // Последний блок
                if (!blockBuffer.isEmpty()) {
                    saveQuestionBlock(blockBuffer, numberCounter.getAndIncrement());
                }

                log.info("✅ Imported {} questions from questions.docx", numberCounter.get() - 1);
            }
        } catch (Exception e) {
            log.error("❌ Failed to import questions", e);
            throw new RuntimeException("Import failed: " + e.getMessage(), e);
        }
    }

    // Для твоего формата "[1)]() Что-то там"
    private boolean isQuestionStart(String text) {
        String trimmed = text.trim();
        // Начинается с [, далее цифры, )
        boolean isStart = trimmed.matches("^\\[[0-9]+\\)\\]\\(\\).*");
        log.debug("isQuestionStart('{}') = {}", trimmed, isStart);
        return isStart;
    }

    /*private void saveQuestionBlock(List<String> blockLines, int questionNumber) {
        Question q = new Question();
        q.setQuestionNumber(questionNumber);

        // 1. Title: первая строка без "[1)]()"
        String rawTitle = blockLines.get(0);
        q.setTitle(extractTitle(rawTitle));

        // 2. Всё остальное — theoryContent (проще и надёжнее для начала)
        StringBuilder theory = new StringBuilder();
        for (int i = 1; i < blockLines.size(); i++) {
            theory.append(blockLines.get(i)).append("\n");
        }
        q.setTheoryContent(theory.toString().trim());

        // Пока без codeExample — можно добавить потом
        q.setCodeExample(null);

        q.setCategory("BASICJAVA");
        q.setDifficulty(Question.Difficulty.EASY);
        q.setXpValue(100);

        questionRepository.save(q);
        log.info("💾 Saved question #{}: {}", questionNumber, q.getTitle());
    }*/

    private void saveQuestionBlock(List<String> blockLines, int questionNumber) {
        Question q = new Question();
        q.setQuestionNumber(questionNumber);

        // 1. Title: первая строка без "[1)]()"
        String rawTitle = blockLines.get(0);
        q.setTitle(extractTitle(rawTitle));

        // 2.  Парсим answer / example / theory
        int answerIdx = -1, exampleIdx = -1;
        for (int i = 1; i < blockLines.size(); i++) {
            String line = blockLines.get(i);
            if (line.contains("Ответ:")) answerIdx = i;
            if (line.contains("Пример:")) exampleIdx = i;
        }

        StringBuilder theory = new StringBuilder();
        StringBuilder answer = new StringBuilder();
        StringBuilder code = new StringBuilder();

        // theory: до "Ответ:"
        if (answerIdx > 0) {
            for (int i = 1; i < answerIdx; i++) {
                theory.append(blockLines.get(i)).append("\n");
            }
        }

        // answer: между "Ответ:" и "Пример:"
        if (answerIdx > 0) {
            int endAnswer = (exampleIdx > 0 ? exampleIdx : blockLines.size());
            for (int i = answerIdx + 1; i < endAnswer; i++) {
                answer.append(blockLines.get(i)).append("\n");
            }
        }

        // code: после "Пример:"
        if (exampleIdx > 0) {
            for (int i = exampleIdx + 1; i < blockLines.size(); i++) {
                code.append(blockLines.get(i)).append("\n");
            }
        }

        q.setTheoryContent(theory.toString().trim().isEmpty() ? null : theory.toString().trim());
        q.setAnswerContent(answer.toString().trim().isEmpty() ? null : answer.toString().trim());
        q.setCodeExample(code.toString().trim().isEmpty() ? null : code.toString().trim());

        q.setCategory("BASICJAVA");
        q.setDifficulty(Question.Difficulty.EASY);
        q.setXpValue(100);

        questionRepository.save(q);
        log.info("💾 Q#{}: title={}, answer={}, code={}",
                questionNumber,
                q.getTitle().substring(0, Math.min(50, q.getTitle().length())),
                q.getAnswerContent() != null ? "✓" : "✗",
                q.getCodeExample() != null ? "✓" : "✗");
    }

    private String extractTitle(String rawLine) {
        String trimmed = rawLine.trim();
        // Убираем "[1)]()" или "[23)]()" и пробел после
        return trimmed.replaceFirst("^\\[[0-9]+\\)\\]\\(\\)\\s*", "");
    }

    private int findLineIndex(List<String> lines, String marker) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains(marker)) {
                return i;
            }
        }
        return -1;
    }
}
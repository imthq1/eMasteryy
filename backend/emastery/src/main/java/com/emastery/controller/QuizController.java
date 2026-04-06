package com.emastery.controller;

import com.emastery.domain.reqDTO.EvaluateGuessRequest;
import com.emastery.domain.reqDTO.GenerateReadingRequest;
import com.emastery.domain.resDTO.EvaluateGuessResponse;
import com.emastery.domain.resDTO.GenerateReadingResponse;
import com.emastery.service.QuizService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
// ĐÃ SỬA: Đổi từ "/api/quiz" thành "/api/reading" cho khớp với Frontend
@RequestMapping("/api/reading")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // ĐÃ SỬA: Đổi từ "/generate-reading" thành "/generate"
    @PostMapping("/generate")
    public ResponseEntity<?> generateReading(@RequestBody GenerateReadingRequest request) {
        try {
            if (request.getGeminiApiKey() == null || request.getGeminiApiKey().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu API Key"));
            }
            String level = request.getEnglishLevel() != null ? request.getEnglishLevel() : "ez";
            GenerateReadingResponse response = quizService.generateReading(level, request.getUsedDescriptions(), request.getGeminiApiKey());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // Dự đoán Frontend của bạn sẽ gọi endpoint này để chấm điểm
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluateGuess(@RequestBody EvaluateGuessRequest request) {
        try {
            if (request.getGeminiApiKey() == null || request.getGeminiApiKey().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Thiếu API Key"));
            }
            EvaluateGuessResponse response = quizService.evaluateGuess(request.getUserGuess(), request.getCorrectPhrase(), request.getGeminiApiKey());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
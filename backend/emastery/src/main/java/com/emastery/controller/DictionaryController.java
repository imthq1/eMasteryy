package com.emastery.controller;

import com.emastery.domain.reqDTO.TranslateRequest;
import com.emastery.domain.resDTO.TranslateResponse;
import com.emastery.domain.resDTO.WordExplanationDto;
import com.emastery.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dictionary")
@RequiredArgsConstructor
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @PostMapping("/translate")
    public ResponseEntity<?> translate(@RequestBody TranslateRequest request) {
        // Validate input
        if (request.getWord() == null || request.getWord().isBlank()
                || request.getApiKey() == null || request.getApiKey().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Thiếu từ hoặc API key."));
        }

        if (!request.getWord().trim().matches("^[a-zA-Z\\-]+$")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Vui lòng chỉ nhập một từ đơn hợp lệ (không chứa khoảng trắng, số hoặc ký tự đặc biệt)."));
        }

        try {
            String rawExplanation = dictionaryService.translateWord(
                    request.getWord().trim(), request.getApiKey());

            WordExplanationDto explanationDto = dictionaryService.parseToDto(rawExplanation);

            return ResponseEntity.ok(new TranslateResponse(request.getWord(), explanationDto));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Lỗi không xác định.", "detail", e.getMessage()));
        }
    }
}

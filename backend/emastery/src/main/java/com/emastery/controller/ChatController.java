package com.emastery.controller;

import com.emastery.domain.reqDTO.ChatRequest;
import com.emastery.domain.resDTO.ChatResponse;
import com.emastery.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/Chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest request) {
        try {
            if (isInvalidRequest(request)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message",
                                "Vui lòng cung cấp đầy đủ thông tin."));
            }

            ChatResponse response = chatService.generateAnswer(
                    request.getConversation(),
                    request.getUsername(),
                    request.getGender(),
                    request.getAge(),
                    request.getEnglishLevel(),
                    request.getGeminiApiKey()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message",
                            "Đã xảy ra lỗi: " + ex.getMessage()));
        }
    }

    private boolean isInvalidRequest(ChatRequest req) {
        return req.getUsername() == null     || req.getUsername().isBlank()     ||
                req.getGender() == null       || req.getGender().isBlank()       ||
                req.getAge() <= 0                                               ||
                req.getGeminiApiKey() == null  || req.getGeminiApiKey().isBlank()  ||
                req.getConversation() == null                                   ||
                req.getConversation().getQuestion() == null                     ||
                req.getConversation().getQuestion().isBlank();
    }
}
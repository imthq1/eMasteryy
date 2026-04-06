package com.emastery.service;

import com.emastery.domain.Enum.Level;
import com.emastery.domain.reqDTO.ChatMessage;
import com.emastery.domain.reqDTO.Conversation;
import com.emastery.domain.resDTO.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final int MAX_RETRIES = 2;
    private final HealthcheckService healthcheckService;
    public ChatResponse generateAnswer(
            Conversation conversation,
            String username,
            String gender,
            int age,
            Level englishLevel,
            String apiKey) throws Exception {

        if (this.healthcheckService.healthcheck(apiKey)) {
            throw new Exception("API Key không hợp lệ.");
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new Exception("API Key không được để trống.");
        }

        String systemInstruction = Instructions.getBasicInstruction(username, gender, age, englishLevel);

        if (conversation.getChatHistory() != null && conversation.getChatHistory().stream()
                .anyMatch(h -> h.getMessage() == null || h.getMessage().isEmpty() || "string".equals(h.getMessage()))) {
            conversation.setChatHistory(null);
        }

        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("contents", buildContents(conversation));

                Map<String, Object> sysInstr = new HashMap<>();
                sysInstr.put("parts", new Object[]{ Map.of("text", systemInstruction) });
                requestBody.put("system_instruction", sysInstr);

                Map<String, Object> genConfig = new HashMap<>();
                genConfig.put("maxOutputTokens", 1000);
                genConfig.put("temperature", attempt == 0 ? 1.0 : 0.5);
                requestBody.put("generationConfig", genConfig);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
                String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey;

                ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.error("API error (attempt {}): Status Code: {}, Body: {}", (attempt + 1), response.getStatusCode(), response.getBody());
                    continue;
                }

                String responseBody = response.getBody();
                log.info("Gemini response (attempt {}): {}", (attempt + 1), responseBody);
                log.info("User question: {}", conversation.getQuestion());

                JsonNode rootNode = objectMapper.readTree(responseBody);
                String message = rootNode.path("candidates").get(0)
                        .path("content")
                        .path("parts").get(0)
                        .path("text").asText();

                log.info("Message before processing: {}", message);

                if (message.trim().startsWith("```json")) {
                    Pattern pattern = Pattern.compile("```json\\n([\\s\\S]*?)\\n```");
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        message = matcher.group(1);
                        log.info("Extracted JSON: {}", message);
                    } else {
                        log.warn("Failed to extract JSON from Markdown: {}", message);
                        continue;
                    }
                }

                if (isValidJson(message)) {
                    try {
                        return objectMapper.readValue(message, ChatResponse.class);
                    } catch (Exception jsonEx) {
                        log.error("JSON deserialization error: {}", jsonEx.getMessage());
                    }
                }

                log.warn("Invalid response, retrying (attempt {})...", (attempt + 1));

            } catch (HttpClientErrorException ex) {
                log.error("Request error (attempt {}): {}", (attempt + 1), ex.getMessage());
            } catch (Exception ex) {
                log.error("Unexpected error (attempt {}): {}", (attempt + 1), ex.getMessage());
            }
        }

        ChatResponse fallbackResponse = new ChatResponse();
        fallbackResponse.setMessageInMarkdown("Xin lỗi, tôi chỉ hỗ trợ các câu hỏi liên quan đến học tiếng Anh. Bạn muốn học gì về tiếng Anh hôm nay?");
        fallbackResponse.setSuggestions(generateContextualSuggestions(conversation.getQuestion()));
        return fallbackResponse;
    }

    private List<Map<String, Object>> buildContents(Conversation conversation) {
        List<Map<String, Object>> contents = new ArrayList<>();
        String lastRole = "";

        if (conversation.getChatHistory() != null && !conversation.getChatHistory().isEmpty()) {
            for (ChatMessage history : conversation.getChatHistory()) {
                String currentRole = history.isFromUser() ? "user" : "model";

                if (currentRole.equals(lastRole) && !contents.isEmpty()) {
                    Map<String, Object> lastContent = contents.get(contents.size() - 1);
                    Object[] parts = (Object[]) lastContent.get("parts");
                    Map<String, String> textMap = (Map<String, String>) parts[0];

                    String newText = textMap.get("text") + "\n" + history.getMessage();
                    lastContent.put("parts", new Object[]{ Map.of("text", newText) });
                } else {
                    // Nếu khác role thì thêm mới bình thường
                    Map<String, Object> content = new HashMap<>();
                    content.put("role", currentRole);
                    content.put("parts", new Object[]{ Map.of("text", history.getMessage()) });
                    contents.add(content);
                    lastRole = currentRole;
                }
            }
        }

        // Xử lý câu hỏi hiện tại
        if ("user".equals(lastRole) && !contents.isEmpty()) {
            // Nếu tin nhắn lịch sử cuối cùng cũng là user, gộp câu hỏi hiện tại vào đó luôn
            Map<String, Object> lastContent = contents.get(contents.size() - 1);
            Object[] parts = (Object[]) lastContent.get("parts");
            Map<String, String> textMap = (Map<String, String>) parts[0];

            String newText = textMap.get("text") + "\n" + conversation.getQuestion();
            lastContent.put("parts", new Object[]{ Map.of("text", newText) });
        } else {
            Map<String, Object> currentQuestion = new HashMap<>();
            currentQuestion.put("role", "user");
            currentQuestion.put("parts", new Object[]{ Map.of("text", conversation.getQuestion()) });
            contents.add(currentQuestion);
        }

        return contents;
    }

    private boolean isValidJson(String str) {
        try {
            objectMapper.readTree(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<String> generateContextualSuggestions(String question) {
        if (question == null || question.toLowerCase().contains("xin chào") || "hi".equals(question.toLowerCase())) {
            return Arrays.asList(
                    "Bạn muốn học cách giới thiệu bản thân bằng tiếng Anh không?",
                    "Chúng ta có thể luyện từ vựng cơ bản không?"
            );
        }

        if (question.toLowerCase().contains("từ vựng")) {
            return Arrays.asList(
                    "Bạn muốn học từ vựng về chủ đề nào tiếp theo?",
                    "Chúng ta có thể luyện cách sử dụng từ vựng trong câu không?"
            );
        }

        if (question.toLowerCase().contains("ngữ pháp")) {
            return Arrays.asList(
                    "Bạn muốn học cấu trúc ngữ pháp nào tiếp theo?",
                    "Chúng ta có thể luyện tập viết câu với ngữ pháp vừa học không?"
            );
        }

        return Arrays.asList(
                "Bạn muốn học từ vựng về chủ đề nào?",
                "Chúng ta có thể luyện ngữ pháp cơ bản không?"
        );
    }
}
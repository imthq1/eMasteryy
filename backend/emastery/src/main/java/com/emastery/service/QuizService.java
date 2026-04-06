package com.emastery.service;

import com.emastery.domain.resDTO.EvaluateGuessResponse;
import com.emastery.domain.resDTO.GenerateReadingResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuizService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public QuizService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    private String callGeminiApi(String prompt, String apiKey, String systemInstruction) throws Exception {
// BẮT BUỘC PHẢI DÙNG BẢN LITE ĐỂ CÓ 1000 LƯỢT/NGÀY
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=" + apiKey;
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", prompt))));

        requestBody.put("contents", contents);
        requestBody.put("system_instruction", Map.of("parts", List.of(Map.of("text", systemInstruction))));
        requestBody.put("generationConfig", Map.of("maxOutputTokens", 1000, "temperature", 0.7));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JsonNode root = objectMapper.readTree(response.getBody());
            String text = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            return removeMarkdownCodeBlock(text);
        }
        throw new Exception("Lỗi khi gọi API Gemini.");
    }

    // 1. Hàm tạo câu hỏi (Generate Reading)
    public GenerateReadingResponse generateReading(String level, List<String> usedDescriptions, String apiKey) throws Exception {
        String sysInstruction = "Bạn là giáo viên tiếng Anh. Nhiệm vụ của bạn là tạo một câu đố đoán từ vựng (Vocabulary Guessing). " +
                "Bạn chỉ được trả về ĐÚNG MỘT OBJECT JSON hợp lệ, KHÔNG kèm văn bản nào khác. " +
                "Định dạng bắt buộc: {\"phrase\": \"<từ vựng/cụm từ đáp án>\", \"description\": \"<1-2 câu tiếng Anh mô tả về từ đó nhưng KHÔNG chứa từ đó>\", \"translation\": \"<Bản dịch tiếng Việt của description>\"}";

        String userPrompt = "Hãy tạo 1 câu đố cho học sinh trình độ: " + level + ". ";
        if (usedDescriptions != null && !usedDescriptions.isEmpty()) {
            userPrompt += "KHÔNG sử dụng lại các ý tưởng sau: " + String.join(" | ", usedDescriptions);
        }

        String jsonResponse = callGeminiApi(userPrompt, apiKey, sysInstruction);
        return objectMapper.readValue(jsonResponse, GenerateReadingResponse.class);
    }

    // 2. Hàm chấm điểm (Evaluate Guess)
    public EvaluateGuessResponse evaluateGuess(String userGuess, String correctPhrase, String apiKey) throws Exception {
        String sysInstruction = "Bạn là người chấm điểm. " +
                "Bạn chỉ được trả về ĐÚNG MỘT OBJECT JSON hợp lệ định dạng: {\"accuracy\": <số nguyên từ 0-100>, \"explanation\": \"<Giải thích tiếng Việt tại sao được điểm đó, chỉ ra lỗi sai nếu có>\"}.";

        String userPrompt = String.format("Đáp án đúng là '%s'. Học sinh đoán là '%s'. Hãy chấm điểm độ chính xác về mặt ngữ nghĩa và chính tả.", correctPhrase, userGuess);

        String jsonResponse = callGeminiApi(userPrompt, apiKey, sysInstruction);
        return objectMapper.readValue(jsonResponse, EvaluateGuessResponse.class);
    }

    private String removeMarkdownCodeBlock(String input) {
        if (input == null) return "";
        Pattern pattern = Pattern.compile("^`{3}(?:json)?\\s*\\n([\\s\\S]*?)\\n`{3}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input.trim());
        return matcher.find() ? matcher.group(1).trim() : input.trim();
    }
}
package com.emastery.service;

import com.emastery.domain.resDTO.WordExplanationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final RestTemplate restTemplate;
    private static final int MAX_RETRIES = 2;
    private static final String GEMINI_URL =
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";

    public String translateWord(String word, String apiKey) {
        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("API Key không được để trống.");

        for (int attempt = 0; attempt <= MAX_RETRIES; attempt++) {
            try {
                double temperature = attempt == 0 ? 0.7 : 0.5;

                Map<String, Object> requestBody = Map.of(
                        "contents", List.of(Map.of(
                                "role", "user",
                                "parts", List.of(Map.of("text", "Giải nghĩa từ \"" + word + "\" bằng tiếng Việt."))
                        )),
                        "system_instruction", Map.of(
                                "parts", List.of(Map.of("text", buildSystemInstruction(word)))
                        ),
                        "generationConfig", Map.of(
                                "maxOutputTokens", 3000,
                                "temperature", temperature
                        )
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                ResponseEntity<Map> response = restTemplate.postForEntity(
                        GEMINI_URL + apiKey, entity, Map.class);

                if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                    System.out.println("[Gemini API attempt " + (attempt + 1) + "] Failed");
                    continue;
                }

                String message = extractMessageFromResponse(response.getBody());
                String cleaned = removeMarkdownCodeBlock(message.trim());
                System.out.println("===== RAW GEMINI RESPONSE =====");
                System.out.println(cleaned);
                System.out.println("================================");
                if (message == null || message.isBlank()) {
                    System.out.println("Empty response from Gemini.");
                    continue;
                }

                return removeMarkdownCodeBlock(message.trim());

            } catch (Exception ex) {
                System.out.println("[Error - attempt " + (attempt + 1) + "]: " + ex.getMessage());
            }
        }

        throw new RuntimeException("Không thể tạo giải nghĩa sau nhiều lần thử.");
    }

    @SuppressWarnings("unchecked")
    private String extractMessageFromResponse(Map body) {
        try {
            List<Map> candidates = (List<Map>) body.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return null;
        }
    }

    public WordExplanationDto parseToDto(String rawText) {
        WordExplanationDto dto = new WordExplanationDto();
        if (rawText == null || rawText.isBlank()) return dto;

        // Tách tại vị trí bắt đầu dòng có dạng "1." "2." ... "7."
        String[] parts = rawText.split("(?m)(?=^\\d+\\.\\s)");

        for (String part : parts) {
            String trimmed = part.trim();
            if (trimmed.isEmpty()) continue;

            // Lấy nội dung sau dòng tiêu đề (dòng đầu tiên)
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline == -1) continue; // không có nội dung

            String content = trimmed.substring(firstNewline).trim();

            if (trimmed.startsWith("1."))      dto.setPronunciation(content);
            else if (trimmed.startsWith("2.")) dto.setMeaning(content);
            else if (trimmed.startsWith("3.")) dto.setGrammarUsage(content);
            else if (trimmed.startsWith("4.")) dto.setPhrasesAndIdioms(content);
            else if (trimmed.startsWith("5.")) dto.setSynonymsAndAntonyms(content);
            else if (trimmed.startsWith("6.")) dto.setFunFactsAndTips(content);
            else if (trimmed.startsWith("7.")) dto.setSummary(content);
        }

        return dto;
    }

    private String removePrefixOnly(String section, String prefix) {
        String[] lines = section.substring(prefix.length()).trim()
                .split("\n");
        List<String> nonEmpty = Arrays.stream(lines)
                .filter(l -> !l.isBlank())
                .collect(Collectors.toList());

        if (nonEmpty.size() <= 1) return "";
        return String.join("\n", nonEmpty.subList(1, nonEmpty.size())).trim();
    }

    public static String removeMarkdownCodeBlock(String input) {
        if (input == null || input.isBlank()) return input;

        Pattern pattern = Pattern.compile(
                "^```(?:\\w+)?\\s*\\n([\\s\\S]*?)\\n```$",
                Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input.trim());

        return matcher.find() ? matcher.group(1).trim() : input.trim();
    }

    private String buildSystemInstruction(String word) {
        return """
        Bạn là từ điển Anh-Việt chuyên nghiệp. Hãy giải nghĩa từ "%s" theo đúng cấu trúc sau.
        QUAN TRỌNG: Mỗi mục PHẢI bắt đầu bằng số thứ tự đúng định dạng "1." "2." v.v., KHÔNG dùng "##", KHÔNG dùng "**1.**".

        1. PHÁT ÂM
        (IPA và phiên âm tiếng Việt)

        2. NGHĨA
        (Các nghĩa theo từ loại, kèm ví dụ)

        3. NGỮ PHÁP
        (Cách dùng trong câu)

        4. CỤM TỪ & THÀNH NGỮ
        (Các cụm từ thông dụng)

        5. TỪ ĐỒNG NGHĨA & TRÁI NGHĨA
        (Danh sách từ)

        6. MẸO & THÚ VỊ
        (Nguồn gốc, mẹo nhớ)

        7. TÓM TẮT
        (Tóm tắt ngắn gọn)

        Chỉ trả về nội dung, không thêm giải thích hay lời mở đầu.
        """.formatted(word);
    }
}
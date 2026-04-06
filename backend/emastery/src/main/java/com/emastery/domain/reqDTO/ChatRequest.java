package com.emastery.domain.reqDTO;

import com.emastery.domain.Enum.Level;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class ChatRequest {
    private Conversation conversation;
    private String username;
    private String gender;
    private int age;
    @Enumerated(EnumType.STRING)
    private Level englishLevel;
    private String geminiApiKey;
}
package com.emastery.domain.reqDTO;

import lombok.Data;

import java.util.List;

@Data
public class Conversation {
    private String question;
    private List<ChatMessage> chatHistory;
}
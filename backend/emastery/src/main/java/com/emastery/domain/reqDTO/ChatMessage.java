package com.emastery.domain.reqDTO;

import lombok.Data;

@Data
public class ChatMessage {
    private boolean fromUser;
    private String message;
}
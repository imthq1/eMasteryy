package com.emastery.domain.reqDTO;

import lombok.Data;

@Data
public class EvaluateGuessRequest {
    private String userGuess;
    private String correctPhrase;
    private String geminiApiKey;
}
package com.emastery.domain.resDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslateResponse {
    private String word;
    private WordExplanationDto explanation;
}
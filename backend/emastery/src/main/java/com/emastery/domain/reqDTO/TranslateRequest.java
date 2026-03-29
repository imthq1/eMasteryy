package com.emastery.domain.reqDTO;

import lombok.Data;

@Data
public class TranslateRequest {
    private String word;
    private String apiKey;
}
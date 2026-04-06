package com.emastery.domain.resDTO;

import lombok.Data;

@Data
public class EvaluateGuessResponse {
    private int accuracy;
    private String explanation;
}
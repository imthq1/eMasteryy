package com.emastery.domain.reqDTO;

import lombok.Data;
import java.util.List;

@Data
public class GenerateReadingRequest {
    private String englishLevel;
    private String geminiApiKey;
    private List<String> usedDescriptions;
}
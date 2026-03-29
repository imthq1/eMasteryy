package com.emastery.domain.resDTO;

import lombok.Data;

@Data
public class WordExplanationDto {
    private String pronunciation;
    private String meaning;
    private String grammarUsage;
    private String phrasesAndIdioms;
    private String synonymsAndAntonyms;
    private String funFactsAndTips;
    private String summary;
}

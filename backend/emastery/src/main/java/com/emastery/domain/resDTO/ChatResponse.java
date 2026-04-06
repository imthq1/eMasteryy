package com.emastery.domain.resDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    @JsonProperty("MessageInMarkdown")
    private String messageInMarkdown;

    @JsonProperty("Suggestions")
    private List<String> suggestions;
}
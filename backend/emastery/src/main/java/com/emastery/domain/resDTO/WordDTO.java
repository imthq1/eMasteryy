package com.emastery.domain.resDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WordDTO {
    private int id;
    private String word;
    private boolean learned;
}
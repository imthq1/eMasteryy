package com.emastery.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "words")
@Table
@Getter
@Setter
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String word;
    private boolean learned=false;
    @ManyToOne
    @JoinColumn(name = "collection_id")
    @JsonBackReference
    private Collection collection;
}
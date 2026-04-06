package com.emastery.controller;

import com.emastery.domain.reqDTO.CreateWordRequest;
import com.emastery.service.WordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping("/collection/{collectionId}")
    public ResponseEntity<?> addWord(
            @PathVariable int collectionId,
            @RequestBody CreateWordRequest request) {

        return ResponseEntity.ok(wordService.addWord(collectionId, request));
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<?> getWords(@PathVariable int collectionId) {
        return ResponseEntity.ok(wordService.getWordsByCollection(collectionId));
    }
    @PutMapping("/{id}/learned")
    public ResponseEntity<?> markAsLearned(@PathVariable int id) {
        return ResponseEntity.ok(wordService.markAsLearned(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWord(@PathVariable int id) {
        wordService.deleteWord(id);
        return ResponseEntity.ok("Deleted word successfully");
    }
}
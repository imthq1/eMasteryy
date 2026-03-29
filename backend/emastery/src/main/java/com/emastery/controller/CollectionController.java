package com.emastery.controller;

import com.emastery.domain.reqDTO.CreateCollectionRequest;
import com.emastery.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createCollection(
            @PathVariable int userId,
            @RequestBody CreateCollectionRequest request) {

        return ResponseEntity.ok(collectionService.createCollection(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCollections(@PathVariable int userId) {
        return ResponseEntity.ok(collectionService.getCollectionsByUser(userId));
    }
}

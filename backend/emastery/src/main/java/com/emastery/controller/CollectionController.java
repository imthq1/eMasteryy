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

    @PostMapping()
    public ResponseEntity<?> createCollection(
            @RequestBody CreateCollectionRequest request) {
        return ResponseEntity.ok(collectionService.createCollection(request));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCollections() {
        return ResponseEntity.ok(collectionService.getCollectionsByUser());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(@PathVariable int id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.ok("Deleted collection successfully");
    }
}

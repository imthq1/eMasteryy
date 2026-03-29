package com.emastery.service;

import com.emastery.domain.Collection;
import com.emastery.domain.Word;
import com.emastery.domain.reqDTO.CreateWordRequest;
import com.emastery.repository.CollectionRepository;
import com.emastery.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;
    private final CollectionRepository collectionRepository;

    public Word addWord(int collectionId, CreateWordRequest request) {
        Collection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new RuntimeException("Collection not found"));

        Word word = new Word();
        word.setWord(request.getWord());
        word.setCollection(collection);
        word.setLearned(false);

        return wordRepository.save(word);
    }

    public List<Word> getWordsByCollection(int collectionId) {
        return wordRepository.findByCollectionId(collectionId);
    }
}
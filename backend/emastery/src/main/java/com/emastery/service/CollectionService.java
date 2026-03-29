package com.emastery.service;

import com.emastery.domain.Collection;
import com.emastery.domain.User;
import com.emastery.domain.reqDTO.CreateCollectionRequest;
import com.emastery.repository.CollectionRepository;
import com.emastery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final CollectionRepository collectionRepository;
    private final UserRepository userRepository;

    public Collection createCollection(int userId, CreateCollectionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Collection collection = new Collection();
        collection.setName(request.getName());
        collection.setUser(user);

        return collectionRepository.save(collection);
    }

    public List<Collection> getCollectionsByUser(int userId) {
        return collectionRepository.findByUserId(userId);
    }
}
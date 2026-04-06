package com.emastery.service;

import com.emastery.config.SecurityUtil;
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

    public Collection createCollection(CreateCollectionRequest request) {
        String username= SecurityUtil.getCurrentUserLogin().get();
        User user = userRepository.findByUsername(username);
        Collection collection = new Collection();
        collection.setName(request.getName());
        collection.setUser(user);

        return collectionRepository.save(collection);
    }

    public List<Collection> getCollectionsByUser() {
        String username= SecurityUtil.getCurrentUserLogin().get();
        User user = userRepository.findByUsername(username);
        return collectionRepository.findByUserId(user.getId());
    }
    public void deleteCollection(int collectionId) {
        if (!collectionRepository.existsById(collectionId)) {
            throw new RuntimeException("Collection not found");
        }
        collectionRepository.deleteById(collectionId);
    }
}
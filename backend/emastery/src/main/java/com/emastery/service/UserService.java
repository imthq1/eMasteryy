package com.emastery.service;

import com.emastery.domain.User;
import com.emastery.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User getUserByEmail(String userName)
    {
        return this.userRepository.findByUsername(userName);
    }
}

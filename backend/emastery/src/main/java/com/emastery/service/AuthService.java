package com.emastery.service;

import com.emastery.config.SecurityUtil;
import com.emastery.domain.User;
import com.emastery.domain.reqDTO.LoginRequest;
import com.emastery.domain.resDTO.LoginResponse;
import com.emastery.domain.resDTO.ResLoginDTO;
import com.emastery.domain.resDTO.UserInfo;
import com.emastery.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final HealthcheckService healthcheckService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecurityUtil securityUtil;
    public AuthService(HealthcheckService healthcheckService, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, SecurityUtil securityUtil) {
        this.healthcheckService = healthcheckService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityUtil = securityUtil;
    }

    public LoginResponse register(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        // Validate
        if (request.getUsername() == null || request.getUsername().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()
                || request.getGender() == null || request.getGender().isEmpty()
                || request.getAge() <= 0) {

            response.setSuccess(false);
            response.setMessage("Vui lòng điền đầy đủ thông tin.");
            return response;
        }

        // Check username đã tồn tại chưa
        User existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser != null) {
            response.setSuccess(false);
            response.setMessage("Username đã tồn tại.");
            return response;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setLevel(request.getEnglishLevel());
        user.setFullname(request.getFullName());
        userRepository.save(user);

        response.setSuccess(true);
        response.setMessage("Đăng ký thành công!");

        return response;
    }
}
package com.emastery.controller;

import com.emastery.domain.reqDTO.ContactRequest;
import com.emastery.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody ContactRequest request) {
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập nội dung góp ý.");
        }

        try {
            emailService.sendFeedback(request.getName(), request.getMessage());
            return ResponseEntity.ok("Gửi góp ý thành công.");
        } catch (Exception ex) {
            log.error("Lỗi khi gửi email: ", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi email: " + ex.getMessage());
        }
    }
}
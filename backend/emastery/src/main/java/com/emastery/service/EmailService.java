package com.emastery.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${email.settings.sender-name}")
    private String senderName;

    @Value("${email.settings.receiver-email}")
    private String receiverEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendFeedback(String name, String messageContent) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail, senderName);
        helper.setTo(receiverEmail);
        helper.setSubject("Góp ý từ người dùng");

        String safeName = HtmlUtils.htmlEscape(name != null ? name : "Khách ẩn danh");
        String safeMessage = HtmlUtils.htmlEscape(messageContent).replace("\n", "<br />");

        String htmlBody = """
            <html>
                <body style='font-family: Arial, sans-serif; line-height: 1.6;'>
                    <h2 style='color: #2e6c80;'> Góp ý từ người dùng</h2>
                    <p><strong> Người gửi:</strong> %1$s</p>
                    <hr />
                    <p><strong> Nội dung góp ý:</strong></p>
                    <div style='padding: 10px; background-color: #f9f9f9; border: 1px solid #ddd; border-radius: 5px;'>
                        %2$s
                    </div>
                </body>
            </html>
            """.formatted(safeName, safeMessage);

        helper.setText(htmlBody, true);

        mailSender.send(message);
    }
}
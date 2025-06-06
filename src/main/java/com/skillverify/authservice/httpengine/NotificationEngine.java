package com.skillverify.authservice.httpengine;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificationEngine {

    private final RestClient restClient;

    public NotificationEngine(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://localhost:5001").build();
    }

    public ResponseEntity<String> makeCallToNotificationService(String toEmail, String subject, String message) {
        log.info("Invoked makeCallToNotificationService with toEmail: {}, subject: {}", toEmail, subject);

        Map<String, String> requestBody = Map.of(
                "to_email", toEmail,
                "subject", subject,
                "message", message
        );

        ResponseEntity<String> response = restClient
                .post()
                .uri("/send-email")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(String.class);

        return response;
    }
}

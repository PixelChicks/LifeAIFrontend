package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.service.PendingAiRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiQueueController {

    private final PendingAiRequestService pendingAiRequestService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean available = pendingAiRequestService.isAiAvailable();
        return ResponseEntity.ok(Map.of(
            "available", available,
            "pendingCount", pendingAiRequestService.getPendingCount()
        ));
    }

    @PostMapping("/queue")
    public ResponseEntity<Map<String, Object>> queue(
            @RequestParam String question,
            @RequestParam String email) {
        if (question.isBlank() || email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        PendingAiRequestService.PendingRequest req =
                pendingAiRequestService.queue(question, email);
        return ResponseEntity.ok(Map.of(
            "id", req.id,
            "position", pendingAiRequestService.getPendingCount()
        ));
    }

    // Remove before production
    @GetMapping("/test-process")
    public ResponseEntity<String> testProcess() {
        pendingAiRequestService.processPendingRequests();
        return ResponseEntity.ok("Processing triggered. Check your email and logs.");
    }

    // Inner DTO for the JSON body
    public static class QueueJsonRequest {
        public String question;
        public String email;
        public String imageBase64;
        public String imageType;
    }

    @PostMapping("/queue-json")
    public ResponseEntity<Map<String, Object>> queueJson(
            @RequestBody QueueJsonRequest body) {
        if (body.question == null || body.question.isBlank()
                || body.email == null || body.email.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        PendingAiRequestService.PendingRequest req =
                pendingAiRequestService.queue(
                        body.question, body.email,
                        body.imageBase64, body.imageType
                );
        return ResponseEntity.ok(Map.of(
                "id", req.id,
                "position", pendingAiRequestService.getPendingCount()
        ));
    }
}
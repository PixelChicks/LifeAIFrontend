package com.lifeAIFrontend.LifeAIFrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lifeAIFrontend.LifeAIFrontend.model.AnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class PendingAiRequestService {

    private final ChatService chatService;
    private final GeminiService geminiService;
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    @Value("${pending.requests.file:./pending-requests.json}")
    private String filePath;

    public PendingAiRequestService(ChatService chatService,
                                   GeminiService geminiService,
                                   JavaMailSender mailSender) {
        this.chatService = chatService;
        this.geminiService = geminiService;
        this.mailSender = mailSender;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public static class PendingRequest {
        public String id;
        public String question;
        public String email;
        public String status;
        public LocalDateTime createdAt;
        public String imageBase64;
        public String imageType;

        public PendingRequest() {}

        // 2-param constructor for text-only requests
        public PendingRequest(String question, String email) {
            this.id = String.valueOf(System.currentTimeMillis());
            this.question = question;
            this.email = email;
            this.status = "PENDING";
            this.createdAt = LocalDateTime.now();
            this.imageBase64 = null;
            this.imageType = null;
        }

        // 4-param constructor for image requests
        public PendingRequest(String question, String email,
                              String imageBase64, String imageType) {
            this.id = String.valueOf(System.currentTimeMillis());
            this.question = question;
            this.email = email;
            this.status = "PENDING";
            this.createdAt = LocalDateTime.now();
            this.imageBase64 = imageBase64;
            this.imageType = imageType;
        }
    }

    private List<PendingRequest> readAll() {
        try {
            File file = new File(filePath);
            if (!file.exists()) return new ArrayList<>();
            return objectMapper.readValue(file,
                    new TypeReference<List<PendingRequest>>() {});
        } catch (Exception e) {
            log.error("Failed to read pending requests file: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private void writeAll(List<PendingRequest> requests) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(filePath), requests);
        } catch (Exception e) {
            log.error("Failed to write pending requests file: {}", e.getMessage());
        }
    }

    public PendingRequest queue(String question, String email) {
        List<PendingRequest> all = readAll();
        PendingRequest req = new PendingRequest(question, email);
        all.add(req);
        writeAll(all);
        log.info("Queued request {} for {}", req.id, email);
        return req;
    }

    public long getPendingCount() {
        return readAll().stream()
                .filter(r -> "PENDING".equals(r.status))
                .count();
    }

    public boolean isAiAvailable() {
        try {
            String result = chatService.chat("test", null);
            return result != null && !result.isBlank();
        } catch (Exception e) {
            log.warn("AI health check failed: {}", e.getMessage());
            return false;
        }
    }

    @Scheduled(fixedDelay = 30_000)
    public void processPendingRequests() {
        if (!isAiAvailable()) {
            log.info("AI unavailable, skipping queue processing");
            return;
        }

        List<PendingRequest> all = readAll();
        List<PendingRequest> pending = all.stream()
                .filter(r -> "PENDING".equals(r.status))
                .toList();

        if (pending.isEmpty()) return;

        log.info("Processing {} pending AI requests", pending.size());

        for (PendingRequest req : pending) {
            try {
                String response;

                if (req.imageBase64 != null && !req.imageBase64.isBlank()) {
                    // Histology image — use GeminiService
                    MockMultipartFile mockFile = toMultipartFile(req.imageBase64, req.imageType);
                    AnalysisResult result = geminiService.analyzeImage(mockFile);
                    response = formatAnalysisResult(result);
                } else {
                    // Plain text question — use ChatService
                    response = chatService.chat(req.question, null);
                }

                req.status = "SENT";
                writeAll(all);
                sendResultEmail(req, response);

            } catch (Exception e) {
                log.error("Failed to process request {}: {}", req.id, e.getMessage());
                req.status = "FAILED";
                writeAll(all);
            }
        }
    }

    public PendingRequest queue(String question, String email,
                                String imageBase64, String imageType) {
        List<PendingRequest> all = readAll();
        PendingRequest req = new PendingRequest(question, email, imageBase64, imageType);
        all.add(req);
        writeAll(all);
        log.info("Queued request {} for {} (hasImage={})",
                req.id, email, imageBase64 != null);
        return req;
    }

    private MockMultipartFile toMultipartFile(String base64, String mimeType) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        String ext = mimeType != null && mimeType.contains("png") ? ".png" : ".jpg";
        return new MockMultipartFile("image", "histology" + ext, mimeType, bytes);
    }

    private String formatAnalysisResult(AnalysisResult result) {
        if (!result.isReadable()) {
            return "Документът не е четлив. Моля, направете по-ясна снимка и опитайте отново.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Открити термини в хистологията:\n\n");
        for (AnalysisResult.TermExplanation term : result.getTerms()) {
            sb.append("• ").append(term.getTerm()).append("\n");
            sb.append("  ").append(term.getExplanation()).append("\n\n");
        }
        return sb.toString();
    }

    private void sendResultEmail(PendingRequest req, String aiResponse) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(req.email);
        mail.setSubject("Вашият въпрос е отговорен от LifeAI");
        mail.setText(
            "Здравейте,\n\n" +
            "Получихте отговор на Вашия въпрос:\n\n" +
            "Въпрос: " + req.question + "\n\n" +
            "Отговор:\n" + aiResponse + "\n\n" +
            "С уважение,\nЕкипът на LifeAI"
        );
        mailSender.send(mail);
    }
}
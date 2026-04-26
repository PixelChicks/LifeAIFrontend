package com.lifeAIFrontend.LifeAIFrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lifeAIFrontend.LifeAIFrontend.model.AnalysisResult;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void sendResultEmail(PendingRequest req, String aiResponse) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(req.email);
        helper.setSubject("Вашият въпрос е отговорен от LifeAI");

        String safeQuestion = escapeHtml(req.question);
        String formattedResponse = formatResponse(aiResponse);

        String htmlContent = """
            <div style="font-family: Arial, sans-serif; background-color:#f4f4f4; padding:20px;">
                <div style="max-width:600px; margin:auto; background:white; border-radius:10px; padding:20px; box-shadow:0 4px 10px rgba(0,0,0,0.1);">
                    
                    <h2 style="color:#EE617A; text-align:center;">LifeAI</h2>
                    
                    <p>Здравейте,</p>
                    
                    <p>Получихте отговор на Вашия въпрос от приложението <b>LifeAI</b>:</p>
                    
                    <div style="background:#f9f9f9; padding:15px; border-left:4px solid #EE617A; margin:15px 0;">
                        <b>Въпрос:</b><br/>
                        %s
                    </div>
                    
                    <div style="background:#f5f5f5; padding:15px; border-left:4px solid #f9a0b0; margin:15px 0;">
                        <b>Отговор:</b>
                        %s
                    </div>
                    
                    <div style="text-align:center; margin-top:30px;">
                        <a href="https://lifeai.up.railway.app/" 
                           style="background:#EE617A; color:white; padding:12px 20px; text-decoration:none; border-radius:5px; display:inline-block;">
                            Прочетете повече
                        </a>
                    </div>
                    
                    <p style="margin-top:30px;">С уважение,<br/>Екипът на LifeAI</p>
                    
                </div>
            </div>
            """.formatted(safeQuestion, formattedResponse);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private String formatResponse(String aiResponse) {
        if (aiResponse == null || aiResponse.isEmpty()) return "";

        StringBuilder html = new StringBuilder();
        String[] lines = aiResponse.split("\n");
        boolean inList = false;

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            boolean isBullet = trimmed.matches("^[*\\-•]\\s+.*");

            if (isBullet) {
                if (!inList) {
                    html.append("<ul style='padding-left:20px; margin:8px 0;'>");
                    inList = true;
                }
                String content = trimmed.replaceFirst("^[*\\-•]\\s+", "");
                html.append("<li style='margin-bottom:8px; line-height:1.6;'>")
                        .append(applyInlineMarkdown(content))
                        .append("</li>");
            } else {
                if (inList) {
                    html.append("</ul>");
                    inList = false;
                }
                html.append("<p style='margin:6px 0; line-height:1.6;'>")
                        .append(applyInlineMarkdown(trimmed))
                        .append("</p>");
            }
        }

        if (inList) html.append("</ul>");
        return html.toString();
    }

    private String applyInlineMarkdown(String text) {
        String escaped = escapeHtml(text);
        // **bold**
        escaped = escaped.replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>");
        // *italic*
        escaped = escaped.replaceAll("(?<!\\*)\\*(?!\\*)(.+?)(?<!\\*)\\*(?!\\*)", "<em>$1</em>");
        return escaped;
    }
}
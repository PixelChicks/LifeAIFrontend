package com.lifeAIFrontend.LifeAIFrontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

@Service
public class DailyReminderService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    private String cachedReminder = null;
    private LocalDate cacheDate = null;

    public String getDailyReminder() {
        LocalDate today = LocalDate.now();

        // Върни кешираното ако е за днес
        if (cachedReminder != null && today.equals(cacheDate)) {
            return cachedReminder;
        }

        try {
            cachedReminder = generateReminder();
            cacheDate = today;
        } catch (Exception e) {
            cachedReminder = "Ти си силна. Вярвай в себе си и продължавай напред!";
        }

        return cachedReminder;
    }

    private String generateReminder() throws Exception {
        String prompt = """
                Генерирай едно кратко насърчително послание на БЪЛГАРСКИ ЕЗИК за жена, която се лекува от рак на гърдата.
                СТРИКТНИ ПРАВИЛА:
                - Максимум 75 символа
                - Топло, надеждно и окуражаващо
                - Без медицински термини
                - Само едно изречение
                - Без кавички
                - Върни САМО текста, нищо друго
                """;

        var part = new java.util.LinkedHashMap<String, Object>();
        part.put("text", prompt);

        var content = new java.util.LinkedHashMap<String, Object>();
        content.put("parts", java.util.List.of(part));

        var requestMap = new java.util.LinkedHashMap<String, Object>();
        requestMap.put("contents", java.util.List.of(content));

        var genConfig = new java.util.LinkedHashMap<String, Object>();
        genConfig.put("maxOutputTokens", 60);
        genConfig.put("temperature", 1.0); // По-висока температура = по-разнообразни отговори
        requestMap.put("generationConfig", genConfig);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode root = objectMapper.readTree(response.body());
        String text = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText().trim();

        // Осигури максимум 75 символа
        return text.length() > 75 ? text.substring(0, 72) + "..." : text;
    }
}
package com.lifeAIFrontend.LifeAIFrontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeAIFrontend.LifeAIFrontend.model.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final KnowledgeBaseLoader knowledgeBaseLoader;

    public ChatService(KnowledgeBaseLoader knowledgeBaseLoader) {
        this.knowledgeBaseLoader = knowledgeBaseLoader;
    }

    /**
     * Sends the user's message together with the full conversation history to Gemini.
     * Returns the assistant's reply as plain text.
     *
     * @param userMessage  the latest message from the user
     * @param history      prior messages (alternating user/model)
     */
    public String chat(String userMessage, List<ChatMessage> history) throws Exception {

        // Build contents array: system instruction is folded into first user turn
        List<Map<String, Object>> contents = new ArrayList<>();

        // Inject system context as the very first user message (Gemini doesn't have a
        // dedicated system role in the basic API, so we prepend it to the first user turn).
        boolean systemInjected = false;

        if (history != null) {
            for (int i = 0; i < history.size(); i++) {
                ChatMessage msg = history.get(i);
                String role = "user".equals(msg.getRole()) ? "user" : "model";

                String content = msg.getContent();
                if (!systemInjected && "user".equals(role)) {
                    content = buildSystemContext() + "\n\nПотребителски въпрос: " + content;
                    systemInjected = true;
                }

                contents.add(buildContent(role, content));
            }
        }

        // Add current user message
        String currentContent = userMessage;
        if (!systemInjected) {
            currentContent = buildSystemContext() + "\n\nПотребителски въпрос: " + userMessage;
        }
        contents.add(buildContent("user", currentContent));

        // Build request
        Map<String, Object> requestMap = new LinkedHashMap<>();
        requestMap.put("contents", contents);

        Map<String, Object> genConfig = new LinkedHashMap<>();
        genConfig.put("temperature", 0.4);
        genConfig.put("maxOutputTokens", 2048);
        requestMap.put("generationConfig", genConfig);

        String requestBody = objectMapper.writeValueAsString(requestMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API грешка: " + response.statusCode() + " - " + response.body());
        }

        return extractReply(response.body());
    }

    private String buildSystemContext() {
        return """
            Ти си медицински асистент, специализиран в рак на гърдата и онкология.
            Отговаряш САМО на БЪЛГАРСКИ ЕЗИК.
            Базираш отговорите си на предоставената по-долу БАЗА ДАННИ.
            Ако въпросът е извън обхвата на базата данни, кажи го честно и препоръчай консултация с лекар.
            Не давай конкретни медицински препоръки или диагнози — само разяснения и информация.
            Бъди топъл, разбираем и подкрепящ.
            Отговорите ти трябва да са кратки и ясни — максимум 150 думи. Избягвай излишни повторения и дълги изречения.
            
            БАЗА ДАННИ (КОНТЕКСТ):
            """ + knowledgeBaseLoader.getCombinedKnowledge();
    }

    private Map<String, Object> buildContent(String role, String text) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("role", role);

        Map<String, Object> part = new LinkedHashMap<>();
        part.put("text", text);

        content.put("parts", List.of(part));
        return content;
    }

    private String extractReply(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        return root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText("Съжалявам, не успях да генерирам отговор. Моля, опитайте отново.");
    }
}
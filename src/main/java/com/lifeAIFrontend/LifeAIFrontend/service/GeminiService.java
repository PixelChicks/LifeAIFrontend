package com.lifeAIFrontend.LifeAIFrontend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeAIFrontend.LifeAIFrontend.model.AnalysisResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AnalysisResult analyzeImage(MultipartFile imageFile) throws Exception {
        String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
        String mimeType = imageFile.getContentType() != null ? imageFile.getContentType() : "image/jpeg";

        String prompt = buildPrompt();
        String requestBody = buildRequestBody(base64Image, mimeType, prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API грешка: " + response.statusCode() + " - " + response.body());
        }

        return parseResponse(response.body());
    }

    private String buildPrompt() {
        return """
                Ти си медицински асистент, специализиран в хистологията на рак на гърдата.
                Твоята задача е да анализираш изображението на медицински документ и да обясниш термините в него.

                СТРИКТНИ ПРАВИЛА:
                1. Всички отговори ТРЯБВА да са на БЪЛГАРСКИ ЕЗИК.
                2. Използвай самите данни от приложената по-долу "БАЗА ДАННИ", за да обясниш термините.
                3. Не използвай външна информация. Ако терминът не е в базата данни, посочи, че няма информация за него в предоставените документи.
                4. Първо оцени дали текстът на изображението е четлив. Ако не е, върни съобщение, в което молиш потребителя да направи нова снимка.
                5. Ако е четлив, извади всеки медицински термин, който срещаш, и го обясни на нов ред.
                6. Фокусирай се върху контекста на рак на гърдата.

                БАЗА ДАННИ (КОНТЕКСТ):
                """ + MedicalKnowledgeBase.KNOWLEDGE_BASE + """


                ФОРМАТ НА ОТГОВОРА (само валиден JSON, без markdown, без допълнителен текст):
                {
                  "isReadable": boolean,
                  "terms": [
                    { "term": "Име на термин", "explanation": "Обяснение базирано на базата данни" }
                  ],
                  "unexplainedTermsCount": number
                }
                """;
    }

    private String buildRequestBody(String base64Image, String mimeType, String prompt) throws Exception {
        var requestMap = new java.util.LinkedHashMap<String, Object>();

        var part1 = new java.util.LinkedHashMap<String, Object>();
        var inlineData = new java.util.LinkedHashMap<String, Object>();
        inlineData.put("mimeType", mimeType);
        inlineData.put("data", base64Image);
        part1.put("inlineData", inlineData);

        var part2 = new java.util.LinkedHashMap<String, Object>();
        part2.put("text", prompt);

        var content = new java.util.LinkedHashMap<String, Object>();
        content.put("parts", List.of(part1, part2));

        var generationConfig = new java.util.LinkedHashMap<String, Object>();
        generationConfig.put("responseMimeType", "application/json");

        requestMap.put("contents", List.of(content));
        requestMap.put("generationConfig", generationConfig);

        return objectMapper.writeValueAsString(requestMap);
    }

    private AnalysisResult parseResponse(String responseBody) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);
        String text = root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();

        // Strip markdown code fences if present
        text = text.replaceAll("```json", "").replaceAll("```", "").trim();

        JsonNode data = objectMapper.readTree(text);

        boolean isReadable = data.path("isReadable").asBoolean(true);
        int unexplainedCount = data.path("unexplainedTermsCount").asInt(0);

        List<AnalysisResult.TermExplanation> terms = new ArrayList<>();
        JsonNode termsNode = data.path("terms");
        if (termsNode.isArray()) {
            for (JsonNode termNode : termsNode) {
                String term = termNode.path("term").asText();
                String explanation = termNode.path("explanation").asText();
                terms.add(new AnalysisResult.TermExplanation(term, explanation));
            }
        }

        return new AnalysisResult(isReadable, terms, unexplainedCount);
    }
}

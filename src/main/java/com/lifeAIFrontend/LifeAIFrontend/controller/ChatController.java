package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ChatClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@AllArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping("/chat")
    public String showChatForm() {
        return "chat";
    }

    @GetMapping("/researchSideEffects")
    public String researchSideEffects(Model model) {
        model.addAttribute("response", "Здравейте! Аз съм LifeAI, вашият персонализиран асистент. " +
                "Моля, напишете какви странични ефекти изпитвате, за да мога да ви предоставя полезна информация и решения " +
                "за облекчаване на симптомите.");
        return "recommendedStudies/researchSideEffects";
    }

    @PostMapping("/chat")
    public String sendChatMessage(@RequestParam("message") String message, Model model,
                                  @RequestParam(value = "file", required = false) MultipartFile file) {

        ResponseEntity<String> response = chatClient.chat(message, file);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));
        model.addAttribute("response", formattedResponse);
        model.addAttribute("message", message);

        return "chat";
    }

    @PostMapping("/researchSideEffects")
    public ResponseEntity<Map<String, String>> researchSideEffects(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        // Call the service or chatClient to get the response
        ResponseEntity<String> response = chatClient.researchSideEffects(message);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));

        // Create response map to send back to the frontend
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", formattedResponse);

        // Return the response as JSON
        return ResponseEntity.ok(responseMap);
    }

    @PostMapping("/understandingDiagnosis")
    public String understandingDiagnosis(@RequestParam String message, Model model,
                                         @RequestParam(value = "file", required = false) MultipartFile file) {
        if (message.isEmpty()) {
            message = "Моля да опишеш на разбираем език, без медицинските термини какво е заболяването описано във файла. " +
                    "Обясни значението на всеки термин.";
        }

        ResponseEntity<String> response = chatClient.chat(message, file);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));
        model.addAttribute("response", formattedResponse);
        model.addAttribute("message", message);

        return "menu/understandingDiagnosis";
    }

    @PostMapping("/explainTerms")
    @ResponseBody
    public Map<String, String> explainTerms(
            @RequestBody Map<String, String> payload) {

        String message = payload.get("message");
        String conversationContext = payload.get("conversationContext");

        if (message == null || message.isEmpty()) {
            message = "Каква е целта ти?";
        }

        if (conversationContext == null) {
            conversationContext = "";
        }

        // Append message to conversation context
        String updatedContext = conversationContext + "\nUser: " + message;

        // Send request to AI chatbot
        ResponseEntity<String> response = chatClient.chat(updatedContext, null);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));

        updatedContext += "\nAssistant: " + formattedResponse;

        // Prepare JSON response
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("response", formattedResponse);
        responseBody.put("conversationContext", updatedContext);

        return responseBody;
    }


    @PostMapping("/explainTermsChat")
    @ResponseBody
    public Map<String, String> explainTermsChat(@RequestBody Map<String, String> payload) {

        String message = payload.get("message");
        String conversationContext = payload.get("conversationContext");

        // Ensure conversationContext is initialized
        if (conversationContext == null) {
            conversationContext = "";
        }

        // Append user message
        String updatedContext = conversationContext + "\nUser: " + message;
        System.out.println(conversationContext);

        // Call AI service
        ResponseEntity<String> response = chatClient.chat(updatedContext, null);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));

        // Append AI response to context
        updatedContext += "\nAssistant: " + formattedResponse;

        // Prepare JSON response
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("response", formattedResponse);
        responseBody.put("conversationContext", updatedContext);

        return responseBody;
    }


    private String formatResponse(String response) {
        // Replace newline characters with <br> for HTML line breaks
        String formatted = response.replace("\n", "<br>");

        // Make text between ** and ** bold by replacing **text** with <b>text</b>
        formatted = formatted.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");

        // Add a new line before numbered items like 1., 2., etc.
        formatted = formatted.replaceAll("(?<=<br>|^)(\\d+\\.)", "<br>$1");

        return formatted;
    }
}


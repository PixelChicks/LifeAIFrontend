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
    public String explainTerms(
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "popupTextContent", required = false) String popupTextContent,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Model model) {

        if (!(message.isEmpty())) {
            model.addAttribute("plainMessage", message);
        } else {
            message = "Каква е целта ти?";
        }

        if (popupTextContent != null && !(popupTextContent.contains("Здравейте! Аз съм LifeAI"))) {
            message = message + " " + popupTextContent;
            model.addAttribute("popupText", popupTextContent);
        }

        ResponseEntity<String> response = chatClient.chat(message, file);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));

        model.addAttribute("response", formattedResponse);
        model.addAttribute("message", message);

        return "recommendedStudies/chat";
    }

    @PostMapping("/explainTermsChat")
    @ResponseBody
    public Map<String, String> explainTermsChat(
            @RequestBody Map<String, String> payload) {

        String message = payload.get("message");
        String conversationContext = payload.get("conversationContext");

        String updatedContext = (conversationContext == null ? "" : conversationContext) +
                "\nUser: " + message;

        ResponseEntity<String> response = chatClient.chat(updatedContext, null);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));

        updatedContext += "\nAssistant: " + formattedResponse;

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


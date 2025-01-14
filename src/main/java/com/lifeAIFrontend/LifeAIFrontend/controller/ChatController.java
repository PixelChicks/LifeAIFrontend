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

    @PostMapping("/chat")
    public String sendChatMessage(@RequestParam("message") String message, Model model,
                                  @RequestParam(value = "file", required = false) MultipartFile file) {

        ResponseEntity<String> response = chatClient.chat(message, file);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));
        model.addAttribute("response", formattedResponse);
        model.addAttribute("message", message);

        return "chat";
    }

    @PostMapping("/understandingDiagnosis")
    public String understandingDiagnosis(
            @RequestParam String message,
            Model model,
            @RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
            @RequestParam(value = "cameraFileUpload", required = false) MultipartFile cameraFileUpload) {

        // Select the correct file input
        MultipartFile file = fileUpload != null ? fileUpload : cameraFileUpload;

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


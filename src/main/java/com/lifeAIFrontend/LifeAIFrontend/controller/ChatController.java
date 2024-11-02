package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ChatClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
    public String understandingDiagnosis(@RequestParam String message, Model model,
                                         @RequestParam(value = "file", required = false) MultipartFile file) {
        if(message.isEmpty()){
            message = "Моля да опишеш на разбираем език, без медицинските термини какво е заболяването описано във файла. " +
                    "Обясни значението на всеки термин.";
        }

        ResponseEntity<String> response = chatClient.chat(message, file);
        String formattedResponse = formatResponse(Objects.requireNonNull(response.getBody()));
        model.addAttribute("response", formattedResponse);
        model.addAttribute("message", message);

        return "menu/understandingDiagnosis";
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


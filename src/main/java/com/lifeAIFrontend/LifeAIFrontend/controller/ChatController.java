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
        model.addAttribute("response", response.getBody());
        model.addAttribute("message", message);

        return "chat";
    }

    @PostMapping("/understandingDiagnosis")
    public String understandingDiagnosis(@RequestParam("message") String message, Model model,
                                         @RequestParam(value = "file", required = false) MultipartFile file) {

        ResponseEntity<String> response = chatClient.chat(message, file);
        model.addAttribute("response", response.getBody());
        model.addAttribute("message", message);

        return "menu/understandingDiagnosis";
    }
}


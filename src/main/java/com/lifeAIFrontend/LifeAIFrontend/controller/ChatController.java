package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ChatClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @GetMapping("/chat")
    public String showChatForm() {
        return "chat";
    }

    @PostMapping("/chat")
    public String sendChatMessage(@RequestParam("message") String message, Model model) {
        Map<String, String> request = new HashMap<>();
        request.put("message", message);

        ResponseEntity<String> response = chatClient.chat(request);
        model.addAttribute("response", response.getBody());
        model.addAttribute("message", message);

        return "chat";
    }
}


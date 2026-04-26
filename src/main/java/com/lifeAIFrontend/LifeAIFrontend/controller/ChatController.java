package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.model.ChatMessage;
import com.lifeAIFrontend.LifeAIFrontend.model.ChatRequest;
import com.lifeAIFrontend.LifeAIFrontend.model.ChatResponse;
import com.lifeAIFrontend.LifeAIFrontend.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Render the chat page
     */
    @GetMapping
    public String chatPage() {
        return "chat";
    }

    /**
     * REST endpoint — accepts JSON with the user message + full history,
     * returns the assistant reply as JSON.
     * <p>
     * Example request body:
     * {
     * "message": "Какво е HER2?",
     * "history": [
     * { "role": "user",      "content": "Здравейте!" },
     * { "role": "assistant", "content": "Здравейте! Как мога да помогна?" }
     * ]
     * }
     */
    @PostMapping("/message")
    @ResponseBody
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest chatRequest) {
        if (chatRequest.getMessage() == null || chatRequest.getMessage().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new ChatResponse(false, "Съобщението не може да бъде празно."));
        }

        try {
            List<ChatMessage> history = chatRequest.getHistory();
            String reply = chatService.chat(chatRequest.getMessage(), history);
            return ResponseEntity.ok(new ChatResponse(reply));
        } catch (Exception e) {
            ChatResponse unavailable = new ChatResponse(false, "AI_UNAVAILABLE");
            return ResponseEntity.status(503).body(unavailable);
        }
    }
}
package com.lifeAIFrontend.LifeAIFrontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AiUnavailableController {

    @GetMapping("/ai-unavailable")
    public String showPage() {
        return "ai-unavailable";
    }
}
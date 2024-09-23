package com.lifeAIFrontend.LifeAIFrontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    @GetMapping("/home")
    public String home() {
        return "menu/home";
    }

    @GetMapping("/info")
    public String info() {
        return "menu/info";
    }
}

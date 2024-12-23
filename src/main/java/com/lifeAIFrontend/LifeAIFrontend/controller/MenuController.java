package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.model.Answer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @GetMapping("/limfedem")
    public String limfedem() {
        return "menu/limfedem";
    }

    @GetMapping("/alternativeMedicine")
    public String alternativeMedicine() {
        return "menu/alternativeMedicine";
    }

    @GetMapping("/diagnosisUploadFile")
    public String understandingDiagnosisUploadFile() {
        return "menu/understandingDiagnosisUploadFile";
    }
}

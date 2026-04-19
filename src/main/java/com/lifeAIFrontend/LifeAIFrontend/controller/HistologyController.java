package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.model.AnalysisResult;
import com.lifeAIFrontend.LifeAIFrontend.service.GeminiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Controller
public class HistologyController {

    private final GeminiService geminiService;

    public HistologyController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @GetMapping("/diagnosisUploadFile")
    public String index() {
        return "diagnosisUploadFile";
    }

    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("image") MultipartFile imageFile,
            Model model) {

        if (imageFile.isEmpty()) {
            model.addAttribute("error", "Моля, изберете изображение.");
            return "index";
        }

        try {
            // Pass image preview back so user can see what they uploaded
            String base64Preview = "data:" + imageFile.getContentType() + ";base64,"
                    + Base64.getEncoder().encodeToString(imageFile.getBytes());
            model.addAttribute("imagePreview", base64Preview);

            AnalysisResult result = geminiService.analyzeImage(imageFile);
            model.addAttribute("result", result);

            return "results";

        } catch (Exception e) {
            model.addAttribute("error", "Възникна грешка при анализа: " + e.getMessage());
            return "index";
        }
    }
}

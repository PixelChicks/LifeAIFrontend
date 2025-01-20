package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.AnswerClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Answer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.CompletableFuture;

@Controller
@AllArgsConstructor
public class AnswerController {

    private final AnswerClient answerClient;

    @PostMapping("/{id}")
    public String getAnswerById(@PathVariable Long id, Model model) {
        Answer answer = answerClient.getAnswerById(id);
        model.addAttribute("answer", answer);
        return "recommendedStudies/questions";
    }

    @GetMapping("/recommendedStudies")
    public String showQuestions(Model model) {
        model.addAttribute("answer", new Answer());
        model.addAttribute("answers", answerClient.getAllAnswers());
        // Fetch answers in parallel using CompletableFuture
        CompletableFuture<String> coreNeedleBiopsyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(17L).getText());
        CompletableFuture<String> clinicalExaminationFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(18L).getText());
        CompletableFuture<String> imagingStudiesFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(19L).getText());
        CompletableFuture<String> echomammographyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(20L).getText());
        CompletableFuture<String> ultrasoundFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(21L).getText());
        CompletableFuture<String> mammographyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(22L).getText());
        CompletableFuture<String> nuclearMagneticResonanceFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(23L).getText());
        CompletableFuture<String> histologicalExaminationFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(24L).getText());
        CompletableFuture<String> petScannerFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(25L).getText());
        CompletableFuture<String> contrastScannerFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(26L).getText());
        CompletableFuture<String> abdominalUltrasoundFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(27L).getText());
        CompletableFuture<String> lungXrayFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(28L).getText());
        CompletableFuture<String> boneScintigraphyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(29L).getText());
        CompletableFuture<String> tumorMarkerFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(30L).getText());
        CompletableFuture<String> brca12Future = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(31L).getText());
        CompletableFuture<String> chemotherapyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(32L).getText());
        CompletableFuture<String> targetedTherapyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(33L).getText());
        CompletableFuture<String> hormonalTherapyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(34L).getText());
        CompletableFuture<String> immuneTherapyFuture = CompletableFuture.supplyAsync(() -> answerClient.getAnswerById(35L).getText());

        // Wait for all futures to complete and get their results
        model.addAttribute("coreNeedleBiopsy", coreNeedleBiopsyFuture.join());
        model.addAttribute("clinicalExamination", clinicalExaminationFuture.join());
        model.addAttribute("imagingStudies", imagingStudiesFuture.join());
        model.addAttribute("echomammography", echomammographyFuture.join());
        model.addAttribute("ultrasound", ultrasoundFuture.join());
        model.addAttribute("mammography", mammographyFuture.join());
        model.addAttribute("nuclearMagneticResonance", nuclearMagneticResonanceFuture.join());
        model.addAttribute("histologicalExaminationOfBiopsyMaterial", histologicalExaminationFuture.join());
        model.addAttribute("PETScanner", petScannerFuture.join());
        model.addAttribute("contrastScanner", contrastScannerFuture.join());
        model.addAttribute("abdominalUltrasound", abdominalUltrasoundFuture.join());
        model.addAttribute("lungXray", lungXrayFuture.join());
        model.addAttribute("boneScintigraphy", boneScintigraphyFuture.join());
        model.addAttribute("tumorMarker", tumorMarkerFuture.join());
        model.addAttribute("BRCA12", brca12Future.join());
        model.addAttribute("chemotherapy", chemotherapyFuture.join());
        model.addAttribute("targetedTherapy", targetedTherapyFuture.join());
        model.addAttribute("hormonalTherapy", hormonalTherapyFuture.join());
        model.addAttribute("immuneTherapy", immuneTherapyFuture.join());

        return "recommendedStudies/questions";
    }
}

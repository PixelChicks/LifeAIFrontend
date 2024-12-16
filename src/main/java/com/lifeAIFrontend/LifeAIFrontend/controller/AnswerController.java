package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.AnswerClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Answer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        model.addAttribute("coreNeedleBiopsy", answerClient.getAnswerById(17L).getText());
        model.addAttribute("clinicalExamination", answerClient.getAnswerById(18L).getText());
        model.addAttribute("imagingStudies", answerClient.getAnswerById(19L).getText());
        model.addAttribute("echomammography", answerClient.getAnswerById(20L).getText());
        model.addAttribute("ultrasound", answerClient.getAnswerById(21L).getText());
        model.addAttribute("mammography", answerClient.getAnswerById(22L).getText());
        model.addAttribute("nuclearMagneticResonance", answerClient.getAnswerById(23L).getText());
        model.addAttribute("histologicalExaminationOfBiopsyMaterial", answerClient.getAnswerById(24L).getText());
        model.addAttribute("PETScanner", answerClient.getAnswerById(25L).getText());
        model.addAttribute("contrastScanner", answerClient.getAnswerById(26L).getText());
        model.addAttribute("abdominalUltrasound", answerClient.getAnswerById(27L).getText());
        model.addAttribute("lungX-ray", answerClient.getAnswerById(28L).getText());
        model.addAttribute("boneScintigraphy", answerClient.getAnswerById(29L).getText());
        model.addAttribute("tumorMarker", answerClient.getAnswerById(30L).getText());
        model.addAttribute("BRCA1/2", answerClient.getAnswerById(31L).getText());
        model.addAttribute("chemotherapy", answerClient.getAnswerById(32L).getText());
        model.addAttribute("targetedTherapy", answerClient.getAnswerById(32L).getText());
        model.addAttribute("chemotherapy", answerClient.getAnswerById(32L).getText());

        return "recommendedStudies/questions";
    }
}

package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.AnswerClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Answer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class AnswerController {

    private final AnswerClient answerClient;

    @PostMapping("/{id}")
    public String getAnswerById(@PathVariable Long id, Model model) {
        Answer answer = answerClient.getAnswerById(id); // Get the answer from the client
        model.addAttribute("answer", answer); // Add the answer to the model to be used in the view
        return "recommendedStudies/questions"; // Return the same view to show the answer
    }
}

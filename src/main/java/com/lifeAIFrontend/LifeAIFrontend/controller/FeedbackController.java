package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.FeedbackClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@AllArgsConstructor
public class FeedbackController {

    private final FeedbackClient feedbackClient;

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, RedirectAttributes redirectAttributes) {
        feedbackClient.createFeedback(feedback); // Send the feedback to the backend
        redirectAttributes.addFlashAttribute("successMessage", "Благодарим Ви! Обратната връзка е успешно изпратена!");
        return "redirect:/home";
    }
}
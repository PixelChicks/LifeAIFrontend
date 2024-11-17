package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.FeedbackClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class FeedbackController {

    private final FeedbackClient feedbackClient;

    @PostMapping("/feedback")
    public String submitFeedback(@ModelAttribute Feedback feedback, Model model) {
        feedbackClient.createFeedback(feedback); // Send the feedback to the backend
        model.addAttribute("successMessage", "Thank you! Your feedback has been sent.");
        return "redirect:/"; // Return to the same page where the feedback was submitted
    }
}
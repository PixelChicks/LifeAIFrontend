package com.lifeAIFrontend.LifeAIFrontend.config;

import com.lifeAIFrontend.LifeAIFrontend.model.Feedback;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUrl")
    public String populateCurrentUrl(HttpServletRequest request) {
        return request.getRequestURI(); // Get the current URL
    }

    @ModelAttribute("feedback")
    public Feedback feedback() {
        return new Feedback();
    }
}
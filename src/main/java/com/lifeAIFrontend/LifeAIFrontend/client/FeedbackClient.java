package com.lifeAIFrontend.LifeAIFrontend.client;


import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.Feedback;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "life-ai", url = "${backend.base-url}/feedbacks", configuration = FeignClientConfiguration.class)
public interface FeedbackClient {
    @GetMapping("/all")
    List<Feedback> getAllFeedbacks();

    @GetMapping("/{id}")
    Feedback getFeedbackById(@PathVariable(name = "id") Long id);

    @PostMapping("/create")
    Feedback createFeedback(@Valid @RequestBody Feedback feedbackDTO);
}
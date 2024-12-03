package com.lifeAIFrontend.LifeAIFrontend.client;

import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.Answer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "life-ai-answer", url = "${backend.base-url}/answers", configuration = FeignClientConfiguration.class)
public interface AnswerClient {
    @GetMapping("/all")
    List<Answer> getAllAnswers();

    @GetMapping("/{id}")
    Answer getAnswerById(@PathVariable(name = "id") Long id);
}

package com.lifeAIFrontend.LifeAIFrontend.client;

import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.MoodEntry;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.MoodRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "mood-client",
    url = "${backend.base-url}/mood",
    configuration = FeignClientConfiguration.class
)
public interface MoodClient {

    @PostMapping
    MoodEntry saveMood(
        @RequestHeader("Authorization") String token,
        @RequestBody MoodRequest request
    );

    @GetMapping("/today")
    MoodEntry getTodaysMood(@RequestHeader("Authorization") String token);
}
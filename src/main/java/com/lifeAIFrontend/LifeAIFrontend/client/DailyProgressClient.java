package com.lifeAIFrontend.LifeAIFrontend.client;

import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.DailyProgress;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.ChallengeCompleteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "daily-progress-client",
    url = "${backend.base-url}/progress",
    configuration = FeignClientConfiguration.class
)
public interface DailyProgressClient {

    @PostMapping("/complete")
    DailyProgress completeChallenge(
        @RequestHeader("Authorization") String token,
        @RequestBody ChallengeCompleteRequest request
    );

    @GetMapping("/today")
    DailyProgress getTodaysProgress(@RequestHeader("Authorization") String token);
}
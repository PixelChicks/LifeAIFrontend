package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.DailyProgressClient;
import com.lifeAIFrontend.LifeAIFrontend.model.DailyProgress;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.ChallengeCompleteRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class DailyProgressController {

    private final DailyProgressClient dailyProgressClient;

    @PostMapping("/complete")
    @ResponseBody
    public ResponseEntity<DailyProgress> completeChallenge(
            @RequestBody ChallengeCompleteRequest request,
            HttpSession session
    ) {
        try {
            String token = (String) session.getAttribute("ACCESS_TOKEN");
            if (token == null) {
                return ResponseEntity.status(401).build();
            }

            DailyProgress progress = dailyProgressClient.completeChallenge(
                "Bearer " + token, 
                request
            );
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/today")
    @ResponseBody
    public ResponseEntity<DailyProgress> getTodaysProgress(HttpSession session) {
        try {
            String token = (String) session.getAttribute("ACCESS_TOKEN");
            if (token == null) {
                return ResponseEntity.status(401).build();
            }

            DailyProgress progress = dailyProgressClient.getTodaysProgress("Bearer " + token);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
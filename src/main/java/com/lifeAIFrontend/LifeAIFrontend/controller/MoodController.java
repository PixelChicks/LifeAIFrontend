package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.MoodClient;
import com.lifeAIFrontend.LifeAIFrontend.client.DailyProgressClient;
import com.lifeAIFrontend.LifeAIFrontend.model.MoodEntry;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.ChallengeCompleteRequest;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.MoodRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/mood")
@RequiredArgsConstructor
public class MoodController {

    private final MoodClient moodClient;
    private final DailyProgressClient dailyProgressClient;

    @PostMapping
    @ResponseBody
    public ResponseEntity<MoodEntry> saveMood(
            @RequestBody MoodRequest request,
            HttpSession session
    ) {
        try {
            String token = (String) session.getAttribute("ACCESS_TOKEN");
            if (token == null) {
                return ResponseEntity.status(401).build();
            }

            MoodEntry savedMood = moodClient.saveMood("Bearer " + token, request);

            // Mark challenge 1 as complete
            try {
                dailyProgressClient.completeChallenge(
                        "Bearer " + token,
                        new ChallengeCompleteRequest(1)
                );
            } catch (Exception e) {
                // Log but don't fail the mood save
                System.err.println("Failed to mark challenge complete: " + e.getMessage());
            }

            return ResponseEntity.ok(savedMood);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/today")
    @ResponseBody
    public ResponseEntity<MoodEntry> getTodaysMood(HttpSession session) {
        try {
            String token = (String) session.getAttribute("ACCESS_TOKEN");
            if (token == null) {
                return ResponseEntity.status(401).build();
            }

            MoodEntry mood = moodClient.getTodaysMood("Bearer " + token);
            return ResponseEntity.ok(mood);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
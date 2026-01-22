package com.lifeAIFrontend.LifeAIFrontend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCompleteRequest {
    private Integer challengeNumber; // 1-5
}
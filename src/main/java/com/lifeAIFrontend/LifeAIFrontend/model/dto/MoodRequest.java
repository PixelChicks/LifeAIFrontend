package com.lifeAIFrontend.LifeAIFrontend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoodRequest {
    private Integer moodLevel; // 1-5
}
package com.lifeAIFrontend.LifeAIFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoodEntry {
    private Long id;
    private Long userId;
    private Integer moodLevel; // 1-5
    private LocalDate date;
    private LocalDateTime createdAt;
}
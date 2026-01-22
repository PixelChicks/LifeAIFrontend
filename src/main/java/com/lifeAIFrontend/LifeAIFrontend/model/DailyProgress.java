package com.lifeAIFrontend.LifeAIFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyProgress {
    private Long id;
    private Long userId;
    private LocalDate date;
    private Set<Integer> completedChallenges = new HashSet<>(); // 1,2,3,4,5
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
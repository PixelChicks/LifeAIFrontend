package com.lifeAIFrontend.LifeAIFrontend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    private Long id;
    private String comment;
    private boolean thumbsUp;
    private String url;
    private LocalDateTime createdAt;
}
package com.lifeAIFrontend.LifeAIFrontend.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private Long id;
    private String title;
    private String description;
    private String thumbnailPicture;
    private Integer categoryId;
    private String visibility;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String subCategory;
    private Integer calories;
    private Integer hours;
    private Integer minutes;
}
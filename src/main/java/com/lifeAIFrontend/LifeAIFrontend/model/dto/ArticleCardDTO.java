package com.lifeAIFrontend.LifeAIFrontend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ArticleCardDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailPicture;
    private String subCategory;
    private Integer calories;
    private Integer hours;
    private Integer minutes;
}
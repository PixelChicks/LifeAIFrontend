package com.lifeAIFrontend.LifeAIFrontend.client;

import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.Article;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "life-ai-articles", url = "${backend.base-url}/articles", configuration = FeignClientConfiguration.class)
public interface ArticleClient {

    @GetMapping
    Page<Article> getAllArticles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    );

    @GetMapping("/subcategory/{subCategory}")
    Page<Article> getArticlesBySubCategory(
            @PathVariable(name = "subCategory") String subCategory,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    );
}
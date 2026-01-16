package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ArticleClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Article;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/articles")
@AllArgsConstructor
public class ArticleController {

    private final ArticleClient articleClient;

    @GetMapping
    public String listAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Article> articlePage = articleClient.getAllArticles(page, size);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        
        return "articles/list";
    }


    @GetMapping("/category/{subCategory}")
    public String listByCategory(
            @PathVariable String subCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        Page<Article> articlePage = articleClient.getArticlesBySubCategory(subCategory, page, size);
        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("subCategory", subCategory);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        
        return "articles/category_view";
    }
}
package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.ArticleClient;
import com.lifeAIFrontend.LifeAIFrontend.model.Article;
import com.lifeAIFrontend.LifeAIFrontend.model.dto.ArticleCardDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleClient articleClient;

    @GetMapping("/articles")
    public String showArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            Model model, HttpSession session) {

        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        Page<ArticleCardDTO> articlePage;

        if (category != null && !category.equals("all")) {
            articlePage = articleClient.getArticlesBySubCategory(category, page, size);
        } else {
            articlePage = articleClient.getAllArticles(page, size);
        }

        model.addAttribute("articles", articlePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", articlePage.getTotalPages());
        model.addAttribute("currentCategory", category != null ? category : "all");

        return "articles/list";
    }

    @GetMapping("/articles/{id}")
    public String showArticleDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("ACCESS_TOKEN") == null) {
            return "redirect:/login";
        }

        Article article = articleClient.getArticleById(id);
        model.addAttribute("article", article);
        return "articles/article-detail";
    }
}
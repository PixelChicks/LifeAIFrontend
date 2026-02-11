package com.lifeAIFrontend.LifeAIFrontend.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final HttpServletRequest request;

    // 🔐 Attach JWT automatically to every Feign request
    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return template -> {
            Object token = request.getSession()
                    .getAttribute("ACCESS_TOKEN");

            if (token != null) {
                template.header("Authorization", "Bearer " + token);
            }
        };
    }

    // ❌➡️✅ Custom error handling (you already had this)
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomFeignErrorDecoder();
    }
}

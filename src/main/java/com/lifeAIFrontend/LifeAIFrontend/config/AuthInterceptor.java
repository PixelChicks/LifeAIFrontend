package com.lifeAIFrontend.LifeAIFrontend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String token = (String) request.getSession()
                .getAttribute("ACCESS_TOKEN");

        if (token == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }
}

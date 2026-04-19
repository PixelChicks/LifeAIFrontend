package com.lifeAIFrontend.LifeAIFrontend.controller;

import com.lifeAIFrontend.LifeAIFrontend.client.AuthClient;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.AuthenticationRequest;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.AuthenticationResponse;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.RegisterRequest;
import com.lifeAIFrontend.LifeAIFrontend.session.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class AuthFrontendController {

    private final AuthClient authClient;
    private final SessionManager sessionManager;

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            AuthenticationRequest authRequest = new AuthenticationRequest(username, password);
            AuthenticationResponse response = authClient.authenticate(authRequest);

            session.setAttribute("ACCESS_TOKEN", response.getAccessToken());
            session.setAttribute("REFRESH_TOKEN", response.getRefreshToken());
            session.setAttribute("AUTH_USER", response.getUser());

            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute RegisterRequest registerRequest, Model model) {
        try {
            authClient.register(registerRequest);
            return "redirect:/auth/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}

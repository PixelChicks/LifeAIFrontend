package com.lifeAIFrontend.LifeAIFrontend.client;


import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.AuthenticationRequest;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.AuthenticationResponse;
import com.lifeAIFrontend.LifeAIFrontend.model.auth.RegisterRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "life-article-authClient", url = "${backend.base-url}/auth", configuration = FeignClientConfiguration.class)
public interface AuthClient {

    @PostMapping("/register")
    AuthenticationResponse register(@RequestBody RegisterRequest request);

    @PostMapping("/authenticate")
    AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request);
}


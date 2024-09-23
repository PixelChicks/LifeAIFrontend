package com.lifeAIFrontend.LifeAIFrontend.client;


import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "life-ai", url = "${backend.base-url}/openai", configuration = FeignClientConfiguration.class)
public interface ChatClient {

    @PostMapping("/chat")
    ResponseEntity<String> chat(@RequestBody Map<String, String> request);
}

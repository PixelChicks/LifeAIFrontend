package com.lifeAIFrontend.LifeAIFrontend.client;


import com.lifeAIFrontend.LifeAIFrontend.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "life-ai", url = "${backend.base-url}/openai", configuration = FeignClientConfiguration.class)
public interface ChatClient {

    @PostMapping(value = "/chat", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> chat(@RequestPart("message") String userMessage,
                                @RequestPart(value = "file", required = false) MultipartFile file);

    @PostMapping("/researchSideEffects")
    ResponseEntity<String> researchSideEffects(@RequestParam("message") String userMessage);
}

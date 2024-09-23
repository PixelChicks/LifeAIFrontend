package com.lifeAIFrontend.LifeAIFrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class LifeAiFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeAiFrontendApplication.class, args);
    }

}

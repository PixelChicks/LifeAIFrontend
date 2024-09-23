package com.lifeAIFrontend.LifeAIFrontend.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lifeAIFrontend.LifeAIFrontend.exceptions.InternalServerErrorException;
import com.lifeAIFrontend.LifeAIFrontend.exceptions.UnauthorizedException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;

public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = "Unknown error";

        try (InputStream bodyIs = response.body().asInputStream()) {
            JsonNode errorNode = objectMapper.readTree(bodyIs);
            errorMessage = errorNode.path("message").asText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (response.status()) {
            case 400:
                return new BadRequestException(errorMessage);
            case 401:
                return new UnauthorizedException(errorMessage);
            case 403:
                return new AccessDeniedException(errorMessage);
            case 404:
                return new NoSuchFieldException(errorMessage);
            case 500:
                return new InternalServerErrorException(errorMessage);
            default:
                return new ResponseStatusException(HttpStatus.valueOf(response.status()), errorMessage);
        }
    }
}


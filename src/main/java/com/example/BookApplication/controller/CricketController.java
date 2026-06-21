package com.example.BookApplication.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
public class CricketController {

    @Value("${groq.api.key}")
    private String apiKey;

    @PostMapping("/ask")
    public String ask(@RequestBody String question) throws JsonProcessingException {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        String requestBody = """
                {
                  "model": "llama-3.3-70b-versatile",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(question);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<String> entity =
                new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        System.out.println(requestBody);

        String response = restTemplate.postForObject(
                url,
                entity,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = mapper.readTree(response);

        return root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();


    }
}
package com.ai.research_assisant.service;

import com.ai.research_assisant.entity.ResearchReq;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {



    private final WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ResearchService(WebClient.Builder webClientBuilder){
        this.webClient=webClientBuilder.build();
    }

    public String processContent(ResearchReq req) {
        String prompt=buildPrompt(req);

        Map<String,Object> reqBody= Map.of("contents",new Object[]{
                Map.of("parts", new Object[]{
                    Map.of("text",prompt)
                })
        });

        String response= webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(reqBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return extractText(response);
    }

    private String extractText(String response) {
        try{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode rootNode=mapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts")
                    .get(0).path("text").asText();
        }catch (Exception e){
            throw new RuntimeException();
        }

    }

    private String buildPrompt(ResearchReq req) {
        StringBuilder prompt=new StringBuilder();
        switch (req.getOperation()){
            case "summarize":
                prompt.append("Provide a clean, meaningfull and concise summary of the entire content\n\n");
                break;
            case "suggest":
                prompt.append("based on the following content:Suggest some similar topics and similar content in a clear and consise manner\n\n");
                break;
            default:
                throw new IllegalArgumentException("Unknown Operation"+req.getOperation());
        }
        prompt.append(req.getContent());
        return String.valueOf(prompt);

    }
}

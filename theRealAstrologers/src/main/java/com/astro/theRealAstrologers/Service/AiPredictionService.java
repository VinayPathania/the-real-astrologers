package com.astro.theRealAstrologers.Service;

import com.astro.theRealAstrologers.Entity.PromptTemplate;
import com.astro.theRealAstrologers.Repository.PromptTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Slf4j
@Service
public class AiPredictionService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private PromptTemplateRepository promptRepository;

    public String generateReading(Map<String, String> chartData) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        PromptTemplate template = promptRepository.findByTemplateName("FULL_READING_JSON");
        String rawPromptText = (template != null) ? template.getTemplateText() : "Act as an astrologer. Read these planets: %s and return JSON.";
        String finalPrompt = String.format(rawPromptText, chartData.toString());

        // THE UPGRADE: Use Java Maps to build the JSON securely
        Map<String, Object> requestMap = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", finalPrompt)
                        })
                }
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {



            // Send the request
            try {
                ObjectMapper mapper = new ObjectMapper();
                String safeJsonPayload = mapper.writeValueAsString(requestMap);

                HttpEntity<String> request = new HttpEntity<>(safeJsonPayload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                JsonNode root = mapper.readTree(response.getBody());

                return root.path("candidates").get(0)
                        .path("content")
                        .path("parts").get(0)
                        .path("text").asText();

            } catch (RestClientResponseException e) {
                // 1. THIS CATCHES HTTP ERRORS (400, 401, 503, etc.)
                int statusCode = e.getStatusCode().value();

                // We can even grab the exact JSON error Google sent back!
                String apiErrorDetail = e.getResponseBodyAsString();

                if (statusCode == 503) {
                    log.warn("Gemini API is overloaded (503). It is safe to retry.");
                    return "The celestial alignment is currently shifting (High Demand). Please try again in a few moments.";

                } else if (statusCode == 400) {
                    log.error("Bad Request (400) sent to Gemini. Check your prompt JSON formatting! Details: {}", apiErrorDetail);
                    return "Our astrologers received a malformed chart. Please check the data format and try again.";

                } else if (statusCode == 401 || statusCode == 403) {
                    log.error("Authentication Error ({}). Is your API Key correct?", statusCode);
                    return "The gateway to the stars is locked. Please verify API credentials.";

                } else {
                    log.error("Unexpected API Error ({}). Details: {}", statusCode, apiErrorDetail);
                    return "The stars are currently clouded. We could not generate a reading at this time.";
                }
            }

        } catch (Exception e) {
            // 2. THIS CATCHES GENERAL JAVA ERRORS (like losing internet connection)
            log.error("Internal System Error while connecting to AI: {}", e.getMessage());
            return "An internal planetary misalignment occurred. Please try again later.";
        }
    }
}
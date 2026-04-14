package com.astro.theRealAstrologers.Service;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;

@Service
public class GeocodingService {

    public double[] getCoordinates(String cityName) {
        String formattedCity = cityName.replace(" ", "%20");
        String url = "https://nominatim.openstreetmap.org/search?q=" + formattedCity + "&format=json&limit=1";

        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();

            // 1. The Magic Fix: Pretend to be a real Google Chrome browser on Windows
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            // 2. Tell the API we specifically want JSON back
            headers.set("Accept", "application/json");
            // 3. Set a language so the API doesn't get confused
            headers.set("Accept-Language", "en-US,en;q=0.9");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode[].class
            );

            JsonNode[] body = response.getBody();

            if (body != null && body.length > 0) {
                double lat = body[0].get("lat").asDouble();
                double lon = body[0].get("lon").asDouble();
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.out.println("Error fetching coordinates for " + cityName + ": " + e.getMessage());
        }

        return null;
    }
}
package com.techcase.hubspot_integration.service;

import com.techcase.hubspot_integration.config.HubspotProperties;
import com.techcase.hubspot_integration.model.TokenStorage;
import com.techcase.hubspot_integration.model.dto.ContactRequest;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static io.github.resilience4j.ratelimiter.RateLimiter.decorateRunnable;

@Service
public class HubspotService {

    private final WebClient webClient;
    private final TokenStorage tokenStorage;
    private final RateLimiter rateLimiter;

    public HubspotService(WebClient.Builder webClientBuilder,
                          TokenStorage tokenStorage,
                          RateLimiterRegistry rateLimiterRegistry,
                          HubspotProperties hubspotProperties) {
        this.webClient = webClientBuilder.baseUrl(hubspotProperties.getApiUrl()).build();
        this.tokenStorage = tokenStorage;
        this.rateLimiter = rateLimiterRegistry.rateLimiter("hubspotContact");

    }

    public boolean createContact(ContactRequest request) {
        String token = tokenStorage.getAccessToken();

        Map<String, Object> contactPayload = Map.of(
                "properties", Map.of(
                        "email", request.getEmail(),
                        "firstname", request.getFirstName(),
                        "lastname", request.getLastName(),
                        "phone", request.getPhone()
                )
        );

        Runnable createContactCall = () -> {
            try {
                webClient.post()
                        .uri("/crm/v3/objects/contacts")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(contactPayload)
                        .retrieve()
                        .toBodilessEntity()
                        .block();
            } catch (WebClientResponseException ex) {
                System.out.println("Erro ao criar contato: " + ex.getResponseBodyAsString());
                throw ex;
            }
        };

        try {
            decorateRunnable(rateLimiter, createContactCall).run();
            return true;
        } catch (Exception ex) {
            System.out.println("RateLimiter bloqueou ou erro inesperado: " + ex.getMessage());
            return false;
        }
    }
}

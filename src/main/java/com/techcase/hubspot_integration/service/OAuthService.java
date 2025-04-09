package com.techcase.hubspot_integration.service;

import com.techcase.hubspot_integration.config.HubspotProperties;
import com.techcase.hubspot_integration.model.dto.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class OAuthService {

    private final HubspotProperties properties;
    private final WebClient webClient;

    public OAuthService(HubspotProperties properties) {
        this.properties = properties;
        this.webClient = WebClient.builder().build();
    }

    public String generateAuthorizationUrl() {
        return UriComponentsBuilder.fromHttpUrl(properties.getAuthorizationUrl())
                .queryParam("client_id", properties.getClientId())
                .queryParam("redirect_uri", properties.getRedirectUri())
                .queryParam("scope", properties.getScopes())
                .build()
                .toUriString();
    }

    public TokenResponse exchangeCodeForToken(String code) {
        return webClient.post()
                .uri(properties.getTokenUrl())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters
                        .fromFormData("grant_type", "authorization_code")
                        .with("client_id", properties.getClientId())
                        .with("client_secret", properties.getClientSecret())
                        .with("redirect_uri", properties.getRedirectUri())
                        .with("code", code))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .block();
    }
}

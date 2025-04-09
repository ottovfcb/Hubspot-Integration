package com.techcase.hubspot_integration.service;

import com.techcase.hubspot_integration.config.HubspotProperties;
import com.techcase.hubspot_integration.model.TokenStorage;
import com.techcase.hubspot_integration.model.dto.ContactRequest;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HubspotServiceTest {

    private HubspotService hubspotService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private TokenStorage tokenStorage;

    @Mock
    private RateLimiterRegistry rateLimiterRegistry;

    @Mock
    private RateLimiter rateLimiter;

    @Mock
    private HubspotProperties hubspotProperties;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(hubspotProperties.getApiUrl()).thenReturn("https://api.hubapi.com");

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        when(rateLimiterRegistry.rateLimiter("hubspotContact")).thenReturn(rateLimiter);

        hubspotService = new HubspotService(webClientBuilder, tokenStorage, rateLimiterRegistry, hubspotProperties);
    }

    @Test
    void createContact_tokenMissing() {
        when(tokenStorage.getAccessToken()).thenReturn(null);

        boolean result = hubspotService.createContact(new ContactRequest("test@example.com", "Test", "User", "12345678"));

        assertFalse(result);
        verify(webClient, never()).post();
    }

    @Test
    void createContact_rateLimiterBlocked() {
        when(tokenStorage.getAccessToken()).thenReturn("mock-token");

        doThrow(new RuntimeException("RateLimiter Error")).when(rateLimiter).acquirePermission();

        boolean result = hubspotService.createContact(new ContactRequest("test@example.com", "Test", "User", "12345678"));

        assertFalse(result);
        verify(webClient, never()).post();
    }
}
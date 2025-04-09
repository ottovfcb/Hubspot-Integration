package com.techcase.hubspot_integration.service;

import com.techcase.hubspot_integration.config.HubspotProperties;
import com.techcase.hubspot_integration.model.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class OAuthServiceTest {

    private OAuthService oAuthService;

    @Mock
    private HubspotProperties properties;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.getAuthorizationUrl()).thenReturn("https://example.com/oauth/authorize");
        when(properties.getTokenUrl()).thenReturn("https://example.com/oauth/v1/token");
        when(properties.getClientId()).thenReturn("fake-client-id");
        when(properties.getClientSecret()).thenReturn("fake-client-secret");
        when(properties.getRedirectUri()).thenReturn("http://localhost/callback");
        when(properties.getScopes()).thenReturn("contacts");

        oAuthService = new OAuthService(properties);
    }

    @Test
    void generateAuthorizationUrl_shouldReturnCorrectUrl() {
        String authorizationUrl = oAuthService.generateAuthorizationUrl();

        String expectedUrl = "https://example.com/oauth/authorize" +
                "?client_id=fake-client-id" +
                "&redirect_uri=http://localhost/callback" +
                "&scope=contacts";
        assertEquals(expectedUrl, authorizationUrl);
    }

    @Test
    void exchangeCodeForToken_shouldReturnTokenResponse() {
        TokenResponse mockTokenResponse = new TokenResponse("test-refresh-token", "test-token", 3600);

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq("https://example.com/oauth/v1/token"))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(eq("Content-Type"), eq(MediaType.APPLICATION_FORM_URLENCODED_VALUE))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(BodyInserters.FormInserter.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(TokenResponse.class)).thenReturn(Mono.just(mockTokenResponse));

        oAuthService = new OAuthService(properties) {
            @Override
            public TokenResponse exchangeCodeForToken(String code) {
                return webClient.post()
                        .uri(properties.getTokenUrl())
                        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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
        };

        String testCode = "test-code";
        TokenResponse tokenResponse = oAuthService.exchangeCodeForToken(testCode);

        assertNotNull(tokenResponse);
        assertEquals("test-token", tokenResponse.getAccessToken());
        assertEquals("test-refresh-token", tokenResponse.getRefreshToken());
        assertEquals(3600, tokenResponse.getExpiresIn());
    }
}
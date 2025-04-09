package com.techcase.hubspot_integration.controller;

import com.techcase.hubspot_integration.model.TokenStorage;
import com.techcase.hubspot_integration.model.dto.TokenResponse;
import com.techcase.hubspot_integration.service.OAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private final OAuthService oAuthService;
    private final TokenStorage tokenStorage;

    public OAuthController(OAuthService oAuthService, TokenStorage tokenStorage) {
        this.oAuthService = oAuthService;
        this.tokenStorage = tokenStorage;
    }

    @GetMapping("/authorize-url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl() {
        String url = oAuthService.generateAuthorizationUrl();
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<TokenResponse> callback(@RequestParam String code) {
        TokenResponse response = oAuthService.exchangeCodeForToken(code);
        tokenStorage.setAccessToken(response.getAccessToken());
        return ResponseEntity.ok(response);
    }
}

package com.techcase.hubspot_integration.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class TokenStorage {
    private String accessToken;

    public boolean hasToken() {
        return accessToken != null && !accessToken.isEmpty();
    }
    public void clearTokens() {
        this.accessToken = null;
    }
}

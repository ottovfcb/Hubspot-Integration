package com.techcase.hubspot_integration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hubspot")
@Getter
@Setter
public class HubspotProperties {
    private String clientId;
    private String redirectUri;
    private String scopes;
    private String authorizationUrl;
    private String clientSecret;
    private String tokenUrl;
    private String apiUrl;
}

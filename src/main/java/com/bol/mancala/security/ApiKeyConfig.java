package com.bol.mancala.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.apikey", ignoreUnknownFields = false)
@Getter
@Setter
public class ApiKeyConfig {
    private String value;
}

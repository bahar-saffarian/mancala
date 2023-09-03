package com.bol.mancala.security;


import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class ApiKeyAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private final ApiKeyConfig apiKeyConfig;

    public ApiKeyAuthenticationFilter(ApiKeyConfig apiKeyConfig) {
        this.apiKeyConfig = apiKeyConfig;
    }

    @Override
    protected Authentication getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        ApiKeyAuthentication apiKeyAuthentication = new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);

        if (apiKey != null && apiKey.equals(apiKeyConfig.getValue())) {
            return apiKeyAuthentication;
        }
        apiKeyAuthentication.setAuthenticated(false);
        return apiKeyAuthentication;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}

package de.thm.mcpmanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthChallengeEntryPoint implements AuthenticationEntryPoint {

    @Value("${spring.security.realm}")
    private String realm;

    @Value("${spring.subservices.mcp-management.public-url}")
    private String mcpManagement;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String resourceMetadata = mcpManagement + "/.well-known/oauth-protected-resource";

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String challenge = String.format(
                "Bearer realm=\"%s\", error=\"%s\", error_description=\"%s\", resource_metadata=\"%s\"",
                realm,
                "invalid_token",
                authException.getMessage(),
                resourceMetadata
        );
        response.setHeader("WWW-Authenticate", challenge);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", "
                + "\"message\": \"" + authException.getMessage() + "\"}");
    }
}

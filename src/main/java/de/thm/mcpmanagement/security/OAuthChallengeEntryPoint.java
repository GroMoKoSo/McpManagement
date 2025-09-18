package de.thm.mcpmanagement.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuthChallengeEntryPoint implements AuthenticationEntryPoint {

    private static final String REALM = "GroMoKoSo";
    private static final String RESOURCE_METADATA = "http://localhost:8080/.well-known/oauth-protected-resource";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String challenge = String.format(
                "Bearer realm=\"%s\", error=\"%s\", error_description=\"%s\", resource_metadata=\"%s\"",
                REALM,
                "invalid_token",
                authException.getMessage(),
                RESOURCE_METADATA
        );
        response.setHeader("WWW-Authenticate", challenge);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", "
                + "\"message\": \"" + authException.getMessage() + "\"}");
    }
}

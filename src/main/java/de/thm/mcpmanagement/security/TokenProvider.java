package de.thm.mcpmanagement.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.security.sasl.AuthenticationException;

@Component
public class TokenProvider {

    public String getToken() throws AuthenticationException {
        var token = SecurityContextHolder.getContext().getAuthentication();
        if (!(token instanceof JwtAuthenticationToken jwtToken)) {
            throw new AuthenticationException("JWT token is not valid");
        }
        var jwt = jwtToken.getToken();
        return jwt.getTokenValue();
    }
}

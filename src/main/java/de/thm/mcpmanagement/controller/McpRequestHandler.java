package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.service.McpServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Proxies mcp request to the mcp of the current user.
 * <p>
 * Uses the JWT access token to determine to which user this request belongs
 * and redirects the request to the correct mcp server instance.
 *
 * @author Josia Menger
 */
@Component
public class McpRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpRequestHandler.class);


    private final McpServerService mcpServerService;
    private final JwtDecoder jwtDecoder;


    public McpRequestHandler(McpServerService mcpServerService, JwtDecoder jwtDecoder) {
        this.mcpServerService = mcpServerService;
        this.jwtDecoder = jwtDecoder;
    }

    public ServerResponse handleRequest(ServerRequest request) {

        Jwt token = getToken(request);
        String username = token.getClaimAsString("preferred_username");

        try {
            return mcpServerService.getServerForUser(username).handle(request);
        } catch (Exception e) {
            logger.error("Error handling mcp request", e);
            return ServerResponse.status(500).build();
        }
    }

    private Jwt getToken(ServerRequest request) {
        String auth = request.headers().firstHeader("Authorization");
        if (auth == null) {
            logger.error("Missing Authorization header in mcp request");
            throw new AuthenticationCredentialsNotFoundException("Cannot find bearer token in mcp request");
        }
        return jwtDecoder.decode(auth.replace("Bearer ", ""));
    }
}
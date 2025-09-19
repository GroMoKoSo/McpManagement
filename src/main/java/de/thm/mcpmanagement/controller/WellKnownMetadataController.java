package de.thm.mcpmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
public class WellKnownMetadataController {

    @Value("${spring.server.base-url}")
    private String serverBaseUrl;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.server.mcp}")
    private String mcp;

    @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint}")
    private String streamableHttpMcpEndpoint;

    private final Logger logger = LoggerFactory.getLogger(WellKnownMetadataController.class);

    @GetMapping(".well-known/oauth-protected-resource")
    public Map<String, Object> getProtectedResourceMetadata() {
        String resourceUri = serverBaseUrl + mcp + streamableHttpMcpEndpoint;
        logger.debug("Resource URI: {}", resourceUri);
        logger.info("Authorization Server: {}", issuerUri);
        return Map.of(
                "resource", serverBaseUrl + mcp + streamableHttpMcpEndpoint,
                "authorization_servers", new String[]{issuerUri}
        );
    }
}
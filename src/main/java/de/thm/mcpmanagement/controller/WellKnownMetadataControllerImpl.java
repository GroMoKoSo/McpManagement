package de.thm.mcpmanagement.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController()
public class WellKnownMetadataControllerImpl implements WellKnownMetadataController {

    @Value("${spring.subservices.mcp-management.public-url}")
    private String mcpManagement;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint}")
    private String streamableHttpMcpEndpoint;

    private final Logger logger = LoggerFactory.getLogger(WellKnownMetadataControllerImpl.class);

    @Override
    public Map<String, Object> getProtectedResourceMetadata() {
        String resourceUri = mcpManagement + streamableHttpMcpEndpoint;
        logger.debug("Resource URI: {}", resourceUri);
        logger.info("Authorization Server: {}", issuerUri);
        return Map.of(
                "resource", mcpManagement + streamableHttpMcpEndpoint,
                "authorization_servers", new String[]{issuerUri}
        );
    }
}
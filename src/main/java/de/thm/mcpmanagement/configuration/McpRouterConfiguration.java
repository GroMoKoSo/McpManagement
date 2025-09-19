package de.thm.mcpmanagement.configuration;

import de.thm.mcpmanagement.controller.McpRequestHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

/**
 * Register a {@link RouterFunction<ServerResponse>} to act as a proxy for mcp requests.
 * The mcp requests are passed to a custom request handler
 * that selects the correct mcp server based on the current user.
 *
 * @author Josia Menger
 */
@Configuration
public class McpRouterConfiguration {

    @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint}")
    private String mcp_endpoint;

    @Bean
    public RouterFunction<ServerResponse> mcpRoutes(McpRequestHandler handler) {
        return RouterFunctions.route()
                .GET(mcp_endpoint, handler::handleRequest)
                .POST(mcp_endpoint, handler::handleRequest)
                .DELETE(mcp_endpoint, handler::handleRequest)
                .build();
    }
}
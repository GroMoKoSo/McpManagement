package de.thm.mcpmanagement.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebMvcSseServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class McpServerService {

    private static final Logger logger = LoggerFactory.getLogger(McpServerService.class);

    public Map<String, McpAsyncServer> getServers() {
        return servers;
    }

    public Map<String, WebMvcSseServerTransportProvider> getProviders() {
        return providers;
    }

    private final Map<String, McpAsyncServer> servers;
    private final Map<String, WebMvcSseServerTransportProvider> providers;

    public McpServerService() {
        servers = new HashMap<>();
        providers = new HashMap<>();

        WebMvcSseServerTransportProvider provider = new WebMvcSseServerTransportProvider(new ObjectMapper(),
                "/mcp/message", "/sse");

        McpSchema.ServerCapabilities capabilities = McpSchema.ServerCapabilities.builder()
                .tools(true)
                .logging()
                .build();

        McpAsyncServer server = McpServer.async(provider)
                .serverInfo("MCP Server of test", "v0.1")
                .capabilities(capabilities)
                .build();

        String inputSchema = """
                {
                    "type": "object",
                    "properties": {
                        "postId": {
                            "type": "integer",
                            "description": "The ID of post to retrieve"
                        }
                    },
                    "required": ["postId"],
                    "additionalProperties": false
                }
                """;

        Mono<Void> mono = server.addTool(getToolSpecification(
                "getPosts",
                "Get a detailed information about a single post using its id",
                inputSchema,
                McpServerService::invokeTool));
        mono.block();

        logger.info("Started server {}", server.getServerInfo());
        providers.put("test", provider);
        servers.put("test", server);
    }

    private McpServerFeatures.AsyncToolSpecification getToolSpecification(String name, String description, String inputSchema,
                                                                          Function<Object[], Object> toolFunction) {
        return new McpServerFeatures.AsyncToolSpecification(
                new McpSchema.Tool(
                        name,
                        description,
                        inputSchema
                ),
                (exchange, args) -> {
                    try {
                        // TODO: How to handle the args dynamically?
                        Object expr = args.get("postId");
                        String callResult = toolFunction.apply(new Object[]{expr}).toString();
                        return Mono.fromSupplier(() -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(callResult)), false));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        return Mono.fromSupplier(() ->new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(e.getMessage())), true));
                    }
                }
        );
    }

    public static Object invokeTool(Object... args) {
        logger.debug("Invoking Tool...");
        for (Object arg : args) {
            logger.info("arg: {}", arg);
        }
        logger.info("passed args: {}", args);
        assert args.length == 1 : "Expected exactly one argument, got: " + args.length;
        RestClient restClient = RestClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
        String result = restClient.get()
                .uri("/posts/{postId}", args[0])
                .retrieve()
                .body(String.class);
        logger.info("Result: {}", result);
        return result;
    }
}

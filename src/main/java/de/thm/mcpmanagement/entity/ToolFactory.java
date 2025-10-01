package de.thm.mcpmanagement.entity;

import de.thm.mcpmanagement.client.ApiManagementClient;
import de.thm.mcpmanagement.dto.InvokeApiDto;
import de.thm.mcpmanagement.dto.InvokeApiResponseDto;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * Factory class for creating async mcp tools.
 * <p>
 * Tools are the primary way for MCP servers to expose functionality to AI models.
 * Each tool represents a specific capability.
 * <p>
 * This class is a simple wrapper for the {@link McpServerFeatures} builder class
 * that allows to build mcp tools directly from {@link Tool} objects.
 * <p>
 * The class provides factory methods to create:
 * <ul>
 *     <li>{@link McpServerFeatures.AsyncToolSpecification} tool specifications
 *     that can be directly added to a {@link GroMoKoSoMcpServer}</li>
 * </ul>
 *
 * @author Josia Menger
 */
@Component
public class ToolFactory {

    private static final Logger logger = LoggerFactory.getLogger(ToolFactory.class);

    private final ApiManagementClient apiManagementClient;

    public ToolFactory(ApiManagementClient apiManagementClient) {
        this.apiManagementClient = apiManagementClient;
    }

    /**
     * Builds the AsyncToolSpecification instance.
     *
     * @param tool tool spec
     * @return a new AsyncToolSpecification instance
     * @throws IllegalArgumentException – if required fields are not set
     */
    public McpServerFeatures.AsyncToolSpecification create(Tool tool) {

        var mcpTool = McpSchema.Tool.builder()
                // we use getMcpName to ensure the name is always unique
                .name(tool.getMcpName())
                .description(tool.getDescription())
                .inputSchema(tool.getInputSchema())
                .build();

        return McpServerFeatures.AsyncToolSpecification.builder()
                .tool(mcpTool)
                .callHandler(((serverExchange, callToolRequest) -> {
                    try {
                        String callResult = invokeTool(tool, callToolRequest.arguments());
                        return Mono.fromSupplier(() -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(callResult)), false));
                    } catch (Exception e) {
                        logger.error("Tool {} with parameter {}: Execution failed {}",
                                callToolRequest.name(), callToolRequest.arguments(), e.getMessage());
                        return Mono.fromSupplier(() -> new McpSchema.CallToolResult(
                                List.of(new McpSchema.TextContent(e.getMessage())), true));
                    }
                }))
                .build();
    }

    private String invokeTool(Tool tool, Map<String, Object> args) {
        Object header = args.getOrDefault("header" ,Map.of());
        Object queryParameter = args.getOrDefault("query" ,Map.of());
        Object pathParameter = args.getOrDefault("path" ,Map.of());
        Object body = args.getOrDefault("body" ,Map.of());

        InvokeApiDto params = new InvokeApiDto(tool.getRequestMethod(), tool.getEndpoint(), header,
                body, queryParameter, pathParameter);

        InvokeApiResponseDto response = apiManagementClient.invokeApi(tool.getToolSet().getId(),
                tool.getToolSet().isGroupTool(), tool.getToolSet().getAccessVia(), params);

        if (!response.getHttpStatus().is2xxSuccessful()) {
            logger.warn("Tool invocation failed, external api returned error code {}: {}",
                    response.getHttpStatus(), response.body());
            throw new RuntimeException("Api invoke failed with status code %s: %s"
                    .formatted(response.responseCode(), response.body()));
        }

        logger.info("Tool invoke was successful! Returned {}: {}", response.getHttpStatus(), response.body());

       return response.body();
    }
}

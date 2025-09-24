package de.thm.mcpmanagement.entity;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ToolFactory {

    private static final Logger logger = LoggerFactory.getLogger(ToolFactory.class);

    private static final char HEADER_PREFIX = 'H';
    private static final char QUERY_PREFIX = 'Q';
    private static final char PATH_PREFIX = 'P';
    private static final char BODY_PREFIX = 'B';

   public McpServerFeatures.AsyncToolSpecification create(Tool tool) {

       var mcpTool = McpSchema.Tool.builder()
               .name(tool.getName())
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
                       return Mono.fromSupplier(() ->new McpSchema.CallToolResult(
                               List.of(new McpSchema.TextContent(e.getMessage())), true));
                   }
               }))
               .build();
    }

    private String invokeTool(Tool tool, Map<String, Object> args) {
        Map<String, String> header = new HashMap<>();
        Map<String, String> queryParameter = new HashMap<>();
        Map<String, String> pathParameter = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        for (Map.Entry<String, Object> entry : args.entrySet()) {
            var type = entry.getKey().charAt(0);
            var parameterKey = entry.getKey().substring(2);
            switch (type) {
                case HEADER_PREFIX -> header.put(parameterKey, entry.getValue().toString());
                case QUERY_PREFIX -> queryParameter.put(parameterKey, entry.getValue().toString());
                case PATH_PREFIX -> pathParameter.put(parameterKey, entry.getValue().toString());
                case BODY_PREFIX -> body.put(parameterKey, entry.getValue().toString());
            }
        }

        // TODO: Call ApiManagementClient.invokeApi

        System.out.println("Invoking tool: " + tool.getName() + " with args: " + args);
        return "Hello World!";
    }
}

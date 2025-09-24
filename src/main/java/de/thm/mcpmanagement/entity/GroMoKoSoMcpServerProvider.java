package de.thm.mcpmanagement.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import io.modelcontextprotocol.util.Assert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class for creating GroMoKoSo Model Context Protocol (MCP) servers.
 * The GroMoKoSo server relies on the {@link McpAsyncServer} implementation form spring-ai
 * <p>
 * The class provides factory methods to create:
 * <ul>
 *     <li>{@link GroMoKoSoMcpServer} for non-blocking operations with reactive responses</li>
 * </ul>
 *
 * @author Josia Menger
 */
@Component
public class GroMoKoSoMcpServerProvider {

    private final ToolFactory toolFactory;
    private final ToolSetRepository toolSetRepository;
    private final String mcpEndpoint;

    public GroMoKoSoMcpServerProvider(ToolFactory toolFactory, ToolSetRepository toolSetRepository,
                  @Value("${spring.ai.mcp.server.streamable-http.mcp-endpoint}") String mcpEndpoint) {
        this.toolFactory = toolFactory;
        this.toolSetRepository = toolSetRepository;
        this.mcpEndpoint = mcpEndpoint;
    }

    /**
     * Starts building an asynchronous MCP server that provides non-blocking operations.
     * Asynchronous servers can handle multiple requests concurrently on a single Thread
     * using a functional paradigm with non-blocking server transports, making them more
     * scalable for high-concurrency scenarios but more complex to implement.
     * <p>
     * The configuration process is heavily based on the spring-ai McpAsyncServer configuration.
     * @return A new instance of {@link GroMoKoSoMcpServerSpecification} for configuring the server.
     */
    public GroMoKoSoMcpServerSpecification builder() {
        return new GroMoKoSoMcpServerSpecification();
    }

    public class GroMoKoSoMcpServerSpecification {

        private String name;
        private String version;
        private final List<McpServerFeatures.AsyncToolSpecification> tools;
        private final Map<Integer, List<String>> apiIdToToolName;

        private GroMoKoSoMcpServerSpecification() {
            tools = new ArrayList<>();
            apiIdToToolName = new HashMap<>();
        }

        /**
         * Sets the server implementation information using name and version strings.
         *
         * @param name The server name. Must not be null or empty.
         * @param version The server version. Must not be null or empty.
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if name or version is null or empty
         */
        public GroMoKoSoMcpServerSpecification serverInfo(String name, String version) {
            Assert.hasText(name, "Name must not be null or empty");
            Assert.hasText(version, "Version must not be null or empty");
            this.version = version;
            this.name = name;
            return this;
        }

        /**
         * Adds all tools from a tool set to the server.
         * This is a convenience method for registering multiple tools.
         * @param toolSet Toolset to add. All fields must not be null.
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if toolset is null
         */
        public GroMoKoSoMcpServerSpecification addToolSet(ToolSet toolSet) {
            Assert.notNull(toolSet, "ToolSet must not be null");
            toolSet.getTools().forEach(this::addTool);
            return this;
        }

        /**
         * Adds a single tool with its implementation handler to the server.
         * @param tool Tool to add. All fields must not be null.
         * @return This builder instance for method chaining
         * @throws IllegalArgumentException if tool is null
         */
        public GroMoKoSoMcpServerSpecification addTool(Tool tool) {
            Assert.notNull(tool, "Tool must not be null");

            tools.add(toolFactory.create(tool));
            List<String> toolNames = apiIdToToolName.computeIfAbsent(tool.getId(), k -> new ArrayList<>());
            toolNames.add(tool.getName());
            return this;
        }

        /**
         * Builds an asynchronous GroMoKoSo MCP server that provides non-blocking operations.
         * @return A new instance of {@link GroMoKoSoMcpServer} configured with this builder's settings.
         */
        public GroMoKoSoMcpServer build() {

            WebMvcStreamableServerTransportProvider provider = new WebMvcStreamableServerTransportProvider.Builder()
                    .objectMapper(new ObjectMapper())
                    .mcpEndpoint(mcpEndpoint)
                    .build();

            McpSchema.ServerCapabilities capabilities = McpSchema.ServerCapabilities.builder()
                    .tools(true)
                    .logging()
                    .build();

            McpAsyncServer server = McpServer.async(provider)
                    .serverInfo(name, version)
                    .capabilities(capabilities)
                    .tools(tools)
                    .build();

            return new GroMoKoSoMcpServer(server, provider.getRouterFunction(), apiIdToToolName,
                    toolFactory, toolSetRepository);
        }

    }
}

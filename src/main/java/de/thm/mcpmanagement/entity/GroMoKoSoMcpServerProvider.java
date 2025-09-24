package de.thm.mcpmanagement.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import io.modelcontextprotocol.server.McpAsyncServer;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.transport.WebMvcStreamableServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        public GroMoKoSoMcpServerSpecification name(String name) {
            if (name == null || name.isEmpty())
                throw new IllegalArgumentException("Name must not be null or empty");
            this.name = name;
            return this;
        }

        public GroMoKoSoMcpServerSpecification version(String version) {
            if (version == null || version.isEmpty())
                throw new IllegalArgumentException("Version must not be null or empty");
            this.version = version;
            return this;
        }

        public GroMoKoSoMcpServerSpecification addToolSet(ToolSet toolSet) {
            if (toolSet == null)
                throw new IllegalArgumentException("ToolSet must not be null");
            toolSet.getTools().forEach(this::addTool);
            return this;
        }

        public GroMoKoSoMcpServerSpecification addTool(Tool tool) {
            if (tool == null)
                throw new IllegalArgumentException("Tool must not be null");

            tools.add(toolFactory.create(tool));
            List<String> toolNames = apiIdToToolName.computeIfAbsent(tool.getId(), k -> new ArrayList<>());
            toolNames.add(tool.getName());
            return this;
        }

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

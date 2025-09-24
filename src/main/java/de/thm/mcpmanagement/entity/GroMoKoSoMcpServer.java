package de.thm.mcpmanagement.entity;

import de.thm.mcpmanagement.repository.ToolSetRepository;
import io.modelcontextprotocol.server.McpAsyncServer;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("ReactiveStreamsUnusedPublisher")
public class GroMoKoSoMcpServer {

    private static final Logger logger = LoggerFactory.getLogger(GroMoKoSoMcpServer.class);

    private final McpAsyncServer server;
    private final RouterFunction<ServerResponse> router;
    private final Map<Integer, List<String>> apiIdToToolSet;
    private final ToolFactory toolFactory;
    private final ToolSetRepository toolSetRepository;

    public GroMoKoSoMcpServer(McpAsyncServer server,
                              RouterFunction<ServerResponse> router,
                              Map<Integer, List<String>> apiIdToToolSet,
                              ToolFactory toolFactory,
                              ToolSetRepository toolSetRepository) {
        this.server = server;
        this.router = router;
        this.apiIdToToolSet = apiIdToToolSet;
        this.toolFactory = toolFactory;
        this.toolSetRepository = toolSetRepository;
    }

    public ServerResponse handle(ServerRequest request) {
        Optional<HandlerFunction<ServerResponse>> response = router.route(request);
        if (response.isPresent()) {
            try {
                return response.get().handle(request);
            } catch (Exception ex) {
                logger.error("Error handling request", ex);
                return ServerResponse.status(500).build();
            }
        }
        logger.warn("No matching route found for {}", request.path());
        return ServerResponse.notFound().build();
    }

    public void close() {
        server.close();
    }

    public void updateToolSetList(List<Integer> apiIds) {
        List<Integer> toolSetsToRemove = new ArrayList<>(apiIdToToolSet.keySet());
        toolSetsToRemove.removeAll(apiIds);
        for (int apiId : toolSetsToRemove) removeToolSet(apiId);

        for (int apiId : apiIds) {
            if (apiIdToToolSet.containsKey(apiId)) continue;
            addToolSet(apiId);
        }
    }

    public void updateToolSet(int apiId, ToolSet newToolSet, ToolSet oldToolSet) {
        // TODO: Check if tool list for set has changed
        List<String> toolsToRemove = new ArrayList<>(apiIdToToolSet.get(apiId));
        List<Tool> newTools = newToolSet.getTools();
        List<Tool> oldTools = oldToolSet.getTools();
        for (Tool newTool : newTools) {
            assert newTool != null;
            toolsToRemove.remove(newTool);

            Tool oldTool = oldTools.stream()
                    .filter(t -> t.getName().equals(newTool.getName()))
                    .findFirst()
                    .orElse(null);

            // The new toolset has a tool that is not included in the old toolset
            if (oldTool == null) {
                server.addTool(toolFactory.create(newTool));
                continue;
            }

            // The tool is already included and has not changed
            if (oldTool.equals(newTool)) {
                continue;
            }

            // The tool is already included and has changed
            server.removeTool(oldTool.getName());
            server.addTool(toolFactory.create(newTool));
        }

        for (String toolName : toolsToRemove) server.removeTool(toolName);
        apiIdToToolSet.put(apiId, newToolSet.getTools().stream().map(Tool::getName).toList());
    }

    private void addToolSet(int apiId) {
        List<String> toolNames = new ArrayList<>();

        ToolSet toolSet = toolSetRepository.findById(apiId).orElseThrow(() ->
                new EntityNotFoundException("Toolset with id " + apiId + " not found"));

        for (Tool tool : toolSet.tools) {
            toolNames.add(tool.getName());
            server.addTool(toolFactory.create(tool));
        }

        apiIdToToolSet.put(apiId, toolNames);
    }

    private void removeToolSet(int apiId) {
        for (String toolName : apiIdToToolSet.get(apiId)) {
            server.removeTool(toolName);
        }
        apiIdToToolSet.remove(apiId);
    }
}

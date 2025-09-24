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

/**
 * This class represents a mcp server from a specific user.
 * <p>
 * It wraps the spring-ai implementation of the {@link McpAsyncServer} to get more control over the server.
 * The implementation allows to route mcp request ourselves and update tools/toolsets on runtime.
 * <p>
 * Use the {@link GroMoKoSoMcpServerProvider} to get an instance of this class.
 *
 * @author Josia Menger
 */
@SuppressWarnings("ReactiveStreamsUnusedPublisher")
public class GroMoKoSoMcpServer {

    private static final Logger logger = LoggerFactory.getLogger(GroMoKoSoMcpServer.class);

    private final McpAsyncServer server;
    private final RouterFunction<ServerResponse> router;
    private final Map<Integer, List<String>> apiIdToToolSet;
    private final ToolFactory toolFactory;
    private final ToolSetRepository toolSetRepository;

    /**
     * <b>ONLY FOR INTERNAL USE!</b>
     * <p>
     * To get an instance use {@link GroMoKoSoMcpServerProvider}
     *
     * @param server instance of the underlying spring-ai mcp server
     * @param router router function that is backed into the mcp server
     * @param apiIdToToolSet map from apiId to the tool names from the current tools of the server
     * @param toolFactory instance of {@link ToolFactory}
     * @param toolSetRepository instance of {@link ToolSetRepository}
     */
    GroMoKoSoMcpServer(McpAsyncServer server,
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

    /**
     * Handle a mcp request and return the response.
     * @param request mcp request
     * @return mcp response
     */
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

    /**
     * Close the mcp server. This closes all open connections to mcp clients.
     */
    public void close() {
        server.close();
    }

    /**
     * Update tool sets of the server.
     * <p>
     * <b>IMPORTANT</b>:
     * This method only checks if the complete set has changed (added/ removed).
     * If you want to check if the spec of the tool set has changed (individual tools from the set),
     * use {@link #updateToolSet(int, ToolSet, ToolSet)}!
     *
     * @param apiIds All tool sets that should be included in the server
     */
    public void updateToolSetList(List<Integer> apiIds) {
        List<Integer> toolSetsToRemove = new ArrayList<>(apiIdToToolSet.keySet());
        toolSetsToRemove.removeAll(apiIds);
        for (int apiId : toolSetsToRemove) removeToolSet(apiId);

        for (int apiId : apiIds) {
            if (apiIdToToolSet.containsKey(apiId)) continue;
            addToolSet(apiId);
        }
    }

    /**
     * Update tools from a specific tool set from the server.
     * <p>
     * This method check for changes in every tool in the tool set and updates tools (added, removed, changed)
     * If you want to completely add or remove a tool set,
     * you should use the more efficient method {@link #updateToolSetList(List)}
     * @param apiId id of the tool set
     * @param newToolSet new tool set
     * @param oldToolSet old tool set
     */
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

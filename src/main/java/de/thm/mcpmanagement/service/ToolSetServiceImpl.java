package de.thm.mcpmanagement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.thm.mcpmanagement.client.UserManagementClient;
import de.thm.mcpmanagement.dto.ToolDto;
import de.thm.mcpmanagement.dto.ToolSetDto;
import de.thm.mcpmanagement.entity.Tool;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import de.thm.mcpmanagement.service.exception.ServiceExceptionHandler;
import de.thm.mcpmanagement.service.exception.ServiceNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToolSetServiceImpl implements ToolSetService {

    private final static Logger logger = LoggerFactory.getLogger(ToolSetServiceImpl.class);

    private final ToolSetRepository toolSetRepository;
    private final McpServerService mcpServerService;
    private final ObjectMapper objectMapper;
    private final UserManagementClient userManagementClient;

    public ToolSetServiceImpl(ToolSetRepository toolSetRepository,
                              McpServerService mcpServerService,
                              ObjectMapper objectMapper,
                              UserManagementClient userManagementClient) {
        this.toolSetRepository = toolSetRepository;
        this.mcpServerService = mcpServerService;
        this.objectMapper = objectMapper;
        this.userManagementClient = userManagementClient;
    }

    @Override
    public List<ToolSet> getToolSets(String username) {
        try {
            return toolSetRepository.findByIdIn(userManagementClient.getApisFromUser(username, null));
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }

    @Override
    public ToolSet getToolSets(int toolId) {
        try {
            return toolSetRepository.findById(toolId).orElseThrow();
        } catch (NullPointerException ne) {
            throw new ServiceNotFound("Tool set with id " + toolId + " not found", ne);
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }

    @Override
    public boolean putToolSet(int apiId, @NonNull ToolSetDto toolSpecification, String username) {
        try {
            String accessVia = userManagementClient.getApiOrigin(username, apiId);
            if (accessVia == null) throw new NullPointerException("Cannot retrieve origin of api " + apiId);
            boolean isGroupTool = !accessVia.equals("user");
            accessVia = isGroupTool ? accessVia : username;

            ToolSet newSet = new ToolSet(apiId, toolSpecification.name(), toolSpecification.description(),
                    accessVia, isGroupTool);
            for (ToolDto toolDto : toolSpecification.tools()) {
                String schema = objectMapper.writeValueAsString(toolDto.inputSchema());

                newSet.addTool(new Tool(toolDto.name(), toolDto.description(), toolDto.requestMethod(),
                        toolDto.endpoint(), schema));
            }
            ToolSet oldSet = toolSetRepository.findById(apiId).orElse(null);
            if (oldSet != null) oldSet = oldSet.deepCopy();
            newSet = toolSetRepository.save(newSet);
            if (oldSet != null && mcpServerService.isServerForUserRunning(username))
                mcpServerService.getServerForUser(username).updateToolSet(apiId, newSet, oldSet);
            return oldSet == null;
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }

    @Override
    public void deleteToolSet(int toolId) {
        if (!toolSetRepository.existsById(toolId)) {
            throw new ServiceNotFound("Tool with id " + toolId + " does not exist");
        }
        try {
            toolSetRepository.deleteById(toolId);
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }

    @Override
    public void updateToolSetList(String username, List<Integer> newApiIdList) {
        logger.info("Updating tool set list for user {}", username);
        try {
            if (mcpServerService.isServerForUserRunning(username))
                mcpServerService.getServerForUser(username).updateToolSetList(newApiIdList);
        } catch (Exception e) {
            throw ServiceExceptionHandler.handleException(e);
        }
    }
}

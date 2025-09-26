package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.dto.ToolDto;
import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.Tool;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ToolSetServiceImpl implements ToolSetService {

    private final static Logger logger = LoggerFactory.getLogger(ToolSetServiceImpl.class);

    private final ToolSetRepository toolSetRepository;
    private final McpServerService mcpServerService;

    public ToolSetServiceImpl(ToolSetRepository toolSetRepository, McpServerService mcpServerService) {
        this.toolSetRepository = toolSetRepository;
        this.mcpServerService = mcpServerService;
    }

    @Override
    public ToolSet[] getToolSets() {
        ArrayList<ToolSet> toolSets = new ArrayList<>();
        toolSetRepository.findAll().forEach(toolSets::add);
        return toolSets.toArray(new ToolSet[0]);
    }

    @Override
    public ToolSet getToolSets(int toolId) {
        return toolSetRepository.findById(toolId).orElseThrow(() -> new NoSuchElementException("Tool with id " + toolId + " does not exist"));
    }

    @Override
    public boolean putToolSet(int apiId, @NonNull ToolSpecificationDto toolSpecification, String username) {
        ToolSet newSet = new ToolSet(apiId, toolSpecification.name(), toolSpecification.description());
        for (ToolDto toolDto : toolSpecification.tools()) {
            newSet.addTool(new Tool(toolDto.title(), toolDto.description(), toolDto.requestMethod(),
                    toolDto.endpoint(), toolDto.inputSchema()));
        }
        ToolSet oldSet = toolSetRepository.findById(apiId).orElse(null);
        toolSetRepository.save(newSet);
        if (oldSet != null) mcpServerService.getServerForUser(username).updateToolSet(apiId, newSet, oldSet);
        return oldSet == null;
    }

    @Override
    public void deleteToolSet(int toolId) {
        if (!toolSetRepository.existsById(toolId)) {
            throw new NoSuchElementException("Tool with id " + toolId + " does not exist");
        }
        toolSetRepository.deleteById(toolId);
    }

    @Override
    public void updateToolSetList(String userId, List<Integer> newApiIdList) {
        logger.info("Updating tool set list for user {}", userId);
        mcpServerService.getServerForUser(userId).updateToolSetList(newApiIdList);
    }
}

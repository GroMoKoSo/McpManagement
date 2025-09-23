package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.dto.ToolDto;
import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.Tool;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.repository.ToolRepository;
import de.thm.mcpmanagement.repository.ToolSetRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.NoSuchElementException;

@Service
public class ToolSetServiceImpl implements ToolSetService {

    private final ToolRepository toolRepository;
    private final ToolSetRepository toolSetRepository;

    public ToolSetServiceImpl(ToolRepository toolRepository, ToolSetRepository toolSetRepository) {
        this.toolRepository = toolRepository;
        this.toolSetRepository = toolSetRepository;
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
    public boolean putToolSet(int apiId, @NonNull ToolSpecificationDto toolSpecification) {
        ToolSet toolSet = new ToolSet(apiId, toolSpecification.name(), toolSpecification.description());
        for (ToolDto toolDto : toolSpecification.tools()) {
            toolSet.addTool(new Tool(toolDto.title(), toolDto.description(), toolDto.requestMethod(),
                    toolDto.endpoint(), toolDto.inputSchema()));
        }
        boolean isNew = !toolSetRepository.existsById(apiId);
        toolSetRepository.save(toolSet);
        return isNew;
    }

    @Override
    public void deleteToolSet(int toolId) {
        if (!toolRepository.existsById(toolId)) {
            throw new NoSuchElementException("Tool with id " + toolId + " does not exist");
        }
        toolSetRepository.deleteById(toolId);
    }

    @Override
    public void updateToolSetList(String userId) {
        // TODO: Implement tool update
        System.out.println("Tool list for user " + userId + " updated");
    }
}

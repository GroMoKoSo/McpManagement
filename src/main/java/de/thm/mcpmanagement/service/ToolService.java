package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.entity.ToolSet;
import dto.ToolSpecificationDto;

public interface ToolService {

    ToolSet[] getTools();

    ToolSet getTool(int toolId);

    boolean putTool(int apiId, ToolSpecificationDto toolSpecification);

    void deleteTool(int toolId);

    void updateToolList(String userId);
}

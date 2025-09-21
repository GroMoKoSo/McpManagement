package de.thm.mcpmanagement.service;

import dto.ToolSpecificationDto;

public interface ToolService {

    boolean putTool(int apiId, ToolSpecificationDto toolSpecification);

    void deleteTool(int toolId);

    void updateToolList(String userId);
}

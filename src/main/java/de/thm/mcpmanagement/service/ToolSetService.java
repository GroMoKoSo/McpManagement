package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.ToolSet;

public interface ToolSetService {

    ToolSet[] getToolSets();

    ToolSet getToolSets(int toolId);

    boolean putToolSet(int apiId, ToolSpecificationDto toolSpecification);

    void deleteToolSet(int toolId);

    void updateToolSetList(String userId);
}

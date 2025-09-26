package de.thm.mcpmanagement.service;

import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.ToolSet;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ToolSetService {

    ToolSet[] getToolSets();

    ToolSet getToolSets(int toolId);

    boolean putToolSet(int apiId, @NonNull ToolSpecificationDto toolSpecification, String username);

    void deleteToolSet(int toolId);

    void updateToolSetList(String userId, List<Integer> newApiIdList);
}

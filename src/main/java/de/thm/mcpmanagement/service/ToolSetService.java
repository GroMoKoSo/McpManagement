package de.thm.mcpmanagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.thm.mcpmanagement.dto.ToolSetDto;
import de.thm.mcpmanagement.entity.ToolSet;
import org.springframework.lang.NonNull;

import java.util.List;

public interface ToolSetService {

    List<ToolSet> getToolSets(String username);

    ToolSet getToolSets(int toolId);

    boolean putToolSet(int apiId, @NonNull ToolSetDto toolSpecification, String username);
    void deleteToolSet(int toolId);

    void updateToolSetList(String userId, List<Integer> newApiIdList);
}

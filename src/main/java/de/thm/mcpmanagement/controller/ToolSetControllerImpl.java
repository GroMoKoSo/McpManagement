package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.ToolSet;
import de.thm.mcpmanagement.service.ToolSetService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@RestController
public class ToolSetControllerImpl implements ToolSetController {

    private final ToolSetService toolSetService;

    public ToolSetControllerImpl(ToolSetService toolSetService) {
        this.toolSetService = toolSetService;
    }

    @Override
    public ToolSet[] getToolSets() {
        return toolSetService.getToolSets();
    }

    @Override
    public ToolSet getToolSet(int id) {
        return toolSetService.getToolSets(id);
    }

    @Override
    public void putToolSet(int id, ToolSpecificationDto toolSpecification, HttpServletResponse response) {
        if (toolSetService.putToolSet(id, toolSpecification)) response.setStatus(HttpStatus.CREATED.value());
    }

    @Override
    public void deleteToolSet(int id) {
        try {
            toolSetService.deleteToolSet(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    public void updateToolSetList(String id) {
        toolSetService.updateToolSetList(id);
    }
}

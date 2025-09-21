package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.service.ToolService;
import dto.ToolSpecificationDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.NoSuchElementException;

@Controller
public class ToolControllerImpl implements ToolController {

    private final ToolService toolService;

    public ToolControllerImpl(ToolService toolService) {
        this.toolService = toolService;
    }

    @Override
    public void putTool(int id, ToolSpecificationDto toolSpecification, HttpServletResponse response) {
        if (toolService.putTool(id, toolSpecification)) response.setStatus(HttpStatus.CREATED.value());
    }

    @Override
    public void deleteTool(int id) {
        try {
            toolService.deleteTool(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @Override
    public void updateToolList(String id) {
        toolService.updateToolList(id);
    }
}

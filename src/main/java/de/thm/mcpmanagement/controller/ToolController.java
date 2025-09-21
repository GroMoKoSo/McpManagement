package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.entity.ToolSet;
import dto.ToolSpecificationDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface ToolController {

    @GetMapping("/tools")
    ToolSet[] getTools();

    @GetMapping("/tools/{id}")
    ToolSet getTool(@PathVariable(name = "id") int id);

    @PutMapping("/tools/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void putTool(@PathVariable(name = "id") int id,
                 @RequestBody ToolSpecificationDto toolSpecification,
                 HttpServletResponse response);

    @DeleteMapping("/tools/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTool(@PathVariable(name = "id") int id);

    @PostMapping("/users/{id}/tools/list-changed]")
    void updateToolList(@PathVariable(name = "id") String id);
}

package de.thm.mcpmanagement.controller;

import dto.ToolSpecificationDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface ToolController {

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

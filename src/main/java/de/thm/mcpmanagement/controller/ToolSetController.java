package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.entity.ToolSet;
import dto.ToolSpecificationDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface ToolSetController {

    @GetMapping("/toolsets")
    ToolSet[] getToolSets();

    @GetMapping("/toolsets/{id}")
    ToolSet getToolSet(@PathVariable(name = "id") int id);

    @PutMapping("/toolsets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void putToolSet(@PathVariable(name = "id") int id,
                    @RequestBody ToolSpecificationDto toolSpecification,
                    HttpServletResponse response);

    @DeleteMapping("/toolsets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteToolSet(@PathVariable(name = "id") int id);

    @PostMapping("/users/{id}/toolsets/list-changed]")
    void updateToolSetList(@PathVariable(name = "id") String id);
}

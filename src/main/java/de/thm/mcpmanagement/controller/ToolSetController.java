package de.thm.mcpmanagement.controller;

import de.thm.mcpmanagement.dto.ToolSpecificationDto;
import de.thm.mcpmanagement.entity.ToolSet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ToolSet Management", description = "Endpoints for managing tool sets.")
public interface ToolSetController {

    @Operation(summary = "Get all tool sets", description = "Retrieve a list of all available tool sets.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    @GetMapping("/toolsets")
    ToolSet[] getToolSets();

    @Operation(summary = "Get a tool set by ID", description = "Retrieve a single tool set using its unique ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved tool set")
    @ApiResponse(responseCode = "404", description = "Tool set not found")
    @GetMapping("/toolsets/{id}")
    ToolSet getToolSet(@Parameter(description = "ID of the tool set to retrieve")
                       @PathVariable(name = "id") int id);

    @Operation(summary = "Update a tool set", description = "Update an existing tool set with new specifications or create a new tool set.")
    @ApiResponse(responseCode = "202", description = "A new tool set was created")
    @ApiResponse(responseCode = "200", description = "Tool set successfully updated")
    @PutMapping("/toolsets/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<ToolSpecificationDto> putToolSet(@Parameter(description = "ID of the tool set to update")
                    @PathVariable(name = "id") int id,
                                                    @Valid @RequestBody ToolSpecificationDto toolSpecification,
                                                    HttpServletResponse response);

    @Operation(summary = "Delete a tool set", description = "Delete a tool set by its ID.")
    @ApiResponse(responseCode = "204", description = "Tool set successfully deleted")
    @ApiResponse(responseCode = "404", description = "Tool set not found")
    @DeleteMapping("/toolsets/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteToolSet(@Parameter(description = "ID of the tool set to delete")
                       @PathVariable(name = "id") int id);

    @Operation(summary = "Notify a user's tool set list has changed", description = "Signal that a specific user's tool set list has been updated. The body MUST contain a list of all api ids that should be available to the user")
    @ApiResponse(responseCode = "200", description = "Notification successful")
    @PostMapping("/users/{id}/toolsets/list-changed")
    void updateToolSetList(@Parameter(description = "ID of the user whose tool set list has changed")
                           @PathVariable(name = "id") String id, @Valid @RequestBody List<Integer> apis);
}
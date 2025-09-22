package de.thm.mcpmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ToolSpecificationDto(
        @NotNull
        @Schema(description = "Unique identifier for the tool set")
        String name,
        @NotNull
        @Schema(description = "Human-readable description of the tool set")
        String description,
        @NotNull
        @Schema(description = "List of tools to include in the tool set. One tool per API endpoint")
        ToolDto[] tools) {
}

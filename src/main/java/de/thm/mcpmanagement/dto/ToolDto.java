package de.thm.mcpmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record ToolDto(
        @NotEmpty
        @Schema(description = "Unique identifier for the tool")
        String title,
        @NotEmpty
        @Schema(description = "Human-readable description of functionality")
        String description,
        @NotEmpty
        @Schema(description = "HTTP request method")
        String requestMethod,
        @NotEmpty
        @Schema(description = "Endpoint URL of the API")
        String endpoint,
        @NotEmpty
        @Schema(description = "JSON Schema defining expected parameters")
        String inputSchema) {
}

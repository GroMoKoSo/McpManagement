package de.thm.mcpmanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ToolDto(
        @NotNull
        @Schema(description = "Unique identifier for the tool")
        String title,
        @NotNull
        @Schema(description = "Human-readable description of functionality")
        String description,
        @NotNull
        @Schema(description = "HTTP request method")
        String requestMethod,
        @NotNull
        @Schema(description = "Endpoint URL of the API")
        String endpoint,
        @NotNull
        @Schema(description = "JSON Schema defining expected parameters")
        String inputSchema) {
}

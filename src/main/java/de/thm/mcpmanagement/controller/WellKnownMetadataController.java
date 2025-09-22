package de.thm.mcpmanagement.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Tag(name = "Metadata", description = "Endpoints for retrieving metadata for protected resources.")
public interface WellKnownMetadataController {

    @ApiResponse(responseCode = "200", description = "Retrieve metadata for protected resources. Used for the mcp endpoint  ")
    @GetMapping(".well-known/oauth-protected-resource")
    Map<String, Object> getProtectedResourceMetadata();
}

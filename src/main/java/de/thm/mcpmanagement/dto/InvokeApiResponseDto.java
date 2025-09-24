package de.thm.mcpmanagement.dto;

import java.util.Map;

public record InvokeApiResponseDto(
        String responseCode,
        Map<String, String> headers,
        String body
) { }

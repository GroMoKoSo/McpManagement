package de.thm.mcpmanagement.dto;

import java.util.Map;

public record InvokeApiDto(String requestType,
                           String requestPath,
                           Map<String, String> headers,
                           String body,
                           Map<String, String> requestParameter,
                           Map<String, String> pathParameter) { }

